package de.ljz.questify.core.domain.repositories.app

import androidx.datastore.core.DataStore
import de.ljz.questify.core.data.models.SortingPreferences
import de.ljz.questify.core.data.models.descriptors.QuestSortingData
import de.ljz.questify.core.data.models.descriptors.SortingDirections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SortingPreferencesRepositoryImpl(
    private val sortingDataStore: DataStore<SortingPreferences>
) : SortingPreferencesRepository {

    override fun getQuestSortingPreferences(): Flow<QuestSortingData> {
        return sortingDataStore.data.map {
            QuestSortingData(
                questSortingDirection = it.questSortingDirection,
                showCompletedQuests = it.showCompletedQuests
            )
        }
    }

    override suspend fun saveQuestSortingDirection(sortingDirection: SortingDirections) {
        sortingDataStore.updateData {
            it.copy(questSortingDirection = sortingDirection)
        }
    }

    override suspend fun saveShowCompletedQuests(showCompletedQuests: Boolean) {
        sortingDataStore.updateData {
            it.copy(showCompletedQuests = showCompletedQuests)
        }
    }
}