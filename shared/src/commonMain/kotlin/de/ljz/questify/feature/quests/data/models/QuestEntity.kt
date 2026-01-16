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
            parentColumns = ["uuid"],
            childColumns = ["category_uuid"],
            onDelete = ForeignKey.Companion.SET_NULL
        )
    ],
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["category_id"]),
        Index(value = ["category_uuid"])
    ]
)
data class QuestEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "uuid", defaultValue = "") val uuid: String,

    @ColumnInfo(name = "category_uuid")
    val categoryUuid: String? = null,

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
