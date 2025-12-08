package de.ljz.questify.feature.quests.data.models.descriptors

import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlin.random.Random

data class SubQuestModel(
    val text: String,
    val tempId: Long = Random.nextLong()
)

fun SubQuestModel.toEntity(
    text: String,
    questId: Long
): SubQuestEntity {
    return SubQuestEntity(
        text = text,
        questId = questId
    )
}