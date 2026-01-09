package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface QuestDao {
    @Upsert
    suspend fun upsert(quest: QuestEntity): Long

    @Upsert
    suspend fun upsertAll(quests: List<QuestEntity>)

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') AND sync_status != 'TO_BE_DELETED' ORDER BY title, notes DESC")
    suspend fun searchQuests(query: String): List<QuestWithDetails>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE sync_status != 'TO_BE_DELETED'")
    fun getAllQuests(): Flow<List<QuestWithDetails>>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id")
    suspend fun getQuestById(id: Int): QuestWithDetails

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE category_id = :categoryId AND sync_status != 'TO_BE_DELETED'")
    fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>>

    @Query("UPDATE quest_entity SET title = :title, notes = :description, difficulty = :difficulty, due_date = :dueDate, category_id = :categoryId WHERE id = :id")
    suspend fun updateQuestById(
        id: Int,
        title: String,
        description: String? = null,
        difficulty: Difficulty,
        dueDate: Instant? = null,
        categoryId: Int? = null
    )

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id")
    suspend fun suspendGetQuestById(id: Int): QuestWithDetails

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id")
    fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?>

    @Transaction
    @Query("UPDATE quest_entity SET done = :done, sync_status = 'UNSYNCED' WHERE id = :id")
    suspend fun setQuestDone(id: Int, done: Boolean)

    @Query("SELECT COUNT(*) FROM quest_entity WHERE done = 1 AND sync_status != 'TO_BE_DELETED'")
    suspend fun getCompletedQuestsCount(): Int

    @Upsert
    suspend fun upsertMainQuest(value: QuestEntity): Long

    @Upsert
    suspend fun upsertQuests(value: List<QuestEntity>)

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE sync_status != 'SYNCED' AND sync_status != 'TO_BE_DELETED'")
    suspend fun getUnsyncedQuests(): List<QuestWithDetails>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE sync_status = 'TO_BE_DELETED'")
    suspend fun getQuestsMarkedForDeletion(): List<QuestWithDetails>

    @Query("UPDATE quest_entity SET sync_status = 'TO_BE_DELETED' WHERE id = :id")
    suspend fun markQuestForDeletion(id: Int)

    @Query("SELECT * FROM quest_entity WHERE remote_id = :remoteId")
    suspend fun getQuestByRemoteId(remoteId: Int): QuestEntity?

    @Query("UPDATE quest_entity SET sync_status = :syncStatus, remote_id = :remoteId WHERE id = :id")
    suspend fun updateSyncStatus(id: Int, syncStatus: String, remoteId: Int?)

    @Transaction
    @Query("DELETE FROM quest_entity WHERE id = :questId")
    suspend fun deleteQuest(questId: Int)

    @Query("SELECT remote_id FROM quest_entity WHERE remote_id IS NOT NULL")
    suspend fun getAllRemoteIds(): List<Int>
}