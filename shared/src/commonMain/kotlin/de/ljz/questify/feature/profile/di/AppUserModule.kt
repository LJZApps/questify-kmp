package de.ljz.questify.feature.profile.di

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepositoryImpl
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appUserModule = module {
    singleOf(::AppUserRepositoryImpl) { bind<AppUserRepository>() }

    factoryOf(::GetAppUserUseCase)
    factoryOf(::SaveProfileUseCase)
}