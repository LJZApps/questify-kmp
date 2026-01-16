package de.ljz.questify.feature.habits.di

import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.network.HttpClientFactory
import de.ljz.questify.core.domain.repositories.AuthRepository
import de.ljz.questify.core.domain.repositories.AuthRepositoryImpl
import de.ljz.questify.feature.habits.data.daos.HabitDao
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository
import de.ljz.questify.feature.habits.domain.repositories.HabitRepositoryImpl
import de.ljz.questify.feature.habits.domain.use_cases.DeleteHabitUseCase
import de.ljz.questify.feature.habits.domain.use_cases.GetAllActiveHabitsUseCase
import de.ljz.questify.feature.habits.domain.use_cases.GetAllArchivedHabitsUseCase
import de.ljz.questify.feature.habits.domain.use_cases.GetHabitByIdOnceUseCase
import de.ljz.questify.feature.habits.domain.use_cases.GetHabitByIdUseCase
import de.ljz.questify.feature.habits.domain.use_cases.UpdateArchivedStatusUseCase
import de.ljz.questify.feature.habits.domain.use_cases.UpsertHabitUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val habitModule = module {
    single<HabitDao> {
        get<AppDatabase>().habitDao
    }

    singleOf(::HabitRepositoryImpl) { bind<HabitRepository>() }

    factoryOf(::GetAllActiveHabitsUseCase)
    factoryOf(::GetAllArchivedHabitsUseCase)
    factoryOf(::GetHabitByIdUseCase)
    factoryOf(::GetHabitByIdOnceUseCase)
    factoryOf(::UpsertHabitUseCase)
    factoryOf(::DeleteHabitUseCase)
    factoryOf(::UpdateArchivedStatusUseCase)

    single<AuthRepository> {
        AuthRepositoryImpl(
            client = get(named("authClient")),
            tokenStorage = get()
        )
    }

    single(named("authClient")) {
        HttpClientFactory.createAuthHttpClient()
    }

    single {
        HttpClientFactory.createAppHttpClient(
            tokenStorage = get(),
            authRepository = get()
        )
    }
}