package de.ljz.questify.feature.player_stats.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import kotlinx.coroutines.flow.Flow

internal class PlayerStatsRepositoryImpl(
    private val playerStatsStore: DataStore<PlayerStats>,
    private val remoteDataSource: de.ljz.questify.core.data.remote.QuestifyRemoteDataSource
) : PlayerStatsRepository {
    override fun getPlayerStats(): Flow<PlayerStats> {
        return playerStatsStore.data
    }

    override suspend fun updatePlayerStats(newStats: PlayerStats) {
        playerStatsStore.updateData {
            newStats
        }
        
        val dto = de.ljz.questify.core.data.remote.models.PlayerStatsDTO(
            level = newStats.level,
            xp = newStats.xp,
            points = newStats.points,
            currentHp = newStats.currentHP,
            maxHp = newStats.maxHP,
            status = newStats.status.name,
            statusExpiryTimestamp = newStats.statusExpiryTimestamp
        )
        // Fire and forget update
        remoteDataSource.updatePlayerStats(dto)
    }
}
