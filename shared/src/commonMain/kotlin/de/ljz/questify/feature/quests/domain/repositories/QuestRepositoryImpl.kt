package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

class QuestRepositoryImpl(
    private val questDao: QuestDao
) : QuestRepository {
    override suspend fun addMainQuest(quest: QuestEntity): Long {
        return questDao.upsert(quest)
    }

    override suspend fun upsertQuest(quest: QuestEntity): Long {
        return questDao.upsert(quest)
    }

    override suspend fun setQuestDone(id: Int, done: Boolean) {
        questDao.setQuestDone(id, done)
    }

    override suspend fun updateQuest(quest: QuestEntity) {
        questDao.upsert(quest)
    }

    /*@Deprecated(
        "Please use updateQuest(quest: QuestEntity)",
        replaceWith = ReplaceWith("updateQuest(quest: QuestEntity)"),
        level = DeprecationLevel.ERROR
    )*/
    override suspend fun updateQuest(
        id: Int,
        title: String,
        description: String?,
        difficulty: Difficulty,
        dueDate: Instant?,
        categoryId: Int?
    ) {
        questDao.updateQuestById(
            id = id,
            title = title,
            description = description,
            difficulty = difficulty,
            dueDate = dueDate,
            categoryId = categoryId
        )
    }

    override suspend fun getQuests(): Flow<List<QuestWithDetails>> {
        return questDao.getAllQuests()
    }

    override suspend fun getQuestById(id: Int): QuestWithDetails {
        return questDao.getQuestById(id)
    }

    override suspend fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>> {
        return questDao.getQuestsForCategoryStream(categoryId)
    }

    override suspend fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?> {
        return questDao.getQuestByIdFlow(id)
    }

    override suspend fun deleteQuest(id: Int) {
        return questDao.deleteQuest(id)
    }
}