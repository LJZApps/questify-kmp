package de.ljz.questify.feature.quests.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel
import kotlin.time.Instant

@Entity(
    tableName = "sub_quest_entity",
    foreignKeys = [
        ForeignKey(
            entity = QuestEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["quest_uuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("quest_id"),
        Index("quest_uuid"),
        Index(value = ["uuid"], unique = true)
    ]
)
data class SubQuestEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "uuid", defaultValue = "") val uuid: String,

    @ColumnInfo(name = "quest_uuid", defaultValue = "")
    val questUuid: String,

    @ColumnInfo(name = "sync_status", defaultValue = "DIRTY")
    val syncStatus: SyncStatus = SyncStatus.DIRTY,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    @ColumnInfo(name = "quest_id")
    val questId: Int,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null
)

fun SubQuestEntity.toModel(
    id: Int,
    text: String,
    orderIndex: Int
): SubQuestModel {
    return SubQuestModel(
        id = id,
        text = text,
        orderIndex = orderIndex
    )
}
