package de.ljz.questify.feature.quests.presentation.screens.create_quest

import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState

sealed interface CreateQuestUiEvent {
    object OnNavigateUp : CreateQuestUiEvent

    object OnCreateQuest : CreateQuestUiEvent

    data class OnTitleUpdated(val value: String) : CreateQuestUiEvent
    data class OnDescriptionUpdated(val value: String) : CreateQuestUiEvent
    data class OnDifficultyUpdated(val value: Int) : CreateQuestUiEvent

    data class OnShowDialog(val dialogState: CreateQuestDialogState) : CreateQuestUiEvent
    object OnCloseDialog : CreateQuestUiEvent

    data class OnShowSubDialog(val subDialogState: CreateQuestSubDialogState) : CreateQuestUiEvent
    object OnCloseSubDialog : CreateQuestUiEvent

    data class OnCreateQuestCategory(val value: String) : CreateQuestUiEvent
    data class OnSelectQuestCategory(val questCategoryEntity: QuestCategoryEntity) : CreateQuestUiEvent

    data class OnRemoveReminder(val index: Int) : CreateQuestUiEvent
    data class OnCreateReminder(val timestamp: Long) : CreateQuestUiEvent

    object OnCreateSubQuest : CreateQuestUiEvent
    data class OnUpdateSubQuest(
        val index: Int,
        val value: String
    ) : CreateQuestUiEvent
    data class OnRemoveSubQuest(val index: Int) : CreateQuestUiEvent
    object OnEnableSubQuestCreation : CreateQuestUiEvent
    object OnDisableSubQuestCreation : CreateQuestUiEvent
    data class OnMoveSubQuest(val fromIndex: Int, val toIndex: Int) : CreateQuestUiEvent

    data class OnSetDueDate(val timestamp: Long) : CreateQuestUiEvent
    object OnRemoveDueDate : CreateQuestUiEvent

    data class OnUpdateReminderState(val value: AddingDateTimeState) : CreateQuestUiEvent

}