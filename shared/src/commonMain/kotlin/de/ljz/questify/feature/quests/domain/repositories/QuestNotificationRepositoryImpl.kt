package de.ljz.questify.feature.quests.domain.repositories

import de.ljz.questify.core.utils.TimeUtils
import de.ljz.questify.feature.quests.data.daos.QuestNotificationDao
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import kotlinx.coroutines.flow.Flow

internal class QuestNotificationRepositoryImpl(
    private val questNotificationDao: QuestNotificationDao
) : QuestNotificationRepository {
    override fun getPendingNotifications(): Flow<List<QuestNotificationEntity>> {
        return questNotificationDao.getPendingNotifications(TimeUtils.now())
    }

    override suspend fun getNotificationById(id: Int): QuestNotificationEntity {
        return questNotificationDao.getNotificationById(id) ?: throw NoSuchElementException("Notification not found")
    }

    override suspend fun removeNotifications(questId: Int) {
        questNotificationDao.deleteNotificationsForQuest(questId)
    }

    override suspend fun setNotificationAsNotified(id: Int): Int {
        return questNotificationDao.setNotificationAsNotified(id)
    }

    override suspend fun isNotified(id: Int): Boolean {
        return questNotificationDao.isNotified(id)
    }

    override suspend fun addQuestNotification(questNotifications: QuestNotificationEntity): Long {
        return questNotificationDao.upsert(questNotifications)
    }

    override suspend fun getNotificationsByQuestId(questId: Int) =
        questNotificationDao.getNotificationsByQuestId(questId)

    override suspend fun addQuestNotifications(notifications: List<QuestNotificationEntity>): List<Long> {
        return questNotificationDao.upsertAll(notifications)
    }
}
