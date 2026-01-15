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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    // OMRIX client
    fun createAuthHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    fun createAppHttpClient(
        tokenStorage: TokenStorage,
        authRepository: AuthRepository
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; prettyPrint = true })
            }

            defaultRequest {
                url(Constants.QUESTIFY_API_URL)
                contentType(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenStorage.getAccessToken()
                        val refresh = tokenStorage.getRefreshToken()
                        if (access != null && refresh != null) BearerTokens(access, refresh) else null
                    }

                    refreshTokens {
                        val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null
                        val newTokens = authRepository.refreshToken(refreshToken)

                        if (newTokens != null) {
                            tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                        } else {
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        request.url.host == Constants.QUESTIFY_HOST
                    }
                }
            }
        }
    }
}