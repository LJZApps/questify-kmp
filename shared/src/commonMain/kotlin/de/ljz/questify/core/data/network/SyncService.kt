package de.ljz.questify.core.data.network

import de.ljz.questify.core.data.network.dto.SyncRequestDto
import de.ljz.questify.core.data.network.dto.SyncResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

import io.ktor.http.ContentType
import io.ktor.http.contentType

class SyncService(private val httpClient: HttpClient) {
    suspend fun sync(request: SyncRequestDto): SyncResponseDto {
        return httpClient.post("/api/v1/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
