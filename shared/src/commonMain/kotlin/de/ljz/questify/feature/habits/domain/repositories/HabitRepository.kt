package de.ljz.questify.feature.habits.domain.repositories

import de.ljz.questify.feature.habits.data.models.HabitEntity
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    fun getAllArchivedHabits(): Flow<List<HabitEntity>>

    fun getHabitById(id: Int): Flow<HabitEntity?>

    suspend fun getHabitByIdOnce(id: Int): HabitEntity?

    suspend fun upsertHabit(habitEntity: HabitEntity)

    suspend fun deleteHabit(habitEntity: HabitEntity)

    suspend fun updateArchivedStatus(id: Int, isArchived: Boolean)
}