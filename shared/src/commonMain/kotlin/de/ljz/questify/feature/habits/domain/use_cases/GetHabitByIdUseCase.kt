package de.ljz.questify.feature.habits.domain.use_cases

import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.habits.domain.repositories.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetHabitByIdUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(id: Int): Flow<HabitEntity?> {
        return habitRepository.getHabitById(id = id)
    }
}