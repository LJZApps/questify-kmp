package de.ljz.questify.core.data.models.descriptors

data class QuestSortingData(
    val questSortingDirection: SortingDirections,
    val showCompletedQuests: Boolean = false
)
