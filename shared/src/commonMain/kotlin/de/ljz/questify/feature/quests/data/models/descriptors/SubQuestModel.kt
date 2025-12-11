package de.ljz.questify.feature.quests.data.models.descriptors

import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlin.random.Random

data class SubQuestModel(
    val id: Int = 0,
    val text: String,
    val tempId: Long = Random.nextLong(),
    val orderIndex: Int = 0,
)

fun SubQuestModel.toEntity(
    id: Int = 0,
    text: String,
    questId: Long,
    orderIndex: Int
): SubQuestEntity {
    return SubQuestEntity(
        id = id,
        text = text,
        questId = questId,
        orderIndex = orderIndex
    )
}