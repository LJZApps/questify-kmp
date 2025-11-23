package de.ljz.questify.core.data.database.adapters

import androidx.room.TypeConverter
import kotlin.time.Instant

class InstantAdapter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilliseconds()
    }
}