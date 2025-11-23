package de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.data.models.descriptors.SortingDirections
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestsForCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryQuestViewModel(
    private val categoryId: Int,

    private val getAllQuestsForCategoryUseCase: GetAllQuestsForCategoryUseCase,

//    private val getQuestSortingPreferencesUseCase: GetSortingPreferencesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = CategoryQuestsUiState(
            quests = emptyList(),
            sortingDirections = SortingDirections.ASCENDING,
            showCompleted = true
        )
    )
    val uiState: StateFlow<CategoryQuestsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getAllQuestsForCategoryUseCase.invoke(categoryId).collect { quests ->
                    _uiState.update {
                        it.copy(quests = quests)
                    }
                }
            }

            /*launch {
                getQuestSortingPreferencesUseCase()
                    .collectLatest { sortingData ->
                        _uiState.update { state ->
                            state.copy(
                                sortingDirections = sortingData.questSortingDirection,
                                showCompleted = sortingData.showCompletedQuests
                            )
                        }
                    }
            }*/
        }
    }
}