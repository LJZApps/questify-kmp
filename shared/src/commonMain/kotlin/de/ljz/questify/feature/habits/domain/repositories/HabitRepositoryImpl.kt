package de.ljz.questify.feature.habits.domain.repositories

import de.ljz.questify.feature.habits.data.daos.HabitDao
import de.ljz.questify.feature.habits.data.models.HabitEntity
import kotlinx.coroutines.flow.Flow

internal class HabitRepositoryImpl(
    private val habitDao: HabitDao
) : HabitRepository {
    override fun getAllActiveHabits(): Flow<List<HabitEntity>> {
        return habitDao.getAllActiveHabits()
    }

    override fun getAllArchivedHabits(): Flow<List<HabitEntity>> {
        return habitDao.getAllArchivedHabits()
    }

    override fun getHabitById(id: Int): Flow<HabitEntity?> {
        return habitDao.getHabitById(id = id)
    }

    override suspend fun getHabitByIdOnce(id: Int): HabitEntity? {
        return habitDao.getHabitByIdOnce(id = id)
    }

    override suspend fun upsertHabit(habitEntity: HabitEntity) {
        habitDao.upsertHabit(habitEntity = habitEntity)
    }

    override suspend fun deleteHabit(habitEntity: HabitEntity) {
        habitDao.deleteHabit(habitEntity = habitEntity)
    }

    override suspend fun updateArchivedStatus(id: Int, isArchived: Boolean) {
        habitDao.updateArchivedStatus(
            id = id,
            isArchived = isArchived
        )
    }
}