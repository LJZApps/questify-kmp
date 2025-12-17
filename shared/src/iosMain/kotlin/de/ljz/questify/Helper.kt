package de.ljz.questify

import androidx.room.RoomDatabase
import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getDatabaseBuilder
import de.ljz.questify.core.notifications.IosNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import de.ljz.questify.feature.quests.domain.use_cases.UpsertQuestUseCase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.reflect.KClass

private val iosModule = module {
    single<NotificationScheduler> { IosNotificationScheduler() }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}

fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}

class KoinDependencies {
    private fun <T: Any> get(clazz: KClass<T>): T {
        return KoinPlatform.getKoin().get(clazz, null, null)
    }

    val upsertQuestUseCase: UpsertQuestUseCase
        get() = get(UpsertQuestUseCase::class)
}