package de.ljz.questify.feature.profile.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("real_name") val realName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("level") val level: Int? = null,
    @SerialName("xp") val xp: Int? = null
)

@Serializable
data class ProfileResponseDto(
    @SerialName("data") val data: UserProfileDto
)

@Serializable
data class ProfileUpdateRequestDto(
    @SerialName("username") val username: String,
    @SerialName("real_name") val realName: String,
    @SerialName("bio") val bio: String
)
