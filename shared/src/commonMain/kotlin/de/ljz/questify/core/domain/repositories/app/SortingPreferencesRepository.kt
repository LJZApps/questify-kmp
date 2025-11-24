package de.ljz.questify.core.domain.repositories.app

import de.ljz.questify.core.data.models.descriptors.QuestSortingData
import de.ljz.questify.core.data.models.descriptors.SortingDirections
import kotlinx.coroutines.flow.Flow

interface SortingPreferencesRepository {
    fun getQuestSortingPreferences(): Flow<QuestSortingData>

    suspend fun saveQuestSortingDirection(sortingDirection: SortingDirections)

    suspend fun saveShowCompletedQuests(showCompletedQuests: Boolean)


}