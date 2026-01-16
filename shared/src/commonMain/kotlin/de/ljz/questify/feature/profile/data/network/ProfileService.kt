package de.ljz.questify.feature.profile.data.network

import de.ljz.questify.feature.profile.data.network.dto.ProfileResponseDto
import de.ljz.questify.feature.profile.data.network.dto.ProfileUpdateRequestDto
import de.ljz.questify.feature.profile.data.network.dto.UserProfileDto
import de.ljz.questify.feature.profile.data.network.dto.UsernameAvailabilityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ProfileService(private val httpClient: HttpClient) {
    suspend fun getProfile(): UserProfileDto {
        return httpClient.get("/api/v1/profile") {
            contentType(ContentType.Application.Json)
        }.body<ProfileResponseDto>().data
    }

    suspend fun updateProfile(request: ProfileUpdateRequestDto): UserProfileDto {
        return httpClient.put("/api/v1/profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<ProfileResponseDto>().data
    }

    suspend fun checkUsername(username: String): UsernameAvailabilityDto {
        return httpClient.get("/api/v1/profile/check-username") {
            contentType(ContentType.Application.Json)
            parameter("username", username)
        }.body()
    }
}
