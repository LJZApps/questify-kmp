package de.ljz.questify.core.di

import de.ljz.questify.core.data.remote.QuestifyRemoteDataSource
import de.ljz.questify.core.domain.repositories.auth.AuthConfig
import de.ljz.questify.core.domain.repositories.auth.AuthRepository
import de.ljz.questify.core.domain.repositories.auth.AuthRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
    }

    single(named("base_client")) {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
        }
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            dataStore = get(named("auth_tokens")),
            httpClient = get(named("base_client"))
        )
    }

    single { QuestifyRemoteDataSource(get(named("auth_client"))) }

    single(named("auth_client")) {
        val authRepository = get<AuthRepository>()
        
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }

            install(DefaultRequest) {
                url(AuthConfig.QUESTIFY_API_BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val tokens = authRepository.authTokens.first()
                        if (tokens.questifyToken != null) {
                            BearerTokens(tokens.questifyToken, "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        authRepository.logout()
                        null
                    }
                }
            }
        }
    }
}
