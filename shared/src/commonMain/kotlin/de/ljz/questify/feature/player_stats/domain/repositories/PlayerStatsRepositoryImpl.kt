package de.ljz.questify.feature.player_stats.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import kotlinx.coroutines.flow.Flow

class PlayerStatsRepositoryImpl(
    private val playerStatsStore: DataStore<PlayerStats>
) : PlayerStatsRepository {
    override fun getPlayerStats(): Flow<PlayerStats> {
        return playerStatsStore.data
    }

    override suspend fun updatePlayerStats(newStats: PlayerStats) {
        playerStatsStore.updateData {
            newStats
        }
    }
}