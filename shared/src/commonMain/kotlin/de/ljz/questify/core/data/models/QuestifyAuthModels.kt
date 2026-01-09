package de.ljz.questify.core.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestifyLoginRequest(
    @SerialName("access_token") val accessToken: String
)

@Serializable
data class QuestifyLoginResponse(
    val token: String? = null,
    @SerialName("player_profile") val playerProfile: PlayerProfile? = null,
    val data: QuestifyLoginResponse? = null
) {
    val effectiveToken: String get() = token ?: data?.token ?: ""
    val effectiveProfile: PlayerProfile? get() = playerProfile ?: data?.playerProfile
}

@Serializable
data class PlayerProfile(
    val username: String?,
    val level: Int,
    @SerialName("needs_onboarding") val needsOnboarding: Boolean
)
