package de.ljz.questify.feature.quests.presentation.screens.create_quest

import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel

data class CreateQuestUiState(
    val title: String,
    val description: String,
    val difficulty: Int,
    val dialogState: CreateQuestDialogState,
    val subDialogState: CreateQuestSubDialogState,
    val selectedTime: Long,
    val selectedCombinedDueDate: Long,
    val selectedDueDate: Long,
    val selectedDueTime: Long,
    val subQuestCreationEnabled: Boolean,

    val notificationTriggerTimes: List<Long>,
    val addingDateTimeState: AddingDateTimeState,
    val subQuests: List<SubQuestModel>
)

sealed class CreateQuestDialogState {
    object None : CreateQuestDialogState()
    object AddReminder : CreateQuestDialogState()
    object SelectCategorySheet : CreateQuestDialogState()
    object DatePicker : CreateQuestDialogState()
    object TimePicker : CreateQuestDialogState()
    object SelectDifficultySheet : CreateQuestDialogState()
    data class SetDueDateSheet(val selectedCombinedDueDate: Long) : CreateQuestDialogState()
}

sealed class CreateQuestSubDialogState {
    object None : CreateQuestSubDialogState()
    object DatePicker : CreateQuestSubDialogState()
    object TimePicker : CreateQuestSubDialogState()
}