package de.ljz.questify.feature.quests.presentation.screens.edit_quest

import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState

sealed interface EditQuestUiEvent {
    object OnNavigateUp : EditQuestUiEvent

    object OnSaveQuest : EditQuestUiEvent
    object OnDeleteQuest : EditQuestUiEvent

    data class OnTitleUpdated(val value: String) : EditQuestUiEvent
    data class OnDescriptionUpdated(val value: String) : EditQuestUiEvent
    data class OnDifficultyUpdated(val value: Int) : EditQuestUiEvent

    data class OnShowDialog(val dialogState: EditQuestDialogState) : EditQuestUiEvent
    object OnCloseDialog : EditQuestUiEvent

    data class OnShowSubDialog(val subDialogState: EditQuestSubDialogState) : EditQuestUiEvent
    object OnCloseSubDialog : EditQuestUiEvent

    data class OnCreateQuestCategory(val value: String) : EditQuestUiEvent
    data class OnSelectQuestCategory(val questCategoryEntity: QuestCategoryEntity) :
        EditQuestUiEvent

    data class OnRemoveReminder(val index: Int) : EditQuestUiEvent
    data class OnCreateReminder(val timestamp: Long) : EditQuestUiEvent

    object OnCreateSubQuest : EditQuestUiEvent
    data class OnUpdateSubQuest(
        val index: Int,
        val value: String
    ) : EditQuestUiEvent
    data class OnRemoveSubQuest(val index: Int) : EditQuestUiEvent
    data class OnMoveSubQuest(val fromIndex: Int, val toIndex: Int) : EditQuestUiEvent

    data class OnSetCombinedDueDate(val timestamp: Long) : EditQuestUiEvent
    data class OnUpdateDueDate(val value: Long) : EditQuestUiEvent
    data class OnUpdateDueTime(val value: Long) : EditQuestUiEvent
    data class OnUpdateTempDueDate(val date: Long, val time: Long) : EditQuestUiEvent
    data class OnSetDueDate(val timestamp: Long) : EditQuestUiEvent
    object OnRemoveDueDate : EditQuestUiEvent

    data class OnUpdateReminderState(val value: AddingDateTimeState) : EditQuestUiEvent
}