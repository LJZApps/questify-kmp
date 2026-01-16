@file:UseSerializers(InstantSerializer::class)
package de.ljz.questify.core.data.network.dto

import de.ljz.questify.core.utils.InstantSerializer
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.time.Instant

@Serializable
data class SyncRequestDto(
    @SerialName("categories") val categories: List<QuestCategoryDto>,
    @SerialName("quests") val quests: List<QuestDto>,
    @SerialName("sub_quests") val subQuests: List<SubQuestDto>,
    @SerialName("player_stats") val playerStats: PlayerStatsDto?,
    @SerialName("last_sync_timestamp") val lastSyncTimestamp: Instant?
)

@Serializable
data class SyncResponseDto(
    @SerialName("categories") val categories: List<QuestCategoryDto>,
    @SerialName("quests") val quests: List<QuestDto>,
    @SerialName("sub_quests") val subQuests: List<SubQuestDto>,
    @SerialName("player_stats") val playerStats: PlayerStatsDto?,
    @SerialName("new_timestamp") val newTimestamp: Instant
)

@Serializable
data class QuestCategoryDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("text") val text: String,
    @SerialName("updated_at") val updatedAt: Instant?,
    @SerialName("deleted_at") val deletedAt: Instant? = null
)

@Serializable
data class QuestDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("category_uuid") val categoryUuid: String?,
    @SerialName("title") val title: String,
    @SerialName("notes") val notes: String?,
    @SerialName("difficulty") val difficulty: Difficulty,
    @SerialName("due_date") val dueDate: Instant? = null,
    @SerialName("done") val done: Boolean,
    @SerialName("updated_at") val updatedAt: Instant?,
    @SerialName("deleted_at") val deletedAt: Instant? = null
)

@Serializable
data class SubQuestDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("quest_uuid") val questUuid: String,
    @SerialName("text") val text: String,
    @SerialName("is_done") val isDone: Boolean,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("updated_at") val updatedAt: Instant?,
    @SerialName("deleted_at") val deletedAt: Instant? = null
)

@Serializable
data class PlayerStatsDto(
    @SerialName("level") val level: Int,
    @SerialName("xp") val xp: Int,
    @SerialName("points") val points: Int,
    @SerialName("current_hp") val currentHp: Int,
    @SerialName("max_hp") val maxHp: Int,
    @SerialName("status") val status: String,
    @SerialName("status_expiry_timestamp") val statusExpiryTimestamp: Long?,
    @SerialName("updated_at") val updatedAt: Instant?
)
