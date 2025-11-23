package de.ljz.questify

import de.ljz.questify.core.notifications.IosNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosModule = module {
    single<NotificationScheduler> { IosNotificationScheduler() }
}

fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}