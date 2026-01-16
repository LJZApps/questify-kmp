package de.ljz.questify.feature.profile.di

import de.ljz.questify.feature.profile.data.network.ProfileService
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepositoryImpl
import de.ljz.questify.feature.profile.domain.use_cases.CheckUsernameUseCase
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SyncProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val profileModule = module {
    single { ProfileService(httpClient = get()) }

    single<AppUserRepository> {
        AppUserRepositoryImpl(
            appUserDataStore = get(named("app_user")),
            profileService = get()
        )
    }

    factoryOf(::GetAppUserUseCase)
    factoryOf(::SaveProfileUseCase)
    factoryOf(::SyncProfileUseCase)
    factoryOf(::CheckUsernameUseCase)
}