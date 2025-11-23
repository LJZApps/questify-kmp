package de.ljz.questify.core.data.models

import de.ljz.questify.core.data.models.descriptors.ThemeBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    @SerialName("onboarding_state")
    val onboardingState: Boolean = false,

    @SerialName("theme_behavior")
    val themeBehavior: ThemeBehavior = ThemeBehavior.SYSTEM_STANDARD
)
