package de.ljz.questify.core.data.remote

import de.ljz.questify.core.data.remote.models.PlayerStatsDTO
import de.ljz.questify.core.data.remote.models.QuestCategoryDTO
import de.ljz.questify.core.data.remote.models.QuestDTO
import de.ljz.questify.core.data.remote.models.UpdateProfileRequest
import de.ljz.questify.core.data.remote.models.UserDTO
import de.ljz.questify.core.data.remote.util.NetworkResult
import de.ljz.questify.core.data.remote.util.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class QuestifyRemoteDataSource(private val httpClient: HttpClient) {

    // Player Stats
    suspend fun getPlayerStats(): NetworkResult<PlayerStatsDTO> =
        safeApiCall { httpClient.get("player-stats") }

    suspend fun updatePlayerStats(stats: PlayerStatsDTO): NetworkResult<PlayerStatsDTO> =
        safeApiCall {
            httpClient.put("player-stats") {
                setBody(stats)
            }
        }

    // Profile
    suspend fun getProfile(): NetworkResult<UserDTO> =
        safeApiCall { httpClient.get("profile") }

    suspend fun updateProfile(username: String?, realName: String?, bio: String?): NetworkResult<UserDTO> =
        safeApiCall {
            httpClient.put("profile") {
                setBody(UpdateProfileRequest(username, realName, bio))
            }
        }

    suspend fun checkUsername(username: String): NetworkResult<Map<String, Boolean>> =
        safeApiCall {
            httpClient.get("profile/check-username") {
                parameter("username", username)
            }
        }

    // Quest Categories
    suspend fun getQuestCategories(): NetworkResult<List<QuestCategoryDTO>> =
        safeApiCall { httpClient.get("quest-categories") }

    suspend fun createQuestCategory(category: QuestCategoryDTO): NetworkResult<QuestCategoryDTO> =
        safeApiCall {
            httpClient.post("quest-categories") {
                setBody(category)
            }
        }

    suspend fun updateQuestCategory(id: Int, category: QuestCategoryDTO): NetworkResult<QuestCategoryDTO> =
        safeApiCall {
            httpClient.put("quest-categories/$id") {
                setBody(category)
            }
        }

    suspend fun deleteQuestCategory(id: Int): NetworkResult<Unit> =
        safeApiCall { httpClient.delete("quest-categories/$id") }

    // Quests
    suspend fun getQuests(): NetworkResult<List<QuestDTO>> =
        safeApiCall { httpClient.get("quests") }

    suspend fun getQuest(id: Int): NetworkResult<QuestDTO> =
        safeApiCall { httpClient.get("quests/$id") }

    suspend fun createQuest(quest: QuestDTO): NetworkResult<QuestDTO> =
        safeApiCall {
            httpClient.post("quests") {
                setBody(quest)
            }
        }

    suspend fun updateQuest(id: Int, quest: QuestDTO): NetworkResult<QuestDTO> =
        safeApiCall {
            httpClient.put("quests/$id") {
                setBody(quest)
            }
        }

    suspend fun deleteQuest(id: Int): NetworkResult<Unit> =
        safeApiCall { httpClient.delete("quests/$id") }
}
