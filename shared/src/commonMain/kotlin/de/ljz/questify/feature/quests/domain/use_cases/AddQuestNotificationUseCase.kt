package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import de.ljz.questify.feature.quests.domain.repositories.QuestNotificationRepository

class AddQuestNotificationUseCase(
    private val questNotificationRepository: QuestNotificationRepository
) {
    suspend operator fun invoke(questNotificationEntity: QuestNotificationEntity) {
        questNotificationRepository.addQuestNotification(questNotifications = questNotificationEntity)
    }
}