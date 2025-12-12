package de.ljz.questify.feature.quests.presentation.screens.quest_overview

import de.ljz.questify.core.data.models.descriptors.SortingDirections
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails

data class QuestOverviewUIState(
    val dialogState: QuestOverviewDialogState,
    val allQuestPageState: AllQuestPageState,
)

data class AllQuestPageState(
    val quests: List<QuestWithDetails>,
    val sortingDirections: SortingDirections,
    val showCompleted: Boolean,
)

data class QuestDoneDialogState(
    val questName: String,
    val xp: Int,
    val points: Int,
    val newLevel: Int,
)

sealed class QuestOverviewDialogState {
    object None : QuestOverviewDialogState()
    object SortingBottomSheet : QuestOverviewDialogState()
    object CreateCategory : QuestOverviewDialogState()

    data class QuestDone(val questDoneDialogState: QuestDoneDialogState) : QuestOverviewDialogState()
    data class UpdateCategory(val questCategoryEntity: QuestCategoryEntity) : QuestOverviewDialogState()
    data class DeleteCategory(val questCategoryEntity: QuestCategoryEntity) : QuestOverviewDialogState()
    data class DeleteQuestConfirmation(val id: Int) : QuestOverviewDialogState()
}