package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.core.utils.TimeUtils
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import kotlinx.coroutines.flow.Flow

internal class QuestCategoryRepositoryImpl(
    private val questCategoryDao: QuestCategoryDao
) : QuestCategoryRepository {

    override suspend fun addQuestCategory(questCategoryEntity: QuestCategoryEntity) {
        questCategoryDao.upsertQuestCategory(
            questCategory = questCategoryEntity
        )
    }

    override suspend fun deleteQuestCategory(id: Int) {
        questCategoryDao.markCategoryAsDeleted(
            id = id,
            timestamp = TimeUtils.now()
        )
    }

    override suspend fun updateQuestCategory(id: Int, value: String) {
        questCategoryDao.updateQuestCategory(
            questCategoryId = id,
            text = value,
            updatedAt = TimeUtils.now()
        )
    }

    override fun getAllQuestCategories(): Flow<List<QuestCategoryEntity>> {
        return questCategoryDao.getAllQuestCategories()
    }

    override fun getQuestCategoryById(id: Int): Flow<QuestCategoryEntity?> {
        return questCategoryDao.getQuestCategoryById(
            id = id
        )
    }
}
