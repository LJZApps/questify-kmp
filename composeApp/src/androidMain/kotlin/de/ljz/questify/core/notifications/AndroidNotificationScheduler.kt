package de.ljz.questify.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build

class AndroidNotificationScheduler(
    private val context: Context
) : NotificationScheduler {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun testLiveNotification() {
        val intent = Intent(context, QuestLiveService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Live Quests"
            val descriptionText = "Zeigt aktive Quests und Deadlines"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("live_quest_channel", name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

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