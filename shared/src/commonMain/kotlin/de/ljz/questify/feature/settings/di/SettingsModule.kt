package de.ljz.questify.feature.settings.di

import de.ljz.questify.core.domain.use_cases.GetSortingPreferencesUseCase
import de.ljz.questify.core.domain.use_cases.SaveQuestSortingDirectionUseCase
import de.ljz.questify.core.domain.use_cases.SetOnboardingDoneUseCase
import de.ljz.questify.core.domain.use_cases.UpdateShowCompletedQuestsUseCase
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepositoryImpl
import de.ljz.questify.feature.settings.presentation.screens.main.SettingsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    singleOf(::AppSettingsRepositoryImpl) { bind<AppSettingsRepository>() }

    single<AppSettingsRepository> {
        AppSettingsRepositoryImpl(
            appSettingsDataStore = get(named("app_settings"))
        )
    }

    factoryOf(::GetSortingPreferencesUseCase)
    factoryOf(::SaveQuestSortingDirectionUseCase)
    factoryOf(::SetOnboardingDoneUseCase)
    factoryOf(::UpdateShowCompletedQuestsUseCase)

    viewModelOf(::SettingsViewModel)
}