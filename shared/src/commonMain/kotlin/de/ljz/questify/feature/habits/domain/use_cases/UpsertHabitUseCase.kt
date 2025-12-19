package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository

class UpsertHabitUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitEntity: HabitEntity) {
        habitRepository.upsertHabit(habitEntity = habitEntity)
    }
}