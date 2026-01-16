package de.ljz.questify.core.di

import de.ljz.questify.core.data.network.SyncService
import de.ljz.questify.core.domain.repositories.SyncRepository
import de.ljz.questify.core.domain.repositories.SyncRepositoryImpl
import de.ljz.questify.core.domain.use_cases.SyncUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val syncModule = module {
    single { SyncService(get()) }
    single<SyncRepository> { SyncRepositoryImpl(get(), get(), get(), get(), get(), get()) }

    factoryOf(::SyncUseCase)
}
