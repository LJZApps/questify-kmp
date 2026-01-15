package de.ljz.questify.feature.quests.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import kotlin.time.Instant

@Entity(
    tableName = "quest_entity",
    foreignKeys = [
        ForeignKey(
            entity = QuestCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.Companion.SET_NULL
        )
    ],
    indices = [
        Index(value = ["category_id"])
    ]
)
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "uuid", defaultValue = "")
    val uuid: String,

    @ColumnInfo(name = "sync_status", defaultValue = "DIRTY")
    val syncStatus: SyncStatus = SyncStatus.DIRTY,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "difficulty")
    val difficulty: Difficulty,

    @ColumnInfo(name = "due_date")
    val dueDate: Instant? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null,

    @ColumnInfo(name = "lock_deletion")
    val lockDeletion: Boolean = false,

    @ColumnInfo(name = "done")
    val done: Boolean = false,

    @ColumnInfo(name = "category_id")
    val categoryId: Int? = null
)