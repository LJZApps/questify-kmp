package de.ljz.questify.feature.quests.data.models.descriptors

import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlin.random.Random

data class SubQuestModel(
    val uuid: String = "",
    val questUuid: String = "",
    val id: Int = 0,
    val text: String,
    val isDone: Boolean = false,
    val tempId: Long = Random.nextLong(),
    val orderIndex: Int = 0,
)

fun SubQuestModel.toEntity(
    uuid: String,
    questUuid: String,
    id: Int = 0,
    text: String,
    isDone: Boolean = false,
    questId: Int,
    orderIndex: Int
): SubQuestEntity {
    return SubQuestEntity(
        uuid = uuid,
        questUuid = questUuid,
        id = id,
        text = text,
        isDone = isDone,
        questId = questId,
        orderIndex = orderIndex
    )
}
