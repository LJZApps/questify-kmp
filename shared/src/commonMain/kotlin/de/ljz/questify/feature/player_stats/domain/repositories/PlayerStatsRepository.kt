package de.ljz.questify.feature.player_stats.domain.repositories

import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import kotlinx.coroutines.flow.Flow

interface PlayerStatsRepository {
    fun getPlayerStats(): Flow<PlayerStats>

    suspend fun updatePlayerStats(newStats: PlayerStats)
    suspend fun markAsSynced(originalStats: PlayerStats)
    suspend fun updateFromSync(newStats: PlayerStats, originalStats: PlayerStats)
}