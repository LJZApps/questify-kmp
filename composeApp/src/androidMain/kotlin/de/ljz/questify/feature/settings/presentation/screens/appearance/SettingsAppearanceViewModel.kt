package de.ljz.questify.feature.settings.presentation.screens.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.settings.domain.use_cases.GetAppSettingsUseCase
import de.ljz.questify.feature.settings.domain.use_cases.SetDarkModeBehaviorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsAppearanceViewModel(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val setDarkModeBehaviorUseCase: SetDarkModeBehaviorUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsAppearanceUiState())
    val uiState: StateFlow<SettingsAppearanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAppSettingsUseCase().collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        themeBehavior = settings.themeBehavior
                    )
                }
            }
        }
    }

    fun onUiEvent(event: SettingsAppearanceUiEvent) {
        when (event) {

            is SettingsAppearanceUiEvent.ShowDarkModeDialog -> {
                _uiState.update {
                    it.copy(
                        darkModeDialogVisible = true
                    )
                }
            }

            is SettingsAppearanceUiEvent.HideDarkModeDialog -> {
                _uiState.update {
                    it.copy(
                        darkModeDialogVisible = false
                    )
                }
            }

            is SettingsAppearanceUiEvent.UpdateThemeBehavior -> {
                viewModelScope.launch {
                    setDarkModeBehaviorUseCase.invoke(event.behavior)
                }
            }

            else -> Unit
        }
    }
}