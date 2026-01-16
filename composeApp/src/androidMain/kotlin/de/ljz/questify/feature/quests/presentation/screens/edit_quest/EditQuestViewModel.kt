package de.ljz.questify.feature.quests.presentation.screens.edit_quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.domain.use_cases.SyncUseCase
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import de.ljz.questify.feature.quests.data.models.descriptors.AddingDateTimeState
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.models.descriptors.SubQuestModel
import de.ljz.questify.feature.quests.data.models.descriptors.toEntity
import de.ljz.questify.feature.quests.data.models.toModel
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestNotificationUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddSubQuestsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.CancelQuestNotificationsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteSubQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteSubQuestsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestCategoriesUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetQuestByIdAsFlowUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetQuestByIdUseCase
import de.ljz.questify.feature.quests.domain.use_cases.UpsertQuestUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Instant

class EditQuestViewModel(
    private val id: Int,

    private val upsertQuestUseCase: UpsertQuestUseCase,
    private val getQuestByIdUseCase: GetQuestByIdUseCase,
    private val getQuestByIdAsFlowUseCase: GetQuestByIdAsFlowUseCase,
    private val deleteQuestUseCase: DeleteQuestUseCase,

    private val addQuestCategoryUseCase: AddQuestCategoryUseCase,
    private val getAllQuestCategoriesUseCase: GetAllQuestCategoriesUseCase,

    private val addSubQuestsUseCase: AddSubQuestsUseCase,
    private val deleteSubQuestUseCase: DeleteSubQuestUseCase,
    private val deleteSubQuestsUseCase: DeleteSubQuestsUseCase,

    private val addQuestNotificationUseCase: AddQuestNotificationUseCase,
    private val cancelQuestNotificationsUseCase: CancelQuestNotificationsUseCase,
    private val syncUseCase: SyncUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = EditQuestUiState(
            title = "",
            notes = "",
            difficulty = 0,
            combinedDueDate = 0,
            categoryId = null,

            notificationTriggerTimes = emptyList(),
            subQuests = emptyList(),

            addingDateTimeState = AddingDateTimeState.DATE,
            dialogState = EditQuestDialogState.None,
            subDialogState = EditQuestSubDialogState.None,
            selectedDueDate = 0L,
            selectedDueTime = 0L
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = Channel<EditQuestUiEffect>()
    val uiEffects = _uiEffects.receiveAsFlow()

    private var _copiedQuestEntity: QuestEntity? = null

    private val _categories = MutableStateFlow<List<QuestCategoryEntity>>(emptyList())
    val categories: StateFlow<List<QuestCategoryEntity>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<QuestCategoryEntity?>(null)
    val selectedCategory: StateFlow<QuestCategoryEntity?> = _selectedCategory.asStateFlow()

    private val _subQuestsToDelete = mutableListOf<Int>()
    private var isNavigatingUp = false

    init {
        viewModelScope.launch {
            val questFlow = flow { emit(getQuestByIdUseCase(id)) }
            val categoriesFlow = getAllQuestCategoriesUseCase()

            combine(questFlow, categoriesFlow) { questWithDetails, categories ->
                Triple(questWithDetails, categories, questWithDetails.quest.categoryId)
            }
                .take(1)
                .collect { (questWithDetails, categories, categoryId) ->

                    _categories.value = categories
                    _copiedQuestEntity = questWithDetails.quest

                    val matchingCategory = categories.find { it.id == categoryId }
                    _selectedCategory.value = matchingCategory

                    _uiState.update { state ->
                        state.copy(
                            title = questWithDetails.quest.title,
                            notes = questWithDetails.quest.notes ?: "",
                            difficulty = questWithDetails.quest.difficulty.ordinal,
                            combinedDueDate = questWithDetails.quest.dueDate?.toEpochMilliseconds() ?: 0L,
                            categoryId = categoryId,
                            subQuests = questWithDetails.subTasks.sortedBy { it.orderIndex }.map { it.toModel() }
                        )
                    }
                }

            launch {
                getAllQuestCategoriesUseCase().collectLatest {
                    _categories.value = it
                }
            }
        }
    }

    fun onUiEvent(event: EditQuestUiEvent) {
        when (event) {
            is EditQuestUiEvent.OnSaveQuest -> {
                if (isNavigatingUp) return
                viewModelScope.launch {
                    launch {
                        _copiedQuestEntity?.let { copiedQuestEntity ->
                            val updatedQuestEntity = copiedQuestEntity.copy(
                                title = _uiState.value.title,
                                notes = _uiState.value.notes.trim().ifEmpty { null },
                                difficulty = Difficulty.fromIndex(_uiState.value.difficulty),
                                dueDate = if (_uiState.value.combinedDueDate.toInt() == 0) null else Instant.fromEpochMilliseconds(
                                    _uiState.value.combinedDueDate
                                ),
                                categoryId = _selectedCategory.value?.id,
                                categoryUuid = _selectedCategory.value?.uuid
                            )

                            upsertQuestUseCase.invoke(updatedQuestEntity)

                            addSubQuestsUseCase.invoke(
                                subQuestEntities =
                                    _uiState.value.subQuests.mapIndexed { index, model ->
                                        model.toEntity(
                                            uuid = model.uuid.ifEmpty { UUID.randomUUID().toString() },
                                            questUuid = copiedQuestEntity.uuid,
                                            id = model.id,
                                            text = model.text,
                                            questId = copiedQuestEntity.id,
                                            isDone = model.isDone,
                                            orderIndex = index
                                        )
                                    }
                            )

                            _subQuestsToDelete.forEach { id ->
                                deleteSubQuestUseCase(id)
                            }

                            _uiState.value.notificationTriggerTimes.forEach { notificationTriggerTime ->
                                val questNotification = QuestNotificationEntity(
                                    questId = id,
                                    questUuid = copiedQuestEntity.uuid,
                                    notifyAt = Instant.fromEpochMilliseconds(notificationTriggerTime)
                                )

                                addQuestNotificationUseCase.invoke(questNotification)
                            }

                            syncUseCase()

                            isNavigatingUp = true
                            _uiEffects.send(EditQuestUiEffect.OnNavigateUp)
                        }
                    }
                }
            }

            is EditQuestUiEvent.OnDeleteQuest -> {
                if (isNavigatingUp) return
                viewModelScope.launch {
                    cancelQuestNotificationsUseCase.invoke(id = id)

                    deleteQuestUseCase.invoke(questId = id)

                    syncUseCase()

                    isNavigatingUp = true
                    _uiEffects.send(EditQuestUiEffect.OnNavigateUp)
                }
            }

            is EditQuestUiEvent.OnTitleUpdated -> {
                _uiState.update {
                    it.copy(title = event.value)
                }
            }

            is EditQuestUiEvent.OnDescriptionUpdated -> {
                _uiState.update {
                    it.copy(notes = event.value)
                }
            }

            is EditQuestUiEvent.OnDifficultyUpdated -> {
                _uiState.update {
                    it.copy(difficulty = event.value)
                }
            }

            is EditQuestUiEvent.OnShowDialog -> {
                _uiState.update {
                    it.copy(dialogState = event.dialogState)
                }
            }

            is EditQuestUiEvent.OnCloseDialog -> {
                _uiState.update {
                    it.copy(dialogState = EditQuestDialogState.None)
                }
            }

            is EditQuestUiEvent.OnShowSubDialog -> {
                _uiState.update {
                    it.copy(subDialogState = event.subDialogState)
                }
            }

            is EditQuestUiEvent.OnCloseSubDialog -> {
                _uiState.update {
                    it.copy(subDialogState = EditQuestSubDialogState.None)
                }
            }

            is EditQuestUiEvent.OnCreateQuestCategory -> {
                viewModelScope.launch {
                    addQuestCategoryUseCase.invoke(
                        questCategoryEntity = QuestCategoryEntity(
                            uuid = UUID.randomUUID().toString(),
                            text = event.value
                        )
                    )
                    syncUseCase()
                }
            }

            is EditQuestUiEvent.OnSelectQuestCategory -> {
                _selectedCategory.value = event.questCategoryEntity
            }

            is EditQuestUiEvent.OnUnselectQuestCategory -> {
                _selectedCategory.value = null
            }

            is EditQuestUiEvent.OnRemoveReminder -> {
                val updatedTimes = _uiState.value.notificationTriggerTimes.toMutableList().apply {
                    removeAt(event.index)
                }
                _uiState.value = _uiState.value.copy(notificationTriggerTimes = updatedTimes)
            }

            is EditQuestUiEvent.OnCreateReminder -> {
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

            is EditQuestUiEvent.OnCreateSubQuest -> {
                _uiState.update {
                    it.copy(
                        subQuests = _uiState.value.subQuests + SubQuestModel(
                            uuid = UUID.randomUUID().toString(),
                            text = ""
                        )
                    )
                }
            }

            is EditQuestUiEvent.OnUpdateSubQuest -> {
                _uiState.update { state ->
                    state.copy(
                        subQuests = state.subQuests.mapIndexed { i, subTask ->
                            if (i == event.index) subTask.copy(text = event.value) else subTask
                        }
                    )
                }
            }

            is EditQuestUiEvent.OnRemoveSubQuest -> {
                val subTasks = _uiState.value.subQuests.toMutableList()
                if (event.index in subTasks.indices) {
                    val itemToRemove = subTasks[event.index]
                    if (itemToRemove.id != 0) {
                        _subQuestsToDelete.add(itemToRemove.id)
                    }
                    subTasks.removeAt(event.index)
                    _uiState.update { it.copy(subQuests = subTasks) }
                }
            }

            is EditQuestUiEvent.OnMoveSubQuest -> {
                _uiState.update { state ->
                    val mutableList = state.subQuests.toMutableList()
                    val item = mutableList.removeAt(event.fromIndex - 3)
                    mutableList.add(event.toIndex - 3, item)

                    state.copy(subQuests = mutableList)
                }
            }

            is EditQuestUiEvent.OnSetDueDate -> {
                _uiState.update {
                    it.copy(
                        combinedDueDate = event.timestamp,
                        dialogState = EditQuestDialogState.None
                    )
                }
            }

            is EditQuestUiEvent.OnSetCombinedDueDate -> {
                _uiState.update {
                    it.copy(
                        combinedDueDate = event.timestamp,
                        dialogState = EditQuestDialogState.None
                    )
                }
            }

            is EditQuestUiEvent.OnUpdateDueTime -> {
                _uiState.update {
                    it.copy(
                        selectedDueTime = event.value,
                        subDialogState = EditQuestSubDialogState.None
                    )
                }
            }

            is EditQuestUiEvent.OnUpdateDueDate -> {
                _uiState.update {
                    it.copy(
                        selectedDueDate = event.value,
                        subDialogState = EditQuestSubDialogState.None
                    )
                }
            }

            is EditQuestUiEvent.OnUpdateTempDueDate -> {
                _uiState.update {
                    it.copy(
                        selectedDueTime = event.time,
                        selectedDueDate = event.date
                    )
                }
            }

            is EditQuestUiEvent.OnRemoveDueDate -> {
                _uiState.update {
                    it.copy(
                        combinedDueDate = 0L,
                        dialogState = EditQuestDialogState.None
                    )
                }
            }

            is EditQuestUiEvent.OnUpdateReminderState -> {
                _uiState.update {
                    it.copy(addingDateTimeState = event.value)
                }
            }

            else -> Unit
        }
    }
}
