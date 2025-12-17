package de.ljz.questify.feature.profile.di

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepositoryImpl
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val profileModule = module {
    single<AppUserRepository> {
        AppUserRepositoryImpl(
            appUserDataStore = get(named("app_user"))
        )
    }

    factoryOf(::GetAppUserUseCase)
    factoryOf(::SaveProfileUseCase)
}