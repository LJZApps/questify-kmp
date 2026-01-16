package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class QuestRepositoryImplTest {

    class FakeQuestDao : QuestDao {
        var lastMarkedDeletedId: Int? = null
        var lastMarkedDeletedTimestamp: Instant? = null

        override suspend fun upsert(quest: QuestEntity): Long = 1L
        override suspend fun upsertAll(quests: List<QuestEntity>) {}
        override suspend fun searchQuests(query: String): List<QuestWithDetails> = emptyList()
        override fun getAllQuests(): Flow<List<QuestWithDetails>> = flowOf(emptyList())
        override suspend fun getQuestById(id: Int): QuestWithDetails = throw Exception()
        override fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>> = flowOf(emptyList())
        override suspend fun updateQuestById(id: Int, title: String, description: String?, difficulty: Difficulty, dueDate: Instant?, categoryId: Int?, updatedAt: Instant) {}
        override suspend fun suspendGetQuestById(id: Int): QuestWithDetails = throw Exception()
        override fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?> = flowOf(null)
        override suspend fun getQuestByUuid(uuid: String): QuestEntity? = null
        override suspend fun setQuestDone(id: Int, done: Boolean, updatedAt: Instant) {}
        override suspend fun getCompletedQuestsCount(): Int = 0
        override suspend fun upsertMainQuest(value: QuestEntity): Long = 1L
        override suspend fun upsertQuests(value: List<QuestEntity>) {}
        override suspend fun updateQuests(quests: List<QuestEntity>) {}
        override suspend fun getQuestsToSync(): List<QuestEntity> = emptyList()
        override suspend fun markQuestAsDeleted(id: Int, timestamp: Instant) {
            lastMarkedDeletedId = id
            lastMarkedDeletedTimestamp = timestamp
        }
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant, lastLocalUpdate: Instant) {}
    }

    @Test
    fun testDeleteQuestSoftDelete() = runTest {
        val dao = FakeQuestDao()
        val repository = QuestRepositoryImpl(dao)

        repository.deleteQuest(123)

        assertEquals(123, dao.lastMarkedDeletedId)
        assertEquals(true, dao.lastMarkedDeletedTimestamp != null)
    }
}

class QuestCategoryRepositoryImplTest {
    class FakeQuestCategoryDao : de.ljz.questify.feature.quests.data.daos.QuestCategoryDao {
        var lastMarkedDeletedId: Int? = null
        override suspend fun upsertQuestCategory(questCategory: de.ljz.questify.feature.quests.data.models.QuestCategoryEntity): Long = 1L
        override fun getAllQuestCategories(): Flow<List<de.ljz.questify.feature.quests.data.models.QuestCategoryEntity>> = flowOf(emptyList())
        override fun getQuestCategoryById(id: Int): Flow<de.ljz.questify.feature.quests.data.models.QuestCategoryEntity?> = flowOf(null)
        override suspend fun getQuestCategoryByUuid(uuid: String): de.ljz.questify.feature.quests.data.models.QuestCategoryEntity? = null
        override suspend fun updateCategories(categories: List<de.ljz.questify.feature.quests.data.models.QuestCategoryEntity>) {}
        override suspend fun getCategoryCount(): Long = 0
        override suspend fun updateQuestCategory(questCategoryId: Int, text: String, updatedAt: Instant) {}
        override suspend fun getCategoriesToSync(): List<de.ljz.questify.feature.quests.data.models.QuestCategoryEntity> = emptyList()
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant, lastLocalUpdate: Instant) {}
        override suspend fun markCategoryAsDeleted(id: Int, timestamp: Instant) {
            lastMarkedDeletedId = id
        }
    }

    @Test
    fun testDeleteCategory() = runTest {
        val dao = FakeQuestCategoryDao()
        val repository = QuestCategoryRepositoryImpl(dao)
        repository.deleteQuestCategory(456)
        assertEquals(456, dao.lastMarkedDeletedId)
    }
}

class SubQuestRepositoryImplTest {
    class FakeSubQuestDao : de.ljz.questify.feature.quests.data.daos.SubQuestDao {
        var lastMarkedDeletedId: Int? = null
        override suspend fun upsertSubQuest(subQuest: de.ljz.questify.feature.quests.data.models.SubQuestEntity) {}
        override suspend fun upsertSubQuests(subQuests: List<de.ljz.questify.feature.quests.data.models.SubQuestEntity>) {}
        override suspend fun updateSubQuests(subQuests: List<de.ljz.questify.feature.quests.data.models.SubQuestEntity>) {}
        override suspend fun markSubQuestsAsDeleted(id: Int, timestamp: Instant) {
            lastMarkedDeletedId = id
        }
        override suspend fun checkSubQuest(id: Int, checked: Boolean, updatedAt: Instant) {}
        override suspend fun getSubQuestsToSync(): List<de.ljz.questify.feature.quests.data.models.SubQuestEntity> = emptyList()
        override suspend fun getSubQuestByUuid(uuid: String): de.ljz.questify.feature.quests.data.models.SubQuestEntity? = null
        override suspend fun markAsSynced(uuid: String, updatedAt: Instant, lastLocalUpdate: Instant) {}
        override suspend fun markSubQuestAsDeleted(id: Int, timestamp: Instant) {}
    }

    @Test
    fun testDeleteSubQuests() = runTest {
        val dao = FakeSubQuestDao()
        val repository = SubQuestRepositoryImpl(dao)
        repository.deleteSubQuests(789)
        assertEquals(789, dao.lastMarkedDeletedId)
    }
}
