package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.data.network.SyncService
import de.ljz.questify.core.data.network.dto.QuestCategoryDto
import de.ljz.questify.core.data.network.dto.QuestDto
import de.ljz.questify.core.data.network.dto.SyncResponseDto
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import de.ljz.questify.feature.player_stats.domain.repositories.PlayerStatsRepository
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.daos.SubQuestDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import de.ljz.questify.feature.quests.data.models.SyncStatus
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class SyncRepositoryImplTest {

    private fun createMockHttpClient(response: SyncResponseDto): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = Json.encodeToString(SyncResponseDto.serializer(), response),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    // Fakes
    class FakeQuestDao : QuestDao {
        val quests = mutableListOf<QuestEntity>()
        var upsertCount = 0
        var markAsSyncedCount = 0

        override suspend fun upsert(quest: QuestEntity): Long {
            quests.removeAll { it.uuid == quest.uuid }
            quests.add(quest)
            upsertCount++
            return 1L
        }
        override suspend fun upsertAll(quests: List<QuestEntity>) {}
        override suspend fun searchQuests(query: String): List<QuestWithDetails> = emptyList()
        override fun getAllQuests(): Flow<List<QuestWithDetails>> = flowOf(emptyList())
        override suspend fun getQuestById(id: Int): QuestWithDetails = throw Exception()
        override fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>> = flowOf(emptyList())
        override suspend fun updateQuestById(id: Int, title: String, description: String?, difficulty: Difficulty, dueDate: Instant?, categoryId: Int?, updatedAt: Instant) {}
        override suspend fun suspendGetQuestById(id: Int): QuestWithDetails = throw Exception()
        override fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?> = flowOf(null)
        override suspend fun getQuestByUuid(uuid: String): QuestEntity? = quests.find { it.uuid == uuid }
        override suspend fun setQuestDone(id: Int, done: Boolean, updatedAt: Instant) {}
        override suspend fun getCompletedQuestsCount(): Int = 0
        override suspend fun upsertMainQuest(value: QuestEntity): Long = 1L
        override suspend fun upsertQuests(value: List<QuestEntity>) {}
        override suspend fun updateQuests(quests: List<QuestEntity>) {}
        override suspend fun getQuestsToSync(): List<QuestEntity> = quests.filter { it.syncStatus != SyncStatus.SYNCED }
        override suspend fun markQuestAsDeleted(id: Int, timestamp: Instant) {}
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant) {
            markAsSyncedCount++
            val quest = quests.find { it.uuid == uuid }
            if (quest != null) {
                quests.removeAll { it.uuid == uuid }
                quests.add(quest.copy(syncStatus = SyncStatus.SYNCED, updatedAt = updatedAt))
            }
        }
    }

    // Fakes
    class FakeQuestCategoryDao : QuestCategoryDao {
        val categories = mutableListOf<QuestCategoryEntity>()
        var markAsSyncedCount = 0

        override suspend fun upsertQuestCategory(questCategory: QuestCategoryEntity): Long {
            categories.removeAll { it.uuid == questCategory.uuid }
            categories.add(questCategory)
            return 1L
        }
        override fun getAllQuestCategories(): Flow<List<QuestCategoryEntity>> = flowOf(emptyList())
        override fun getQuestCategoryById(id: Int): Flow<QuestCategoryEntity?> = flowOf(null)
        override suspend fun getQuestCategoryByUuid(uuid: String): QuestCategoryEntity? = categories.find { it.uuid == uuid }
        override suspend fun updateCategories(categories: List<QuestCategoryEntity>) {}
        override suspend fun getCategoryCount(): Long = 0
        override suspend fun updateQuestCategory(questCategoryId: Int, text: String, updatedAt: Instant) {}
        override suspend fun getCategoriesToSync(): List<QuestCategoryEntity> = categories.filter { it.syncStatus != SyncStatus.SYNCED }
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant) {
            markAsSyncedCount++
            val cat = categories.find { it.uuid == uuid }
            if (cat != null) {
                categories.removeAll { it.uuid == uuid }
                categories.add(cat.copy(syncStatus = SyncStatus.SYNCED, updatedAt = updatedAt))
            }
        }
        override suspend fun markCategoryAsDeleted(id: Int, timestamp: Instant) {}
    }

    class FakeSubQuestDao : SubQuestDao {
        val subQuests = mutableListOf<SubQuestEntity>()
        var markAsSyncedCount = 0

        override suspend fun upsertSubQuest(subQuest: SubQuestEntity) {
            subQuests.removeAll { it.uuid == subQuest.uuid }
            subQuests.add(subQuest)
        }
        override suspend fun upsertSubQuests(subQuests: List<SubQuestEntity>) {}
        override suspend fun updateSubQuests(subQuests: List<SubQuestEntity>) {}
        override suspend fun markSubQuestsAsDeleted(id: Int, timestamp: Instant) {}
        override suspend fun checkSubQuest(id: Int, checked: Boolean, updatedAt: Instant) {}
        override suspend fun getSubQuestsToSync(): List<SubQuestEntity> = subQuests.filter { it.syncStatus != SyncStatus.SYNCED }
        override suspend fun getSubQuestByUuid(uuid: String): SubQuestEntity? = subQuests.find { it.uuid == uuid }
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant) {
            markAsSyncedCount++
            val sub = subQuests.find { it.uuid == uuid }
            if (sub != null) {
                subQuests.removeAll { it.uuid == uuid }
                subQuests.add(sub.copy(syncStatus = SyncStatus.SYNCED, updatedAt = updatedAt))
            }
        }
        override suspend fun markSubQuestAsDeleted(id: Int, timestamp: Instant) {}
    }

    class FakePlayerStatsRepository : PlayerStatsRepository {
        var stats = PlayerStats(isDirty = true)
        override fun getPlayerStats(): Flow<PlayerStats> = flowOf(stats)
        override suspend fun updatePlayerStats(playerStats: PlayerStats) {
            stats = playerStats
        }
    }

    class FakeAppSettingsRepository : AppSettingsRepository {
        var settings = AppSettings(lastSyncTimestamp = null)
        override fun getAppSettings(): Flow<AppSettings> = flowOf(settings)
        override suspend fun updateLastSyncTimestamp(timestamp: Instant) {
            settings = settings.copy(lastSyncTimestamp = timestamp)
        }
        override suspend fun setOnboardingDone() {}
        override suspend fun resetOnboarding() {}
        override suspend fun setDarkModeBehavior(value: de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior) {}
    }

    @Test
    fun testSyncFlow() = runTest {
        val now = Instant.fromEpochMilliseconds(1000L)
        val responseTimestamp = Instant.fromEpochMilliseconds(2000L)

        val questDao = FakeQuestDao()
        val categoryDao = FakeQuestCategoryDao()
        val subQuestDao = FakeSubQuestDao()
        val playerStatsRepo = FakePlayerStatsRepository()
        val appSettingsRepo = FakeAppSettingsRepository()

        // 1. Setup local data
        val category = QuestCategoryEntity(uuid = "cat-1", text = "Category 1", syncStatus = SyncStatus.DIRTY)
        categoryDao.upsertQuestCategory(category)

        val quest = QuestEntity(uuid = "quest-1", title = "Quest 1", syncStatus = SyncStatus.DIRTY, difficulty = Difficulty.EASY, createdAt = now)
        questDao.upsert(quest)

        // 2. Setup Mock Server
        val response = SyncResponseDto(
            categories = listOf(QuestCategoryDto("cat-2", "Server Category", responseTimestamp)),
            quests = emptyList(),
            subQuests = emptyList(),
            playerStats = null,
            newTimestamp = responseTimestamp
        )
        val syncService = SyncService(createMockHttpClient(response))

        val repository = SyncRepositoryImpl(
            syncService = syncService,
            questDao = questDao,
            questCategoryDao = categoryDao,
            subQuestDao = subQuestDao,
            playerStatsRepository = playerStatsRepo,
            appSettingsRepository = appSettingsRepo
        )

        // 3. Execute Sync
        val result = repository.sync()

        // 4. Verify
        println("[DEBUG_LOG] Result success: ${result.isSuccess}")
        if (result.isFailure) {
            println("[DEBUG_LOG] Failure: ${result.exceptionOrNull()}")
        }
        
        assertTrue(result.isSuccess)
        
        val cat1Status = categoryDao.categories.find { it.uuid == "cat-1" }?.syncStatus
        println("[DEBUG_LOG] cat-1 status: $cat1Status")
        assertEquals(SyncStatus.SYNCED, cat1Status)
        
        val quest1Status = questDao.quests.find { it.uuid == "quest-1" }?.syncStatus
        println("[DEBUG_LOG] quest-1 status: $quest1Status")
        assertEquals(SyncStatus.SYNCED, quest1Status)
        
        val cat2Text = categoryDao.categories.find { it.uuid == "cat-2" }?.text
        println("[DEBUG_LOG] cat-2 text: $cat2Text")
        assertEquals("Server Category", cat2Text)
        
        val lastSync = appSettingsRepo.settings.lastSyncTimestamp
        println("[DEBUG_LOG] last sync: $lastSync")
        assertEquals(responseTimestamp, lastSync)
        
        val statsDirty = playerStatsRepo.stats.isDirty
        println("[DEBUG_LOG] stats dirty: $statsDirty")
        assertEquals(false, statsDirty)
    }

    @Test
    fun testSyncSoftDelete() = runTest {
        val now = Instant.fromEpochMilliseconds(1000L)
        val responseTimestamp = Instant.fromEpochMilliseconds(2000L)

        val questDao = FakeQuestDao()
        val categoryDao = FakeQuestCategoryDao()
        val subQuestDao = FakeSubQuestDao()
        val playerStatsRepo = FakePlayerStatsRepository()
        val appSettingsRepo = FakeAppSettingsRepository()

        // 1. Setup local data with a deleted item
        val deletedQuest = QuestEntity(
            uuid = "deleted-quest",
            title = "Deleted",
            syncStatus = SyncStatus.DELETED_LOCALLY,
            deletedAt = now,
            difficulty = Difficulty.HARD,
            createdAt = now
        )
        questDao.upsert(deletedQuest)

        // 2. Setup Mock Server that returns another deleted item
        val response = SyncResponseDto(
            categories = emptyList(),
            quests = listOf(
                QuestDto(
                    uuid = "server-deleted-quest",
                    categoryUuid = null,
                    title = "Server Deleted",
                    notes = null,
                    difficulty = Difficulty.EASY,
                    done = false,
                    updatedAt = responseTimestamp,
                    deletedAt = responseTimestamp
                )
            ),
            subQuests = emptyList(),
            playerStats = null,
            newTimestamp = responseTimestamp
        )
        val syncService = SyncService(createMockHttpClient(response))

        val repository = SyncRepositoryImpl(
            syncService = syncService,
            questDao = questDao,
            questCategoryDao = categoryDao,
            subQuestDao = subQuestDao,
            playerStatsRepository = playerStatsRepo,
            appSettingsRepository = appSettingsRepo
        )

        // 3. Execute Sync
        repository.sync()

        // 4. Verify
        val localDeleted = questDao.quests.find { it.uuid == "deleted-quest" }
        assertEquals(SyncStatus.SYNCED, localDeleted?.syncStatus)
        assertEquals(now, localDeleted?.deletedAt)

        val serverDeleted = questDao.quests.find { it.uuid == "server-deleted-quest" }
        assertEquals(SyncStatus.SYNCED, serverDeleted?.syncStatus)
        assertEquals(responseTimestamp, serverDeleted?.deletedAt)
    }

    @Test
    fun testSyncErrorHandling() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        val syncService = SyncService(httpClient)

        val repository = SyncRepositoryImpl(
            syncService = syncService,
            questDao = FakeQuestDao(),
            questCategoryDao = FakeQuestCategoryDao(),
            subQuestDao = FakeSubQuestDao(),
            playerStatsRepository = FakePlayerStatsRepository(),
            appSettingsRepository = FakeAppSettingsRepository()
        )

        val result = repository.sync()
        assertTrue(result.isFailure)
    }
}
