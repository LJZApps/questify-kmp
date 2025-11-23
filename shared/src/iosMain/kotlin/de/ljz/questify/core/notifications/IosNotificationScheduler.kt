package de.ljz.questify.core.notifications

import platform.UserNotifications.UNUserNotificationCenter

class IosNotificationScheduler : NotificationScheduler {
    override fun cancelNotification(notificationId: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val ids = listOf(notificationId.toString())
        center.removePendingNotificationRequestsWithIdentifiers(ids)
    }
}