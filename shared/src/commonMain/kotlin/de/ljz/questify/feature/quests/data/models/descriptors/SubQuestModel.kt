package de.ljz.questify.feature.quests.data.models.descriptors

import kotlin.random.Random

data class SubQuestModel(
    val text: String,
    val tempId: Long = Random.nextLong()
)
