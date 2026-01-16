package de.ljz.questify.feature.quests.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity

data class QuestWithDetails(
    @Embedded val quest: QuestEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "quest_id"
    )
    val allSubTasks: List<SubQuestEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "quest_id"
    )
    val notifications: List<QuestNotificationEntity>,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val questCategory: QuestCategoryEntity?
) {
    val subTasks: List<SubQuestEntity> get() = allSubTasks.filter { it.deletedAt == null }
}
