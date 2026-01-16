package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.auth.TokenStorage
import de.ljz.questify.core.data.models.auth.OmrixTokenResponse
import de.ljz.questify.core.data.models.auth.PlayerProfileDto
import de.ljz.questify.core.data.models.auth.QuestifyAuthResponse
import de.ljz.questify.core.data.models.auth.QuestifyLoginRequest
import de.ljz.questify.core.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    private val clientId = "019bc584-9423-719b-9596-6f669a072102" // TODO
    private val redirectUri = "de.ljz.questify://callback"
    private val tokenUrl = "https://id.omrix.net/oauth/token"

    override suspend fun performLogin(authCode: String, codeVerifier: String): Result<PlayerProfileDto> {
        println("AuthRepository: performLogin gestartet mit authCode: $authCode")
        return try {
            println("AuthRepository: Sende Omrix Token Request an ${Constants.OMRIX_TOKEN_ENDPOINT}")
            val omrixResponse = client.submitForm(
                url = Constants.OMRIX_TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("redirect_uri", redirectUri)
                    append("code", authCode)
                    append("code_verifier", codeVerifier)
                }
            )

            println("AuthRepository: Omrix Response Status: ${omrixResponse.status}")

            if (!omrixResponse.status.isSuccess()) {
                val errorMsg = "Omrix Login failed: ${omrixResponse.status}"
                println("AuthRepository: ERROR - $errorMsg")
                return Result.failure(Exception(errorMsg))
            }

            val omrixData: OmrixTokenResponse = omrixResponse.body()
            println("AuthRepository: Omrix Login erfolgreich. AccessToken erhalten.")

            println("AuthRepository: Sende Questify Swap Request an ${Constants.QUESTIFY_API_URL}/auth/omrix-login")
            val questifyResponse = client.post {
                url("${Constants.QUESTIFY_API_URL}/auth/omrix-login")
                contentType(ContentType.Application.Json)
                setBody(QuestifyLoginRequest(accessToken = omrixData.accessToken))
            }

            println("AuthRepository: Questify Response Status: ${questifyResponse.status}")

            if (!questifyResponse.status.isSuccess()) {
                val errorBody = questifyResponse.body<String>()
                val errorMsg = "Questify Swap failed: ${questifyResponse.status} - $errorBody"
                println("AuthRepository: ERROR - $errorMsg")
                return Result.failure(Exception(errorMsg))
            }

            val questifyData: QuestifyAuthResponse = questifyResponse.body()
            println("AuthRepository: Questify Swap erfolgreich. Speichere Tokens.")

            tokenStorage.saveTokens(
                accessToken = questifyData.token,
                refreshToken = omrixData.refreshToken ?: ""
            )

            println("AuthRepository: Login vollständig abgeschlossen für ${questifyData.playerProfile.username}")
            Result.success(questifyData.playerProfile)
        } catch (e: Exception) {
            println("AuthRepository: Exception während performLogin: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        println("AuthRepository: Logout aufgerufen")
        tokenStorage.clearTokens()
    }
}