package de.ljz.questify.core.data.network

import de.ljz.questify.core.auth.TokenStorage
import de.ljz.questify.core.domain.repositories.AuthRepository
import de.ljz.questify.core.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    // OMRIX client
    fun createAuthHttpClient(): HttpClient {
        println("HttpClientFactory: Erstelle Auth HttpClient")
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("QuestifyHttpAuth: $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }

    fun createAppHttpClient(
        tokenStorage: TokenStorage,
        authRepository: AuthRepository
    ): HttpClient {
        println("HttpClientFactory: Erstelle App HttpClient")
        return HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; prettyPrint = true })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("QuestifyHttpApp: $message")
                    }
                }
                level = LogLevel.ALL
            }

            defaultRequest {
                url(Constants.QUESTIFY_API_URL)
                contentType(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        println("HttpClientAuth: loadTokens aufgerufen")
                        val token = tokenStorage.getAccessToken()
                        val refresh = tokenStorage.getRefreshToken()
                        println("HttpClientAuth: Token geladen. Access: ${token != null}, Refresh: ${refresh != null}")
                        if (token != null) {
                            BearerTokens(token, refresh ?: "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        println("HttpClientAuth: 401 erhalten. refreshTokens aufgerufen.")
                        // TODO: Implement actual refresh logic if available
                        println("HttpClientAuth: Kein Refresh möglich, Logout.")
                        null
                    }

                    sendWithoutRequest { request ->
                        val shouldSend = request.url.host == Constants.QUESTIFY_HOST
                        println("HttpClientAuth: sendWithoutRequest für ${request.url.host}: $shouldSend")
                        shouldSend
                    }
                }
            }
        }
    }
}