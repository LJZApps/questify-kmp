package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.core.data.sync.QuestSyncManager
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class QuestCategoryRepositoryImpl(
    private val questCategoryDao: QuestCategoryDao,
    private val syncManager: QuestSyncManager
) : QuestCategoryRepository {

    override suspend fun addQuestCategory(questCategoryEntity: QuestCategoryEntity) {
        questCategoryDao.upsertQuestCategory(
            questCategory = questCategoryEntity.copy(syncStatus = "UNSYNCED")
        )
        syncManager.sync()
    }

    override suspend fun deleteQuestCategory(id: Int) {
        val category = questCategoryDao.getQuestCategoryById(id).first()
        if (category?.remoteId != null) {
            questCategoryDao.markCategoryForDeletion(id)
        } else {
            questCategoryDao.deleteQuestCategory(id)
        }
        syncManager.sync()
    }

    override suspend fun updateQuestCategory(id: Int, value: String) {
        questCategoryDao.updateQuestCategory(
            questCategoryId = id,
            text = value
        )
        questCategoryDao.updateSyncStatus(id, "UNSYNCED", null)
        syncManager.sync()
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