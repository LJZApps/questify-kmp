package de.ljz.questify.core.domain.repositories.auth

import androidx.datastore.core.DataStore
import de.ljz.questify.core.data.models.AuthTokens
import de.ljz.questify.core.data.models.QuestifyLoginRequest
import de.ljz.questify.core.data.models.QuestifyLoginResponse
import de.ljz.questify.core.util.currentTimeSeconds
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val dataStore: DataStore<AuthTokens>,
    private val httpClient: HttpClient // Ein HttpClient ohne Auth-Plugin für Token-Requests
) : AuthRepository {

    override val authTokens: Flow<AuthTokens> = dataStore.data
    override var codeVerifier: String? = null

    override suspend fun getAccessToken(): String? {
        return authTokens.first().accessToken
    }

    override suspend fun exchangeCodeForTokens(code: String, codeVerifier: String): Result<AuthTokens> {
        return try {
            val response: AuthTokensResponse = httpClient.submitForm(
                url = AuthConfig.TOKEN_URL,
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("redirect_uri", AuthConfig.REDIRECT_URI)
                    append("client_id", AuthConfig.CLIENT_ID)
                    append("code_verifier", codeVerifier)
                }
            ).body()

            val tokens = AuthTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                idToken = response.idToken,
                expiresIn = response.expiresIn,
                issuedAt = currentTimeSeconds()
            )

            dataStore.updateData { tokens }
            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithOmrix(omrixAccessToken: String): Result<QuestifyLoginResponse> {
        return try {
            val httpResponse = httpClient.post("${AuthConfig.QUESTIFY_API_BASE_URL}/auth/omrix-login") {
                contentType(ContentType.Application.Json)
                setBody(QuestifyLoginRequest(omrixAccessToken))
            }

            if (httpResponse.status.isSuccess()) {
                try {
                    val response: QuestifyLoginResponse = httpResponse.body()
                    dataStore.updateData { currentTokens ->
                        currentTokens.copy(questifyToken = response.effectiveToken)
                    }
                    Result.success(response)
                } catch (e: Exception) {
                    val body = httpResponse.bodyAsText()
                    Result.failure(Exception("Serialization error. Body: $body", e))
                }
            } else {
                val errorBody = try { httpResponse.bodyAsText() } catch (e: Exception) { "Could not read error body" }
                Result.failure(Exception("Backend error ${httpResponse.status}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshTokens(): Result<AuthTokens> {
        val currentTokens = authTokens.first()
        val refreshToken = currentTokens.refreshToken ?: return Result.failure(Exception("No refresh token"))

        return try {
            val response: AuthTokensResponse = httpClient.submitForm(
                url = AuthConfig.TOKEN_URL,
                formParameters = parameters {
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                    append("client_id", AuthConfig.CLIENT_ID)
                }
            ).body()

            val tokens = currentTokens.copy(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken ?: currentTokens.refreshToken,
                expiresIn = response.expiresIn,
                issuedAt = currentTimeSeconds()
            )

            dataStore.updateData { tokens }
            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        dataStore.updateData { AuthTokens() }
    }
}

@kotlinx.serialization.Serializable
private data class AuthTokensResponse(
    @kotlinx.serialization.SerialName("access_token") val accessToken: String,
    @kotlinx.serialization.SerialName("refresh_token") val refreshToken: String? = null,
    @kotlinx.serialization.SerialName("id_token") val idToken: String? = null,
    @kotlinx.serialization.SerialName("expires_in") val expiresIn: Long? = null
)
