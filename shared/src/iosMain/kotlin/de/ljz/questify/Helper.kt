package de.ljz.questify

import androidx.room.RoomDatabase
import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getDatabaseBuilder
import de.ljz.questify.core.notifications.IosNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val iosModule = module {
    single<NotificationScheduler> { IosNotificationScheduler() }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}


fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}