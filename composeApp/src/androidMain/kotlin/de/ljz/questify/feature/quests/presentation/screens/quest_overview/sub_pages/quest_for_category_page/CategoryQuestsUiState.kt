package de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page

import de.ljz.questify.core.data.models.descriptors.SortingDirections
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails

data class CategoryQuestsUiState(
    val quests: List<QuestWithDetails>,
    val sortingDirections: SortingDirections,
    val showCompleted: Boolean
)
