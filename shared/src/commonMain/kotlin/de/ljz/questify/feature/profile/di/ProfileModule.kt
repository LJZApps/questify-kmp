package de.ljz.questify.feature.profile.di

import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepositoryImpl
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import de.ljz.questify.feature.profile.presentation.screens.edit_profile.EditProfileViewModel
import de.ljz.questify.feature.profile.presentation.screens.view_profile.ViewProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {
    single<AppUserRepository> {
        AppUserRepositoryImpl(
            appUserDataStore = get(named("app_user"))
        )
    }

    factoryOf(::GetAppUserUseCase)
    factoryOf(::SaveProfileUseCase)

    viewModelOf(::ViewProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}