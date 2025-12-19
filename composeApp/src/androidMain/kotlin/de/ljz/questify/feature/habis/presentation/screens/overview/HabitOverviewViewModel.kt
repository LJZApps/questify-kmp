package de.ljz.questify.feature.habis.presentation.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.habits.domain.use_cases.GetAllActiveHabitsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HabitOverviewViewModel(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = HabitOverviewUiState(
            habits = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllActiveHabitsUseCase().collectLatest { habitEntities ->
                _uiState.update { state ->
                    state.copy(
                        habits = habitEntities
                    )
                }
            }
        }
    }

    fun onUiEvent(event: HabitOverviewUiEvent) {
        when (event) {
            else -> Unit
        }
    }
}