package de.ljz.questify.feature.quests.presentation.screens.edit_quest

import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel

data class EditQuestUiState(
    val title: String,
    val notes: String,
    val difficulty: Int,
    val selectedDueDate: Long,
    val selectedDueTime: Long,
    val combinedDueDate: Long,
    val categoryId: Int?,

    val notificationTriggerTimes: List<Long>,
    val subQuests: List<SubQuestModel>,

    val addingDateTimeState: AddingDateTimeState,
    val dialogState: EditQuestDialogState,
    val subDialogState: EditQuestSubDialogState
)

sealed class EditQuestDialogState {
    object None : EditQuestDialogState()
    object DeletionConfirmation : EditQuestDialogState()
    object AddReminder : EditQuestDialogState()
    object SelectCategorySheet : EditQuestDialogState()
    object SelectDifficultySheet : EditQuestDialogState()
    data class SetDueDateSheet(val selectedCombinedDueDate: Long) : EditQuestDialogState()
}

sealed class EditQuestSubDialogState {
    object None : EditQuestSubDialogState()
    object DatePicker : EditQuestSubDialogState()
    object TimePicker : EditQuestSubDialogState()
}