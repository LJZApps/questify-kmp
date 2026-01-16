package de.ljz.questify.core.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmrixTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("expires_in") val expiresIn: Int
)

class AuthService(
    private val client: HttpClient,
    private val pkceGenerator: PkceGenerator
) {
    private val clientId = "TODO"
    private val redirectUri = "de.ljz.questify://auth"
    private val discoveryUrl = "https://id.omrix.net"

    private var currentVerifier: String? = null

    suspend fun exchangeCodeForToken(authCode: String): OmrixTokenResponse {
        val verifier = currentVerifier ?: throw IllegalStateException("Verifier verloren gegangen!")

        val response: OmrixTokenResponse = client.submitForm(
            url = "https://id.omrix.net/oauth/token",
            formParameters = Parameters.build {
                append("grant_type", "authorization_code")
                append("client_id", clientId)
                append("redirect_uri", redirectUri)
                append("code", authCode)
                append("code_verifier", verifier)
            }
        ).body()

        currentVerifier = null

        return response
    }

    suspend fun loginToQuestifyBackend(omrixToken: String): String {
        // TODO
        return "fake-sanctum-token"
    }
}