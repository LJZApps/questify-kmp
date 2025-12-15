package de.ljz.questify.feature.quests.domain.use_cases

class CancelQuestNotificationsUseCase(
//    private val notificationScheduler: NotificationScheduler,
    private val getNotificationsByQuestIdUseCase: GetNotificationsByQuestIdUseCase,
    private val removeNotificationsUseCase: RemoveNotificationsUseCase
) {
    suspend operator fun invoke(id: Int) {
        val notifications = getNotificationsByQuestIdUseCase(id)

        notifications.forEach { notificationEntity ->
            // Die Drecksarbeit macht jetzt die Implementierung
//            notificationScheduler.cancelNotification(notificationEntity.id)
        }

        removeNotificationsUseCase(id = id)
    }
}