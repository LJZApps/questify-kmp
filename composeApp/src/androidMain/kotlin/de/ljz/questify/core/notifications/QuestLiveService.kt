package de.ljz.questify.core.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import de.ljz.questify.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class QuestLiveService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var updateJob: Job? = null

    // Channel IDs
    private val CHANNEL_ID_LIVE = "live_quest_channel"
    private val CHANNEL_ID_EXPIRED = "expired_quest_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannels() // Beide Channels anlegen
        startAnimatedTest()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        serviceScope.cancel()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // 1. Der "Leise" Channel für Live-Updates (Chip)
            val liveChannel = NotificationChannel(CHANNEL_ID_LIVE, "Aktive Quests", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Zeigt laufende Timer und Updates"
                setSound(null, null) // WICHTIG: Stumm, damit Updates nicht nerven
                enableVibration(false)
            }

            // 2. Der "Laute" Channel für das Ende (Popup)
            val expiredChannel = NotificationChannel(CHANNEL_ID_EXPIRED, "Abgelaufene Quests", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Benachrichtigung wenn eine Quest fällig ist"
                enableVibration(true) // Soll vibrieren
                // Hier könnte man auch einen Sound setzen
            }

            notificationManager.createNotificationChannels(listOf(liveChannel, expiredChannel))
        }
    }

    private fun startAnimatedTest() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1337

        // TEST: Nur 15 Sekunden, damit du nicht lange warten musst
        val startTime = System.currentTimeMillis()
        val totalDurationMillis = 15 * 1000L
        val dueTime = startTime + totalDurationMillis

        updateJob?.cancel()
        updateJob = serviceScope.launch {

            // --- PHASE 1: LIVE UPDATES (Leise) ---
            while (isActive && System.currentTimeMillis() < dueTime) {
                val now = System.currentTimeMillis()
                val elapsed = now - startTime
                val remainingSeconds = ((dueTime - now) / 1000).toInt()
                val progress = (elapsed.toFloat() / totalDurationMillis.toFloat() * 100).toInt().coerceIn(0, 100)

                val notification = buildLiveNotification(
                    dueTime = dueTime,
                    progress = progress,
                    remainingSeconds = remainingSeconds
                )

                if (elapsed < 1000) {
                    startForeground(notificationId, notification)
                } else {
                    notificationManager.notify(notificationId, notification)
                }

                delay(1000)
            }

            val expiredNotification = buildExpiredNotification()

            notificationManager.cancel(notificationId)
            stopForeground(STOP_FOREGROUND_REMOVE)
            notificationManager.notify(Random.nextInt(), expiredNotification)

            stopSelf()
        }
    }

    private fun buildLiveNotification(dueTime: Long, progress: Int, remainingSeconds: Int): Notification {
        val progressColor = when {
            remainingSeconds < 5 -> getColor(android.R.color.holo_red_light)
            remainingSeconds < 10 -> getColor(android.R.color.holo_orange_light)
            else -> getColor(R.color.notification_color)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_LIVE)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Wäsche waschen")
            .setContentText("Noch $remainingSeconds Sekunden...")
            .setSubText("Endspurt")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setColor(getColor(R.color.notification_color))

        if (Build.VERSION.SDK_INT >= 36) {
            val progressStyle = NotificationCompat.ProgressStyle()
                .setStyledByProgress(false)
                .setProgress(progress)
                .setProgressSegments(listOf(
                    NotificationCompat.ProgressStyle.Segment(progress).setColor(progressColor),
                    NotificationCompat.ProgressStyle.Segment((100 - progress)).setColor(getColor(android.R.color.darker_gray))
                ))
            builder.setStyle(progressStyle)
            builder.setRequestPromotedOngoing(true)
        }

        return builder.build()
    }

    private fun buildExpiredNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID_EXPIRED)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Quest überfällig!")
            .setContentText("Die Quest 'Wäsche waschen' ist abgelaufen.")
            .setStyle(
                /* style = */ NotificationCompat.BigTextStyle()
                    .bigText("Die Quest 'Wäsche waschen' ist abgelaufen.\nDu bekommst jetzt nurnoch die halbe Belohnung.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(getColor(R.color.notification_color))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()
    }
}