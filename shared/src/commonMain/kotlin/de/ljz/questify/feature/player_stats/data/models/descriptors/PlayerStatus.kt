package de.ljz.questify.feature.player_stats.data.models.descriptors

import kotlinx.serialization.Serializable

@Serializable
enum class PlayerStatus {
    NORMAL,
    EXHAUSTED
}