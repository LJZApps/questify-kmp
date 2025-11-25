package de.ljz.questify.feature.settings.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.feature.settings.data.models.AppSettings
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object AppSettingsSerializer : OkioSerializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings()

    override suspend fun readFrom(source: BufferedSource): AppSettings {
        return try {
            Json.Default.decodeFromString(
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
            it.writeUtf8(Json.Default.encodeToString(AppSettings.serializer(), t))
        }
    }
}