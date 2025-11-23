package de.ljz.questify.core.domain.use_cases

import de.ljz.questify.core.domain.repositories.app.SortingPreferencesRepository

class UpdateShowCompletedQuestsUseCase(
    private val questSortingPreferencesRepository: SortingPreferencesRepository
) {
    suspend operator fun invoke(value: Boolean) {
        questSortingPreferencesRepository.saveShowCompletedQuests(value)
    }
}