package de.ljz.questify.core.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("level") val level: Int,
    @SerialName("xp") val xp: Int,
    @SerialName("points") val points: Int,
    @SerialName("current_hp") val currentHp: Int,
    @SerialName("max_hp") val maxHp: Int,
    @SerialName("status") val status: String,
    @SerialName("status_expiry_timestamp") val statusExpiryTimestamp: Long? = null
)

@Serializable
data class UserDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("real_name") val realName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("level") val level: Int? = null,
    @SerialName("xp") val xp: Int? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class QuestCategoryDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("text") val text: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class QuestDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("title") val title: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("lock_deletion") val lockDeletion: Boolean = false,
    @SerialName("done") val done: Boolean = false,
    @SerialName("sub_quests") val subQuests: List<SubQuestDTO> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class SubQuestDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("text") val text: String,
    @SerialName("is_done") val isDone: Boolean = false,
    @SerialName("order_index") val orderIndex: Int = 0
)
