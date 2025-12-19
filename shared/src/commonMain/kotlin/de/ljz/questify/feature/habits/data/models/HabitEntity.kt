package de.ljz.questify.feature.habits.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.ljz.questify.feature.habits.data.models.descriptors.HabitFrequency
import de.ljz.questify.feature.habits.data.models.descriptors.HabitType
import kotlin.time.Instant

@Entity(tableName = "habit_entity")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "notes")
    val notes: String,

    @ColumnInfo(name = "habit_type")
    val habitType: HabitType,

    @ColumnInfo(name = "frequency")
    val frequency: HabitFrequency,

    @ColumnInfo(name = "target_count")
    val targetCount: Int,

    @ColumnInfo(name = "reminder_count")
    val reminderCount: Int = 1,

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "best_streak")
    val bestStreak: Int = 0,

    @ColumnInfo(name = "icon_res")
    val iconRes: Int? = null,

    @ColumnInfo(name = "color_hex")
    val colorHex: String? = null,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "last_completed_timestamp")
    val lastCompletedTimestamp: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
)