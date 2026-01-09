package de.ljz.questify.feature.profile.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    @SerialName(value = "id")
    val id: Int = -1,

    @SerialName(value = "name")
    val displayName: String = "Abenteurer",

    @SerialName(value = "username")
    val username: String = "",

    @SerialName(value = "about_me")
    val aboutMe: String = "",

    @SerialName(value = "email")
    val email: String = "",

    @SerialName(value = "avatar_url")
    val profilePicture: String = "",

    @SerialName(value = "level")
    val level: Int = 1
)

@Serializable
data class AppUserWrapper(
    val data: AppUser
)