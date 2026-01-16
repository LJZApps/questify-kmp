package de.ljz.questify.feature.settings.data.models

import de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class AppSettings(
    @SerialName("onboarding_state")
    val onboardingState: Boolean = false,

    @SerialName("last_opened_version")
    val lastOpenedVersion: Int = 0,

    @SerialName("theme_behavior")
    val themeBehavior: ThemeBehavior = ThemeBehavior.SYSTEM_STANDARD,

    @SerialName("last_sync_timestamp")
    val lastSyncTimestamp: Instant? = null
)
