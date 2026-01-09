package de.ljz.questify.core.di

import de.ljz.questify.core.data.remote.QuestifyRemoteDataSource
import de.ljz.questify.core.domain.repositories.auth.AuthConfig
import de.ljz.questify.core.domain.repositories.auth.AuthRepository
import de.ljz.questify.core.domain.repositories.auth.AuthRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    // Basis-Json Konfiguration
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }

    // HttpClient ohne Auth (für Token-Requests)
    single(named("base_client")) {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
        }
    }

    // AuthRepository
    single<AuthRepository> {
        AuthRepositoryImpl(
            dataStore = get(named("auth_tokens")),
            httpClient = get(named("base_client"))
        )
    }

    // Remote Data Source
    single { QuestifyRemoteDataSource(get(named("auth_client"))) }

    // Authentifizierter HttpClient mit Token-Rotation
    single(named("auth_client")) {
        val authRepository = get<AuthRepository>()
        
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }

            install(DefaultRequest) {
                url(AuthConfig.QUESTIFY_API_BASE_URL)
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
                        // Bei 401 (Unauthorized) wird dieser Block aufgerufen.
                        // Laut Spec soll der Token verworfen werden.
                        authRepository.logout()
                        null
                    }
                }
            }
        }
    }
}
