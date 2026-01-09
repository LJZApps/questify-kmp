package de.ljz.questify.feature.quests.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quest_category_entity"
)
data class QuestCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "remote_id", defaultValue = "NULL")
    val remoteId: Int? = null,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null,

    @ColumnInfo(name = "sync_status", defaultValue = "SYNCED")
    val syncStatus: String = "SYNCED"
)
