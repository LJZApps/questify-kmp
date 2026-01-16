package de.ljz.questify.feature.quests.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface QuestNotificationDao {
    @Upsert
    suspend fun upsert(questNotification: QuestNotificationEntity): Long

    @Upsert
    suspend fun upsertAll(notifications: List<QuestNotificationEntity>): List<Long>

    @Query("SELECT * FROM quest_notifications WHERE notify_at > :currentTime")
    fun getPendingNotifications(currentTime: Instant): Flow<List<QuestNotificationEntity>>

    @Query("SELECT * FROM quest_notifications WHERE id = :id")
    suspend fun getNotificationById(id: Int): QuestNotificationEntity?

    @Query("DELETE FROM quest_notifications WHERE quest_id = :questId")
    suspend fun deleteNotificationsForQuest(questId: Int)

    @Query("UPDATE quest_notifications SET notified = 1 WHERE id = :id")
    suspend fun setNotificationAsNotified(id: Int): Int

    @Query("SELECT notified FROM quest_notifications WHERE id = :id")
    suspend fun isNotified(id: Int): Boolean

    @Query("SELECT * FROM quest_notifications WHERE quest_id = :questId")
    suspend fun getNotificationsByQuestId(questId: Int): List<QuestNotificationEntity>
}
