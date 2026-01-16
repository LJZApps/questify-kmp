package de.ljz.questify.feature.profile.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsernameAvailabilityDto(
    @SerialName("available") val available: Boolean
)
