package de.ljz.questify.feature.settings.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureSettings(
    @SerialName("quest_fast_adding_enabled")
    val questFastAddingEnabled: Boolean = true,
)