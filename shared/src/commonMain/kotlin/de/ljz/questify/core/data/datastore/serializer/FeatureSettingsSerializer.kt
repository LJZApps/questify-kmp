package de.ljz.questify.core.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.feature.settings.data.models.AppSettings
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object FeatureSettingsSerializer : OkioSerializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings()

    override suspend fun readFrom(source: BufferedSource): AppSettings {
        return try {
            Json.decodeFromString(
                AppSettings.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(AppSettings.serializer(), t))
        }
    }
}