package de.ljz.questify.core.di

import de.ljz.questify.core.notifications.AndroidNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import org.koin.dsl.module

val androidModule = module {
    single<NotificationScheduler> { AndroidNotificationScheduler(get()) }
}