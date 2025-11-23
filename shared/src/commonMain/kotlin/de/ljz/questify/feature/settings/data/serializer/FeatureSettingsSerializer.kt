package de.ljz.questify.feature.settings.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.feature.settings.data.models.FeatureSettings
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object FeatureSettingsSerializer : OkioSerializer<FeatureSettings> {
    override val defaultValue: FeatureSettings = FeatureSettings()

    override suspend fun readFrom(source: BufferedSource): FeatureSettings {
        return try {
            Json.decodeFromString(
                FeatureSettings.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: FeatureSettings, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(FeatureSettings.serializer(), t))
        }
    }
}