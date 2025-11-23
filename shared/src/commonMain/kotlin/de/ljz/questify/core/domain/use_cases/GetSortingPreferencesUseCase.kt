package de.ljz.questify.core.domain.use_cases

import de.ljz.questify.core.data.models.descriptors.QuestSortingData
import de.ljz.questify.core.domain.repositories.app.SortingPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetSortingPreferencesUseCase(
    private val sortingPreferencesRepository: SortingPreferencesRepository
) {
    suspend operator fun invoke(): Flow<QuestSortingData> {
        return sortingPreferencesRepository.getQuestSortingPreferences()
    }
}