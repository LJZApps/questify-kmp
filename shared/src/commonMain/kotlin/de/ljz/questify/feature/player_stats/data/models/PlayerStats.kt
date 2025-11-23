package de.ljz.questify.feature.player_stats.data.models

import de.ljz.questify.feature.player_stats.data.models.descriptors.PlayerStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStats(
    @SerialName("id")
    val id: Int = 1,

    @SerialName("level")
    val level: Int = 1,

    @SerialName("xp")
    val xp: Int = 0,

    @SerialName("points")
    val points: Int = 0,

    @SerialName("current_hp")
    val currentHP: Int = 100,

    @SerialName("max_hp")
    val maxHP: Int = 100,

    @SerialName("status")
    val status: PlayerStatus = PlayerStatus.NORMAL,

    @SerialName("status_expiry_timestamp")
    val statusExpiryTimestamp: Long? = null
)
