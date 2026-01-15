package de.ljz.questify.feature.quests.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val changes: List<QuestSyncDto>,
    @SerialName("last_sync_timestamp")
    val lastSyncTimestamp: String?
)

@Serializable
data class SyncResponse(
    val changes: List<QuestSyncDto>,
    @SerialName("new_timestamp")
    val newTimestamp: String
)

@Serializable
data class QuestSyncDto(
    val uuid: String,
    val title: String,
    val notes: String?,
    val difficulty: String,
    val done: Boolean,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("deleted_at")
    val deletedAt: String? = null
)