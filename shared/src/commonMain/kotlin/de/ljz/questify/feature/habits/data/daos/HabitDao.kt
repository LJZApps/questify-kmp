package de.ljz.questify.feature.habits.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import de.ljz.questify.feature.habits.data.models.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit_entity WHERE is_archived = 0 ORDER BY created_at DESC")
    fun getAllActiveHabits() : Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_entity WHERE is_archived = 1 ORDER BY created_at DESC")
    fun getAllArchivedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_entity WHERE id = :id")
    fun getHabitById(id: Int): Flow<HabitEntity?>

    @Query("SELECT * FROM habit_entity WHERE id = :id")
    suspend fun getHabitByIdOnce(id: Int): HabitEntity?

    @Upsert
    suspend fun upsertHabit(habitEntity: HabitEntity)

    @Delete
    suspend fun deleteHabit(habitEntity: HabitEntity)

    @Query("UPDATE habit_entity SET is_archived = :isArchived WHERE id = :id")
    suspend fun updateArchivedStatus(id: Int, isArchived: Boolean)
}