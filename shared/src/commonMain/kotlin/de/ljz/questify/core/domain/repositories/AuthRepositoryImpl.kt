package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.auth.TokenStorage
import de.ljz.questify.core.data.models.auth.TokenResponse
import de.ljz.questify.core.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.http.isSuccess

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    private val clientId = "TOOD" // TODO
    private val redirectUri = "de.ljz.questify://callback"
    private val tokenUrl = "https://id.omrix.net/oauth/token"

    override suspend fun performLogin(authCode: String, codeVerifier: String): Result<Unit> {
        return try {
            val response = client.submitForm(
                url = Constants.OMRIX_TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("redirect_uri", redirectUri)
                    append("code", authCode)
                    append("code_verifier", codeVerifier)
                }
            )

            if (response.status.isSuccess()) {
                val tokenData: TokenResponse = response.body()
                tokenStorage.saveTokens(tokenData.accessToken, tokenData.refreshToken)
                Result.success(Unit)
            } else {
                val errorBody = response.body<String>()
                Result.failure(Exception("Login failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse? {
        return try {
            val response = client.submitForm(
                url = Constants.OMRIX_TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("grant_type", "refresh_token")
                    append("client_id", clientId)
                    append("refresh_token", refreshToken)
                }
            )

            if (response.status.isSuccess()) {
                response.body<TokenResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun logout() {
        tokenStorage.clearTokens()
    }
}