package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository

class DeleteHabitUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitEntity: HabitEntity) {
        habitRepository.deleteHabit(habitEntity = habitEntity)
    }
}