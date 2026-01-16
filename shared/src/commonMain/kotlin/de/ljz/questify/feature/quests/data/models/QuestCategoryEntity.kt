package de.ljz.questify.feature.quests.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "quest_category_entity",
    indices = [
        Index(value = ["uuid"], unique = true)
    ]
)
data class QuestCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "uuid", defaultValue = "") val uuid: String,

    @ColumnInfo(name = "sync_status", defaultValue = "DIRTY")
    val syncStatus: SyncStatus = SyncStatus.DIRTY,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null
)
