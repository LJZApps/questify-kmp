package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.domain.repositories.QuestNotificationRepository

class RemoveNotificationsUseCase(
    private val questNotificationRepository: QuestNotificationRepository
) {
    suspend operator fun invoke(id: Int) {
        questNotificationRepository.removeNotifications(questId = id)
    }
}