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
    @Query("SELECT * FROM quest_entity WHERE title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY title, notes DESC")
    suspend fun searchQuests(query: String): List<QuestWithDetails>

    @Transaction
    @Query("SELECT * FROM quest_entity")
    fun getAllQuests(): Flow<List<QuestWithDetails>>

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE id = :id")
    suspend fun getQuestById(id: Int): QuestWithDetails

    @Transaction
    @Query("SELECT * FROM quest_entity WHERE category_id = :categoryId")
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
    @Query("UPDATE quest_entity SET done = :done WHERE id = :id")
    suspend fun setQuestDone(id: Int, done: Boolean)

    @Query("SELECT COUNT(*) FROM quest_entity WHERE done = 1")
    suspend fun getCompletedQuestsCount(): Int

    @Upsert
    suspend fun upsertMainQuest(value: QuestEntity): Long

    @Upsert
    suspend fun upsertQuests(value: List<QuestEntity>)

    @Transaction
    @Query("DELETE FROM quest_entity WHERE id = :questId")
    suspend fun deleteQuest(questId: Int)
}