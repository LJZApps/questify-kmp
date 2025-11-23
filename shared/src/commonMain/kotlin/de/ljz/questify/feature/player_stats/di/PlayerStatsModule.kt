package de.ljz.questify.feature.player_stats.di

import de.ljz.questify.core.data.datastore.createDataStore
import de.ljz.questify.core.data.datastore.dataStorePreferencesPath
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import de.ljz.questify.feature.player_stats.data.serializer.PlayerStatsSerializer
import de.ljz.questify.feature.player_stats.domain.repositories.PlayerStatsRepository
import de.ljz.questify.feature.player_stats.domain.repositories.PlayerStatsRepositoryImpl
import de.ljz.questify.feature.player_stats.domain.use_cases.GetPlayerStatsUseCase
import de.ljz.questify.feature.player_stats.domain.use_cases.UpdatePlayerStatsUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val playerStatsModule = module {
    single {
        createDataStore(
            producePath = { dataStorePreferencesPath("player_stats.json") },
            serializer = PlayerStatsSerializer,
            defaultValue = PlayerStats()
        )
    }

    singleOf(::PlayerStatsRepositoryImpl) { bind<PlayerStatsRepository>() }

    factoryOf(::UpdatePlayerStatsUseCase)
    factoryOf(::GetPlayerStatsUseCase)
}