package de.ljz.questify.feature.auth.data

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