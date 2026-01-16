package de.ljz.questify.core.di

import androidx.room.RoomDatabase
import de.ljz.questify.appModule
import de.ljz.questify.core.auth.AndroidTokenStorage
import de.ljz.questify.core.auth.TokenStorage
import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getDatabaseBuilder
import org.koin.dsl.module

val androidModule = module {
//    single<NotificationScheduler> { AndroidNotificationScheduler(get()) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder(get()) }

    single<TokenStorage> { AndroidTokenStorage(get()) }
}

val sharedModules = appModule() + androidModule