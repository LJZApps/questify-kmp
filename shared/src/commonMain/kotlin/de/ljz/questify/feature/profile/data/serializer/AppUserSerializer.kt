package de.ljz.questify.feature.profile.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.feature.profile.data.models.AppUser
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object AppUserSerializer : OkioSerializer<AppUser> {
    override val defaultValue: AppUser = AppUser()

    override suspend fun readFrom(source: BufferedSource): AppUser {
        return try {
            Json.decodeFromString(
                AppUser.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppUser, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(AppUser.serializer(), t))
        }
    }
}