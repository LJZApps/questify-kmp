package de.ljz.questify.core.data.models

import de.ljz.questify.core.data.models.descriptors.SortingDirections
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SortingPreferences(
    @SerialName("quest_sorting_direction")
    val questSortingDirection: SortingDirections = SortingDirections.DESCENDING,

    @SerialName("show_completed_quests")
    val showCompletedQuests: Boolean = false
)