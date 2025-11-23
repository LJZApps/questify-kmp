package de.ljz.questify.feature.profile.di

import de.ljz.questify.core.data.datastore.createDataStore
import de.ljz.questify.core.data.datastore.dataStorePreferencesPath
import de.ljz.questify.feature.profile.data.models.AppUser
import de.ljz.questify.feature.profile.data.serializer.AppUserSerializer
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepositoryImpl
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appUserModule = module {
    single {
        createDataStore(
            producePath = { dataStorePreferencesPath("app_user.json") },
            serializer = AppUserSerializer,
            defaultValue = AppUser()
        )
    }

    singleOf(::AppUserRepositoryImpl) { bind<AppUserRepository>() }

    factoryOf(::GetAppUserUseCase)
    factoryOf(::SaveProfileUseCase)
}