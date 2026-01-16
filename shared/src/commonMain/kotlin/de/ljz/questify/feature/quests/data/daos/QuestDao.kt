package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
    @Query("SELECT * FROM quest_entity WHERE (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') AND deleted_at IS NULL ORDER BY title, notes DESC")
    suspend fun searchQuests(query: String): List<QuestWithDetails>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE deleted_at IS NULL")
    fun getAllQuests(): Flow<List<QuestWithDetails>>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id AND deleted_at IS NULL")
    suspend fun getQuestById(id: Int): QuestWithDetails

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE category_id = :categoryId AND deleted_at IS NULL")
    fun getQuestsForCategoryStream(categoryId: Int): Flow<List<QuestWithDetails>>

    @Query("UPDATE quest_entity SET title = :title, notes = :description, difficulty = :difficulty, due_date = :dueDate, category_id = :categoryId, sync_status = 'DIRTY', updated_at = :updatedAt WHERE id = :id")
    suspend fun updateQuestById(
        id: Int,
        title: String,
        description: String? = null,
        difficulty: Difficulty,
        dueDate: Instant? = null,
        categoryId: Int? = null,
        updatedAt: Instant
    )

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id AND deleted_at IS NULL")
    suspend fun suspendGetQuestById(id: Int): QuestWithDetails

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id AND deleted_at IS NULL")
    fun getQuestByIdFlow(id: Int): Flow<QuestWithDetails?>

    @Query("SELECT * FROM quest_entity WHERE uuid = :uuid")
    suspend fun getQuestByUuid(uuid: String): QuestEntity?

    @Transaction
    @Query("UPDATE quest_entity SET done = :done, sync_status = 'DIRTY', updated_at = :updatedAt WHERE id = :id")
    suspend fun setQuestDone(id: Int, done: Boolean, updatedAt: Instant)

    @Query("SELECT COUNT(*) FROM quest_entity WHERE done = 1")
    suspend fun getCompletedQuestsCount(): Int

    @Upsert
    suspend fun upsertMainQuest(value: QuestEntity): Long

    @Upsert
    suspend fun upsertQuests(value: List<QuestEntity>)

    @Transaction
    @Update
    suspend fun updateQuests(quests: List<QuestEntity>)


    @Query("SELECT * FROM quest_entity WHERE sync_status != 'SYNCED'")
    suspend fun getQuestsToSync(): List<QuestEntity>

    @Query("UPDATE quest_entity SET sync_status = 'DELETED_LOCALLY', deleted_at = :timestamp WHERE id = :id")
    suspend fun markQuestAsDeleted(id: Int, timestamp: Instant)


    @Query("UPDATE quest_entity SET sync_status = 'SYNCED', updated_at = :updatedAt WHERE uuid = :uuid")
    suspend fun markAsSynced(uuid: String, updatedAt: Instant)
}
