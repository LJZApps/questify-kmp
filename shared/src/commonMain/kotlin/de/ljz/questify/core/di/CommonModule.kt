package de.ljz.questify.core.di

import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getRoomDatabase
import de.ljz.questify.feature.quests.domain.use_cases.CancelQuestNotificationsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val commonModule = module{
    factoryOf(::CancelQuestNotificationsUseCase)

    single<AppDatabase> { getRoomDatabase(get()) }
}