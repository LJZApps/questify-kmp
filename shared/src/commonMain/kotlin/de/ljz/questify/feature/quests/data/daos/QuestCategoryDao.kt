package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestCategoryDao {
    @Upsert
    suspend fun upsertQuestCategory(questCategory: QuestCategoryEntity)

    @Query("DELETE FROM quest_category_entity WHERE id = :questCategoryId")
    suspend fun deleteQuestCategory(questCategoryId: Int)

    @Query("SELECT * FROM quest_category_entity WHERE sync_status != 'TO_BE_DELETED'")
    fun getAllQuestCategories(): Flow<List<QuestCategoryEntity>>

    @Query("SELECT * FROM quest_category_entity WHERE id = :id")
    fun getQuestCategoryById(id: Int): Flow<QuestCategoryEntity?>

    @Transaction
    @Update
    suspend fun updateCategories(categories: List<QuestCategoryEntity>)

    @Query("SELECT COUNT(*) FROM quest_category_entity WHERE sync_status != 'TO_BE_DELETED'")
    suspend fun getCategoryCount(): Long

    @Query("SELECT * FROM quest_category_entity WHERE sync_status != 'SYNCED' AND sync_status != 'TO_BE_DELETED'")
    suspend fun getUnsyncedCategories(): List<QuestCategoryEntity>

    @Query("SELECT * FROM quest_category_entity WHERE sync_status = 'TO_BE_DELETED'")
    suspend fun getCategoriesMarkedForDeletion(): List<QuestCategoryEntity>

    @Query("UPDATE quest_category_entity SET sync_status = 'TO_BE_DELETED' WHERE id = :id")
    suspend fun markCategoryForDeletion(id: Int)

    @Query("SELECT * FROM quest_category_entity WHERE remote_id = :remoteId")
    suspend fun getCategoryByRemoteId(remoteId: Int): QuestCategoryEntity?

    @Query("UPDATE quest_category_entity SET sync_status = :syncStatus, remote_id = :remoteId WHERE id = :id")
    suspend fun updateSyncStatus(id: Int, syncStatus: String, remoteId: Int?)

    @Query("UPDATE quest_category_entity SET text = :text WHERE id = :questCategoryId")
    suspend fun updateQuestCategory(questCategoryId: Int, text: String)

    @Query("SELECT remote_id FROM quest_category_entity WHERE remote_id IS NOT NULL")
    suspend fun getAllRemoteIds(): List<Int>
}