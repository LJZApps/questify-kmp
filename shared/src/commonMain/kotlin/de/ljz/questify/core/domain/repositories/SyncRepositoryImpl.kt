package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.data.network.SyncService
import de.ljz.questify.core.data.network.dto.PlayerStatsDto
import de.ljz.questify.core.data.network.dto.QuestCategoryDto
import de.ljz.questify.core.data.network.dto.QuestDto
import de.ljz.questify.core.data.network.dto.SubQuestDto
import de.ljz.questify.core.data.network.dto.SyncRequestDto
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import de.ljz.questify.feature.player_stats.data.models.descriptors.PlayerStatus
import de.ljz.questify.feature.player_stats.domain.repositories.PlayerStatsRepository
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.daos.SubQuestDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import de.ljz.questify.feature.quests.data.models.SyncStatus
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

internal class SyncRepositoryImpl(
    private val syncService: SyncService,
    private val questDao: QuestDao,
    private val questCategoryDao: QuestCategoryDao,
    private val subQuestDao: SubQuestDao,
    private val playerStatsRepository: PlayerStatsRepository,
    private val appSettingsRepository: AppSettingsRepository
) : SyncRepository {

    override suspend fun sync(): Result<Unit> {
        return try {
            val appSettings = appSettingsRepository.getAppSettings().first()
            val lastSyncTimestamp = appSettings.lastSyncTimestamp

            val categoriesToSync = questCategoryDao.getCategoriesToSync()
            val questsToSync = questDao.getQuestsToSync()
            val subQuestsToSync = subQuestDao.getSubQuestsToSync()
            val playerStats = playerStatsRepository.getPlayerStats().first()

            val request = SyncRequestDto(
                categories = categoriesToSync.map { it.toDto() },
                quests = questsToSync.map { it.toDto() },
                subQuests = subQuestsToSync.map { it.toDto() },
                playerStats = if (playerStats.isDirty) playerStats.toDto() else null,
                lastSyncTimestamp = lastSyncTimestamp
            )

            val response = syncService.sync(request)

            // 1. Process Categories
            response.categories.forEach { dto ->
                val existing = questCategoryDao.getQuestCategoryByUuid(dto.uuid)
                val entity = QuestCategoryEntity(
                    uuid = dto.uuid,
                    id = existing?.id ?: 0,
                    text = dto.text,
                    syncStatus = SyncStatus.SYNCED,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt
                )
                questCategoryDao.upsertQuestCategory(entity)
            }

            // 2. Process Quests
            response.quests.forEach { dto ->
                val existing = questDao.getQuestByUuid(dto.uuid)
                val categoryId = dto.categoryUuid?.let { uuid ->
                    questCategoryDao.getQuestCategoryByUuid(uuid)?.id
                }
                val entity = QuestEntity(
                    uuid = dto.uuid,
                    id = existing?.id ?: 0,
                    categoryUuid = dto.categoryUuid,
                    title = dto.title,
                    notes = dto.notes,
                    difficulty = dto.difficulty,
                    done = dto.done,
                    syncStatus = SyncStatus.SYNCED,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                    categoryId = categoryId,
                    createdAt = existing?.createdAt ?: dto.updatedAt ?: Instant.fromEpochMilliseconds(0)
                )
                questDao.upsert(entity)
            }

            // 3. Process SubQuests
            response.subQuests.forEach { dto ->
                val existing = subQuestDao.getSubQuestByUuid(dto.uuid)
                val questId = questDao.getQuestByUuid(dto.questUuid)?.id ?: 0
                val entity = SubQuestEntity(
                    uuid = dto.uuid,
                    id = existing?.id ?: 0,
                    questUuid = dto.questUuid,
                    text = dto.text,
                    isDone = dto.isDone,
                    orderIndex = dto.orderIndex,
                    syncStatus = SyncStatus.SYNCED,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                    questId = questId
                )
                subQuestDao.upsertSubQuest(entity)
            }

            // 4. Process PlayerStats
            val playerStatsToUpdate = response.playerStats?.let { dto ->
                playerStats.copy(
                    level = dto.level,
                    xp = dto.xp,
                    points = dto.points,
                    currentHP = dto.currentHp,
                    maxHP = dto.maxHp,
                    status = try {
                        PlayerStatus.valueOf(dto.status)
                    } catch (e: Exception) {
                        PlayerStatus.NORMAL
                    },
                    statusExpiryTimestamp = dto.statusExpiryTimestamp,
                    updatedAt = dto.updatedAt,
                    isDirty = false
                )
            } ?: if (playerStats.isDirty) {
                playerStats.copy(isDirty = false)
            } else {
                null
            }

            playerStatsToUpdate?.let {
                playerStatsRepository.updatePlayerStats(it)
            }

            // Mark sent items as synced
            categoriesToSync.forEach { questCategoryDao.markAsSynced(it.uuid, response.newTimestamp) }
            questsToSync.forEach { questDao.markAsSynced(it.uuid, response.newTimestamp) }
            subQuestsToSync.forEach { subQuestDao.markAsSynced(it.uuid, response.newTimestamp) }

            appSettingsRepository.updateLastSyncTimestamp(response.newTimestamp)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun QuestCategoryEntity.toDto() = QuestCategoryDto(
        uuid = uuid,
        text = text,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    private fun QuestEntity.toDto() = QuestDto(
        uuid = uuid,
        categoryUuid = categoryUuid,
        title = title,
        notes = notes,
        difficulty = difficulty,
        done = done,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    private fun SubQuestEntity.toDto() = SubQuestDto(
        uuid = uuid,
        questUuid = questUuid,
        text = text,
        isDone = isDone,
        orderIndex = orderIndex,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    private fun PlayerStats.toDto() = PlayerStatsDto(
        level = level,
        xp = xp,
        points = points,
        currentHp = currentHP,
        maxHp = maxHP,
        status = status.name,
        statusExpiryTimestamp = statusExpiryTimestamp,
        updatedAt = updatedAt
    )
}
