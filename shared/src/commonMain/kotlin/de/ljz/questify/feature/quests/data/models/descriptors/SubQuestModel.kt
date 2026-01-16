package de.ljz.questify.feature.quests.data.models.descriptors

import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlin.random.Random

data class SubQuestModel(
    val uuid: String = "",
    val questUuid: String = "",
    val id: Int = 0,
    val text: String,
    val tempId: Long = Random.nextLong(),
    val orderIndex: Int = 0,
)

fun SubQuestModel.toEntity(
    uuid: String,
    questUuid: String,
    id: Int = 0,
    text: String,
    questId: Int,
    orderIndex: Int
): SubQuestEntity {
    return SubQuestEntity(
        uuid = uuid,
        questUuid = questUuid,
        id = id,
        text = text,
        questId = questId,
        orderIndex = orderIndex
    )
}
