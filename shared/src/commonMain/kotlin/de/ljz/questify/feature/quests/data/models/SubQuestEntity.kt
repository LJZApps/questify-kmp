package de.ljz.questify.feature.quests.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel

@Entity(
    tableName = "sub_quest_entity",
    foreignKeys = [
        ForeignKey(
            entity = QuestEntity::class,
            parentColumns = ["id"],
            childColumns = ["quest_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("quest_id")
    ]
)
data class SubQuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    @ColumnInfo(name = "quest_id")
    val questId: Long,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0
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