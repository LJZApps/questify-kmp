package de.ljz.questify.core.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.core.data.models.SortingPreferences
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object SortingPreferencesSerializer : OkioSerializer<SortingPreferences> {
    override val defaultValue: SortingPreferences = SortingPreferences()

    override suspend fun readFrom(source: BufferedSource): SortingPreferences {
        return try {
            Json.decodeFromString(
                SortingPreferences.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: SortingPreferences, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(SortingPreferences.serializer(), t))
        }
    }
}