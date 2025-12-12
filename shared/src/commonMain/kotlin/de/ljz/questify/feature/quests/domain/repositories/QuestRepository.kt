package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface QuestRepository {
    suspend fun addMainQuest(quest: QuestEntity): Long

    suspend fun upsertQuest(quest: QuestEntity): Long

    suspend fun setQuestDone(id: Int, done: Boolean)

    suspend fun updateQuest(quest: QuestEntity)

    /*@Deprecated(
        message = "Please use updateQuest(quest: QuestEntity)",
        replaceWith = ReplaceWith("updateQuest(quest: QuestEntity)"),
        level = DeprecationLevel.ERROR
    )*/
    suspend fun updateQuest(
        id: Int,
        title: String,
        description: String? = null,
        difficulty: Difficulty,
        dueDate: Instant? = null,
        categoryId: Int? = null
    )

    suspend fun getQuests(): Flow<List<QuestWithDetails>>

    suspend fun getQuestById(id: Int): QuestWithDetails

    suspend fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>>

    suspend fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?>

    suspend fun deleteQuest(id: Int)
}