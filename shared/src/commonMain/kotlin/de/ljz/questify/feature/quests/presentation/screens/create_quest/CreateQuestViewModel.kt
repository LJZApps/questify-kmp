package de.ljz.questify.feature.quests.presentation.screens.create_quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestNotificationUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddSubQuestsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestCategoriesUseCase
import de.ljz.questify.feature.quests.domain.use_cases.UpsertQuestUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

class CreateQuestViewModel(
    private val selectedCategoryIndex: Int?,

    private val upsertQuestUseCase: UpsertQuestUseCase,

    private val addQuestNotificationUseCase: AddQuestNotificationUseCase,

    private val addQuestCategoryUseCase: AddQuestCategoryUseCase,
    private val getAllQuestCategoriesUseCase: GetAllQuestCategoriesUseCase,

    private val addSubQuestsUseCase: AddSubQuestsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = CreateQuestUiState(
            title = "",
            description = "",
            difficulty = 0,
            dialogState = CreateQuestDialogState.None,
            subDialogState = CreateQuestSubDialogState.None,
            selectedTime = 0,
            selectedDueDate = 0L,
            subQuestCreationEnabled = false,

            notificationTriggerTimes = emptyList(),
            addingDateTimeState = AddingDateTimeState.DATE,
            subQuests = emptyList()
        )
    )
    val uiState: StateFlow<CreateQuestUiState> = _uiState.asStateFlow()

    private val _uiEffects = Channel<CreateQuestUiEffect>()
    val uiEffects = _uiEffects.receiveAsFlow()

    private val _categories = MutableStateFlow<List<QuestCategoryEntity>>(emptyList())
    val categories: StateFlow<List<QuestCategoryEntity>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<QuestCategoryEntity?>(null)
    val selectedCategory: StateFlow<QuestCategoryEntity?> = _selectedCategory.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getAllQuestCategoriesUseCase.invoke()
                    .collectLatest { questCategoryEntities ->
                        _categories.value = questCategoryEntities

                        selectedCategoryIndex?.let { index ->
                            if (selectedCategory.value == null) {
                                _selectedCategory.value = _categories.value[index]
                            }
                        }
                    }
            }
        }
    }

    fun onUiEvent(event: CreateQuestUiEvent) {
        when (event) {
            is CreateQuestUiEvent.OnCreateQuest -> {
                val quest = QuestEntity(
                    title = _uiState.value.title,
                    notes = _uiState.value.description.ifEmpty { null },
                    difficulty = Difficulty.fromIndex(_uiState.value.difficulty),
                    createdAt = Clock.System.now(),
                    dueDate = if (_uiState.value.selectedDueDate.toInt() == 0) null else Instant.fromEpochMilliseconds(
                        _uiState.value.selectedDueDate
                    ),
                    categoryId = _selectedCategory.value?.id
                )

                viewModelScope.launch {
                    val questId = upsertQuestUseCase.invoke(quest)

                    _uiState.value.notificationTriggerTimes.forEach { notificationTriggerTime ->
                        val questNotification = QuestNotificationEntity(
                            questId = questId.toInt(),
                            notifyAt = Instant.fromEpochMilliseconds(notificationTriggerTime)
                        )

                        addQuestNotificationUseCase.invoke(questNotification)
                    }

                    val subQuestEntities = _uiState.value.subQuests.mapIndexed { index, subTask ->
                        SubQuestEntity(
                            text = subTask.text,
                            questId = questId,
                            orderIndex = index
                        )
                    }

                    addSubQuestsUseCase.invoke(subQuestEntities = subQuestEntities)
                    _uiEffects.send(CreateQuestUiEffect.OnNavigateUp)
                }
            }

            is CreateQuestUiEvent.OnTitleUpdated -> {
                _uiState.update {
                    it.copy(title = event.value)
                }
            }

            is CreateQuestUiEvent.OnDescriptionUpdated -> {
                _uiState.update {
                    it.copy(description = event.value)
                }
            }

            is CreateQuestUiEvent.OnDifficultyUpdated -> {
                _uiState.update {
                    it.copy(difficulty = event.value)
                }
            }

            is CreateQuestUiEvent.OnShowDialog -> {
                _uiState.update {
                    it.copy(dialogState = event.dialogState)
                }
            }

            is CreateQuestUiEvent.OnShowSubDialog -> {
                _uiState.update {
                    it.copy(subDialogState = event.subDialogState)
                }
            }

            is CreateQuestUiEvent.OnCloseSubDialog -> {
                _uiState.update {
                    it.copy(subDialogState = CreateQuestSubDialogState.None)
                }
            }

            is CreateQuestUiEvent.OnCloseDialog -> {
                _uiState.update {
                    it.copy(dialogState = CreateQuestDialogState.None)
                }
            }

            is CreateQuestUiEvent.OnCreateQuestCategory -> {
                viewModelScope.launch {
                    addQuestCategoryUseCase.invoke(
                        questCategoryEntity = QuestCategoryEntity(
                            text = event.value
                        )
                    )
                }
            }

            is CreateQuestUiEvent.OnSelectQuestCategory -> {
                _selectedCategory.value = event.questCategoryEntity
            }

            is CreateQuestUiEvent.OnRemoveReminder -> {
                val updatedTimes = _uiState.value.notificationTriggerTimes.toMutableList().apply {
                    removeAt(event.index)
                }
                _uiState.value = _uiState.value.copy(notificationTriggerTimes = updatedTimes)
            }

            is CreateQuestUiEvent.OnCreateReminder -> {
                val updatedTimes = _uiState.value.notificationTriggerTimes.toMutableList().apply {
                    add(event.timestamp)
                }

                _uiState.update {
                    it.copy(
                        notificationTriggerTimes = updatedTimes,
                        addingDateTimeState = AddingDateTimeState.DATE
                    )
                }
            }

            is CreateQuestUiEvent.OnCreateSubQuest -> {
                _uiState.update {
                    it.copy(
                        subQuests = _uiState.value.subQuests + SubQuestModel(text = "")
                    )
                }
            }

            is CreateQuestUiEvent.OnUpdateSubQuest -> {
                _uiState.update { state ->
                    state.copy(
                        subQuests = state.subQuests.mapIndexed { i, subTask ->
                            if (i == event.index) subTask.copy(text = event.value) else subTask
                        }
                    )
                }
            }

            is CreateQuestUiEvent.OnRemoveSubQuest -> {
                _uiState.update { state ->
                    state.copy(
                        subQuests = state.subQuests.filterIndexed { i, _ -> i != event.index }
                    )
                }
            }

            is CreateQuestUiEvent.OnEnableSubQuestCreation -> {
                _uiState.update {
                    it.copy(subQuestCreationEnabled = true)
                }
            }

            is CreateQuestUiEvent.OnDisableSubQuestCreation -> {
                _uiState.update {
                    it.copy(
                        subQuestCreationEnabled = false,
                        subQuests = emptyList()
                    )
                }
            }

            is CreateQuestUiEvent.OnMoveSubQuest -> {
                _uiState.update { state ->
                    val mutableList = state.subQuests.toMutableList()
                    val item = mutableList.removeAt(event.fromIndex - 3)
                    mutableList.add(event.toIndex - 3, item)

                    state.copy(subQuests = mutableList)
                }
            }

            is CreateQuestUiEvent.OnSetDueDate -> {
                _uiState.update {
                    it.copy(
                        selectedDueDate = event.timestamp,
                        subDialogState = CreateQuestSubDialogState.None
                    )
                }
            }

            is CreateQuestUiEvent.OnRemoveDueDate -> {
                _uiState.update {
                    it.copy(
                        selectedDueDate = 0L,
                        subDialogState = CreateQuestSubDialogState.None
                    )
                }
            }

            is CreateQuestUiEvent.OnUpdateReminderState -> {
                _uiState.update {
                    it.copy(
                        addingDateTimeState = event.value
                    )
                }
            }

            else -> Unit
        }
    }
}