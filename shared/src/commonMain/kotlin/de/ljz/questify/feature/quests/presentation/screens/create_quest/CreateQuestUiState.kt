package de.ljz.questify.feature.quests.presentation.screens.create_quest

import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel

data class CreateQuestUiState(
    val title: String,
    val description: String,
    val difficulty: Int,
    val dialogState: DialogState,
    val selectedTime: Long,
    val selectedDueDate: Long,
    val subQuestCreationEnabled: Boolean,

    val notificationTriggerTimes: List<Long>,
    val addingDateTimeState: AddingDateTimeState,
    val subQuests: List<SubQuestModel>
)

sealed class DialogState {
    object None : DialogState()
    object AddReminder : DialogState()
    object SelectCategorySheet : DialogState()
    object DatePicker : DialogState()
    object TimePicker : DialogState()
}