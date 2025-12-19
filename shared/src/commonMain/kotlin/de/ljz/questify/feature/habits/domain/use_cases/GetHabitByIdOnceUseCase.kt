package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository

class GetHabitByIdOnceUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(id: Int): HabitEntity? {
        return habitRepository.getHabitByIdOnce(id = id)
    }
}