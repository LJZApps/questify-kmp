package de.ljz.questify.feature.player_stats.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.feature.player_stats.data.models.PlayerStats
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object PlayerStatsSerializer : OkioSerializer<PlayerStats> {
    override val defaultValue: PlayerStats = PlayerStats()

    override suspend fun readFrom(source: BufferedSource): PlayerStats {
        return try {
            Json.decodeFromString(
                PlayerStats.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: PlayerStats, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(PlayerStats.serializer(), t))
        }
    }
}