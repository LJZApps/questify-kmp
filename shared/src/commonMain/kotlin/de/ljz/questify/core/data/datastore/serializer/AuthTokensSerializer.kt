package de.ljz.questify.core.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import de.ljz.questify.core.data.models.AuthTokens
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object AuthTokensSerializer : OkioSerializer<AuthTokens> {
    override val defaultValue: AuthTokens = AuthTokens()

    override suspend fun readFrom(source: BufferedSource): AuthTokens {
        return try {
            Json.decodeFromString(
                AuthTokens.serializer(),
                source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AuthTokens, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(AuthTokens.serializer(), t))
        }
    }
}
