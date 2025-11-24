package de.ljz.questify.core.di

import androidx.room.RoomDatabase
import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getDatabaseBuilder
import de.ljz.questify.core.notifications.AndroidNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import org.koin.dsl.module

val androidModule = module {
    single<NotificationScheduler> { AndroidNotificationScheduler(get()) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder(get()) }
}