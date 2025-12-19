package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetAllArchivedHabitsUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<List<HabitEntity>> {
        return habitRepository.getAllArchivedHabits()
    }
}