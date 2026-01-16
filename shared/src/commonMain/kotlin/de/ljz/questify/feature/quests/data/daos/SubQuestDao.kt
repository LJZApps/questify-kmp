package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlin.time.Instant

@Dao
interface SubQuestDao {
    @Upsert
    suspend fun upsertSubQuest(subQuest: SubQuestEntity)

    @Upsert
    suspend fun upsertSubQuests(subQuests: List<SubQuestEntity>)

    @Transaction
    @Update
    suspend fun updateSubQuests(subQuests: List<SubQuestEntity>)


    @Query("UPDATE sub_quest_entity SET sync_status = 'DELETED_LOCALLY', deleted_at = :timestamp WHERE quest_id = :id")
    suspend fun markSubQuestsAsDeleted(id: Int, timestamp: Instant)

    @Query("UPDATE sub_quest_entity SET is_done = :checked, sync_status = 'DIRTY', updated_at = :updatedAt WHERE id = :id")
    suspend fun checkSubQuest(id: Int, checked: Boolean, updatedAt: Instant)

    @Query("SELECT * FROM sub_quest_entity WHERE sync_status != 'SYNCED'")
    suspend fun getSubQuestsToSync(): List<SubQuestEntity>

    @Query("SELECT * FROM sub_quest_entity WHERE uuid = :uuid")
    suspend fun getSubQuestByUuid(uuid: String): SubQuestEntity?

    @Query("UPDATE sub_quest_entity SET sync_status = 'SYNCED', updated_at = :updatedAt WHERE uuid = :uuid")
    suspend fun markAsSynced(uuid: String, updatedAt: Instant)

    @Query("UPDATE sub_quest_entity SET sync_status = 'DELETED_LOCALLY', deleted_at = :timestamp WHERE id = :id")
    suspend fun markSubQuestAsDeleted(id: Int, timestamp: Instant)
}
