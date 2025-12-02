package de.ljz.questify.core.di

import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getRoomDatabase
import de.ljz.questify.core.domain.repositories.app.SortingPreferencesRepository
import de.ljz.questify.core.domain.repositories.app.SortingPreferencesRepositoryImpl
import de.ljz.questify.core.presentation.screens.AppViewModel
import de.ljz.questify.core.presentation.theme.ThemeViewModel
import de.ljz.questify.feature.quests.domain.use_cases.CancelQuestNotificationsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    factoryOf(::CancelQuestNotificationsUseCase)

    single<AppDatabase> { getRoomDatabase(get()) }

    single<SortingPreferencesRepository> {
        SortingPreferencesRepositoryImpl(
            sortingDataStore = get(named("sorting_preferences"))
        )
    }

    viewModelOf(::ThemeViewModel)
    viewModelOf(::AppViewModel)
}