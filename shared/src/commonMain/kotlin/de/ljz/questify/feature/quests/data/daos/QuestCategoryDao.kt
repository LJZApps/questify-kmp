package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface QuestCategoryDao {
    @Upsert
    suspend fun upsertQuestCategory(questCategory: QuestCategoryEntity): Long


    @Query("SELECT * FROM quest_category_entity WHERE deleted_at IS NULL")
    fun getAllQuestCategories(): Flow<List<QuestCategoryEntity>>

    @Query("SELECT * FROM quest_category_entity WHERE id = :id AND deleted_at IS NULL")
    fun getQuestCategoryById(id: Int): Flow<QuestCategoryEntity?>

    @Query("SELECT * FROM quest_category_entity WHERE uuid = :uuid")
    suspend fun getQuestCategoryByUuid(uuid: String): QuestCategoryEntity?

    @Transaction
    @Update
    suspend fun updateCategories(categories: List<QuestCategoryEntity>)

    @Query("SELECT COUNT(*) FROM quest_category_entity")
    suspend fun getCategoryCount(): Long

    @Query("UPDATE quest_category_entity SET text = :text, sync_status = 'DIRTY', updated_at = :updatedAt WHERE id = :questCategoryId")
    suspend fun updateQuestCategory(questCategoryId: Int, text: String, updatedAt: Instant)

    @Query("SELECT * FROM quest_category_entity WHERE sync_status != 'SYNCED'")
    suspend fun getCategoriesToSync(): List<QuestCategoryEntity>

    @Query("UPDATE quest_category_entity SET sync_status = 'SYNCED', updated_at = :updatedAt WHERE uuid = :uuid")
    suspend fun markAsSynced(uuid: String, updatedAt: Instant)

    @Query("UPDATE quest_category_entity SET sync_status = 'DELETED_LOCALLY', deleted_at = :timestamp WHERE id = :id")
    suspend fun markCategoryAsDeleted(id: Int, timestamp: Instant)
}
