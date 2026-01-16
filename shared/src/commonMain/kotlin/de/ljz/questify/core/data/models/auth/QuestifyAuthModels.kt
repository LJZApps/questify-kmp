package de.ljz.questify.core.data.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestifyLoginRequest(
    @SerialName("access_token") val accessToken: String
)

@Serializable
data class QuestifyAuthResponse(
    @SerialName("token")
    val token: String,
    @SerialName("player_profile")
    val playerProfile: PlayerProfileDto
)

@Serializable
data class PlayerProfileDto(
    @SerialName("username") val username: String?,
    @SerialName("level") val level: Int = 1,
    @SerialName("needs_onboarding") val needsOnboarding: Boolean
)

@Serializable
data class OmrixTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null, // Optional, falls Omrix Refresh Tokens nutzt
    @SerialName("expires_in") val expiresIn: Long = 3600
)