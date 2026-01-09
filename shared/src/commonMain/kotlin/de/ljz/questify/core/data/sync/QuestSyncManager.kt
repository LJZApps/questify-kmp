package de.ljz.questify.core.data.sync

import de.ljz.questify.core.data.remote.QuestifyRemoteDataSource
import de.ljz.questify.core.data.remote.models.QuestCategoryDTO
import de.ljz.questify.core.data.remote.models.QuestDTO
import de.ljz.questify.core.data.remote.models.SubQuestDTO
import de.ljz.questify.core.data.remote.util.NetworkResult
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.daos.SubQuestDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

class QuestSyncManager(
    private val remoteDataSource: QuestifyRemoteDataSource,
    private val questDao: QuestDao,
    private val questCategoryDao: QuestCategoryDao,
    private val subQuestDao: SubQuestDao
) {

    suspend fun sync() {
        syncCategories()
        syncQuests()
    }

    private suspend fun syncCategories() {
        // 1. Push deletions
        val markedForDeletion = questCategoryDao.getCategoriesMarkedForDeletion()
        markedForDeletion.forEach { local ->
            if (local.remoteId != null) {
                val result = remoteDataSource.deleteQuestCategory(local.remoteId)
                if (result is NetworkResult.Success || (result is NetworkResult.Error && result.code == 404)) {
                    questCategoryDao.deleteQuestCategory(local.id)
                }
            } else {
                questCategoryDao.deleteQuestCategory(local.id)
            }
        }

        // 2. Push updates/creates
        val unsynced = questCategoryDao.getUnsyncedCategories()
        unsynced.forEach { local ->
            val dto = QuestCategoryDTO(text = local.text)
            val result = if (local.remoteId == null) {
                remoteDataSource.createQuestCategory(dto)
            } else {
                remoteDataSource.updateQuestCategory(local.remoteId, dto)
            }
            
            if (result is NetworkResult.Success) {
                questCategoryDao.updateSyncStatus(local.id, "SYNCED", result.data.id)
            }
        }

        // 3. Pull
        val result = remoteDataSource.getQuestCategories()
        if (result is NetworkResult.Success) {
            val remoteCategories = result.data
            
            // Delete locals that are gone on remote
            val remoteIds = remoteCategories.mapNotNull { it.id }.toSet()
            val localRemoteIds = questCategoryDao.getAllRemoteIds()
            localRemoteIds.forEach { localRemoteId ->
                if (localRemoteId !in remoteIds) {
                    val local = questCategoryDao.getCategoryByRemoteId(localRemoteId)
                    local?.let { questCategoryDao.deleteQuestCategory(it.id) }
                }
            }

            // Upsert remote categories
            remoteCategories.forEach { remote ->
                val local = questCategoryDao.getCategoryByRemoteId(remote.id!!)
                if (local == null) {
                    questCategoryDao.upsertQuestCategory(
                        QuestCategoryEntity(
                            remoteId = remote.id,
                            text = remote.text,
                            syncStatus = "SYNCED"
                        )
                    )
                } else {
                    questCategoryDao.updateQuestCategory(local.id, remote.text)
                    questCategoryDao.updateSyncStatus(local.id, "SYNCED", remote.id)
                }
            }
        }
    }

    private suspend fun syncQuests() {
        // 1. Push deletions
        val markedForDeletion = questDao.getQuestsMarkedForDeletion()
        markedForDeletion.forEach { localWithDetails ->
            val local = localWithDetails.quest
            if (local.remoteId != null) {
                val result = remoteDataSource.deleteQuest(local.remoteId)
                if (result is NetworkResult.Success || (result is NetworkResult.Error && result.code == 404)) {
                    questDao.deleteQuest(local.id)
                }
            } else {
                questDao.deleteQuest(local.id)
            }
        }

        // 2. Push updates/creates
        val unsynced = questDao.getUnsyncedQuests()
        unsynced.forEach { localWithDetails ->
            val local = localWithDetails.quest
            val categoryRemoteId = local.categoryId?.let { 
                questCategoryDao.getQuestCategoryById(it).first()?.remoteId 
            }
            
            val dto = QuestDTO(
                categoryId = categoryRemoteId,
                title = local.title,
                notes = local.notes,
                difficulty = local.difficulty.name,
                dueDate = local.dueDate?.toString(),
                lockDeletion = local.lockDeletion,
                done = local.done,
                subQuests = localWithDetails.subTasks.map { 
                    SubQuestDTO(id = it.remoteId, text = it.text, isDone = it.isDone, orderIndex = it.orderIndex) 
                }
            )

            val result = if (local.remoteId == null) {
                remoteDataSource.createQuest(dto)
            } else {
                remoteDataSource.updateQuest(local.remoteId, dto)
            }
            
            if (result is NetworkResult.Success) {
                questDao.updateSyncStatus(local.id, "SYNCED", result.data.id)
            }
        }

        // 3. Pull
        val result = remoteDataSource.getQuests()
        if (result is NetworkResult.Success) {
            val remoteQuests = result.data
            
            // Delete locals that are gone on remote
            val remoteIds = remoteQuests.mapNotNull { it.id }.toSet()
            val localRemoteIds = questDao.getAllRemoteIds()
            localRemoteIds.forEach { localRemoteId ->
                if (localRemoteId !in remoteIds) {
                    val local = questDao.getQuestByRemoteId(localRemoteId)
                    local?.let { questDao.deleteQuest(it.id) }
                }
            }

            remoteQuests.forEach { remote ->
                val local = questDao.getQuestByRemoteId(remote.id!!)
                val categoryId = remote.categoryId?.let { rId ->
                    questCategoryDao.getCategoryByRemoteId(rId)?.id
                }

                val questEntity = QuestEntity(
                    id = local?.id ?: 0,
                    remoteId = remote.id,
                    title = remote.title,
                    notes = remote.notes,
                    difficulty = Difficulty.valueOf(remote.difficulty),
                    dueDate = remote.dueDate?.let { Instant.parse(it) },
                    createdAt = remote.createdAt?.let { Instant.parse(it) } ?: Instant.fromEpochMilliseconds(0),
                    updatedAt = remote.updatedAt?.let { Instant.parse(it) },
                    lockDeletion = remote.lockDeletion,
                    done = remote.done,
                    categoryId = categoryId,
                    syncStatus = "SYNCED"
                )

                val questId = questDao.upsertMainQuest(questEntity)
                
                subQuestDao.deleteSubQuests(questId.toInt())
                val subQuestEntities = remote.subQuests.map { sDto ->
                    SubQuestEntity(
                        remoteId = sDto.id,
                        text = sDto.text,
                        isDone = sDto.isDone,
                        questId = questId,
                        orderIndex = sDto.orderIndex
                    )
                }
                subQuestDao.upsertSubQuests(subQuestEntities)
            }
        }
    }
}
