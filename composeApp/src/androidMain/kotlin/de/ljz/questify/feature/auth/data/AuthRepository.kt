package de.ljz.questify.feature.auth.data

import de.ljz.questify.core.network.QuestifyNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmrixTokenResponse(
    @SerialName("access_token") val accessToken: String
)

@Serializable
data class QuestifyLoginResponse(
    val token: String
)

object AuthRepository {
    private val client = QuestifyNetwork.client

    private const val CLIENT_ID = "questify-mobile" // <--- ANPASSEN
    private const val REDIRECT_URI = "de.ljz.questify://auth" // <--- ANPASSEN
    private const val OMRIX_URL = "https://id.omrix.net"
    private const val QUESTIFY_BACKEND_URL = "https://dein-questify-backend.de/api" // <--- ANPASSEN


}