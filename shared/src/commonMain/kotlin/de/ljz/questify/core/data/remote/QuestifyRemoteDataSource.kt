package de.ljz.questify.core.data.remote

import de.ljz.questify.core.data.remote.models.PlayerStatsDTO
import de.ljz.questify.core.data.remote.models.QuestCategoryDTO
import de.ljz.questify.core.data.remote.models.QuestDTO
import de.ljz.questify.core.data.remote.models.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class QuestifyRemoteDataSource(private val httpClient: HttpClient) {

    // Player Stats
    suspend fun getPlayerStats(): PlayerStatsDTO = 
        httpClient.get("player-stats").body()

    suspend fun updatePlayerStats(stats: PlayerStatsDTO): PlayerStatsDTO =
        httpClient.put("player-stats") {
            setBody(stats)
            contentType(ContentType.Application.Json)
        }.body()

    // Profile
    suspend fun getProfile(): UserDTO =
        httpClient.get("profile").body()

    suspend fun updateProfile(username: String?, realName: String?, bio: String?): UserDTO =
        httpClient.put("profile") {
            setBody(mapOf("username" to username, "real_name" to realName, "bio" to bio))
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun checkUsername(username: String): Boolean =
        httpClient.get("profile/check-username") {
            parameter("username", username)
        }.body<Map<String, Boolean>>()["available"] ?: false

    // Quest Categories
    suspend fun getQuestCategories(): List<QuestCategoryDTO> =
        httpClient.get("quest-categories").body()

    suspend fun createQuestCategory(category: QuestCategoryDTO): QuestCategoryDTO =
        httpClient.post("quest-categories") {
            setBody(category)
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun updateQuestCategory(id: Int, category: QuestCategoryDTO): QuestCategoryDTO =
        httpClient.put("quest-categories/$id") {
            setBody(category)
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun deleteQuestCategory(id: Int) =
        httpClient.delete("quest-categories/$id")

    // Quests
    suspend fun getQuests(): List<QuestDTO> =
        httpClient.get("quests").body()

    suspend fun getQuest(id: Int): QuestDTO =
        httpClient.get("quests/$id").body()

    suspend fun createQuest(quest: QuestDTO): QuestDTO =
        httpClient.post("quests") {
            setBody(quest)
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun updateQuest(id: Int, quest: QuestDTO): QuestDTO =
        httpClient.put("quests/$id") {
            setBody(quest)
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun deleteQuest(id: Int) =
        httpClient.delete("quests/$id")
}
