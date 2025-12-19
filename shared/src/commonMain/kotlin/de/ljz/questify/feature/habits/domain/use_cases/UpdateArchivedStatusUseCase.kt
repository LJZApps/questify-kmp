package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.domain.repositories.HabitRepository

class UpdateArchivedStatusUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(id: Int, isArchived: Boolean) {
        habitRepository.updateArchivedStatus(
            id = id,
            isArchived = isArchived
        )
    }
}