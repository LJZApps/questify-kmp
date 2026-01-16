package de.ljz.questify.feature.player_stats.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.core.utils.TimeUtils
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import kotlinx.coroutines.flow.Flow

internal class PlayerStatsRepositoryImpl(
    private val playerStatsStore: DataStore<PlayerStats>
) : PlayerStatsRepository {
    override fun getPlayerStats(): Flow<PlayerStats> {
        return playerStatsStore.data
    }

    override suspend fun updatePlayerStats(newStats: PlayerStats) {
        playerStatsStore.updateData {
            newStats.copy(updatedAt = TimeUtils.now(), isDirty = true)
        }
    }

    override suspend fun markAsSynced(originalStats: PlayerStats) {
        playerStatsStore.updateData { current ->
            if (current.updatedAt == originalStats.updatedAt) {
                current.copy(isDirty = false)
            } else {
                current
            }
        }
    }

    override suspend fun updateFromSync(newStats: PlayerStats, originalStats: PlayerStats) {
        playerStatsStore.updateData { current ->
            if (current.updatedAt == originalStats.updatedAt) {
                newStats.copy(isDirty = false)
            } else {
                // Wenn sich lokale Daten geändert haben, behalten wir die lokalen Änderungen,
                // aber übernehmen vielleicht die Server-Werte für XP etc?
                // Der Einfachheit halber: Wenn ein Konflikt besteht, lassen wir es dirty.
                newStats.copy(isDirty = true, updatedAt = current.updatedAt)
            }
        }
    }
}