package de.ljz.questify.core.domain.use_cases

import de.ljz.questify.core.data.models.descriptors.SortingDirections
import de.ljz.questify.core.domain.repositories.app.SortingPreferencesRepository

class SaveQuestSortingDirectionUseCase(
    private val questSortingPreferencesRepository: SortingPreferencesRepository
) {
    suspend operator fun invoke(sortingDirections: SortingDirections) {
        questSortingPreferencesRepository.saveQuestSortingDirection(sortingDirection = sortingDirections)
    }
}