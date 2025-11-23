package de.ljz.questify.core.notifications

import android.content.Context

class AndroidNotificationScheduler(
    private val context: Context
): NotificationScheduler {
    override fun cancelNotification(notificationId: Int) {
        // TODO add later - IMPORTANT
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, QuestNotificationReceiver::class.java)
//
//        // WICHTIG: Die Flags müssen exakt übereinstimmen mit denen beim Erstellen!
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            notificationId,
//            intent,
//            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        if (pendingIntent != null) {
//            alarmManager.cancel(pendingIntent)
//        }
    }
}