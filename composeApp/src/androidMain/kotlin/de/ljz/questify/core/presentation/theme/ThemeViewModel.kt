package de.ljz.questify.core.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeViewModel(
    appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = ThemeUiState(
            themeBehavior = ThemeBehavior.SYSTEM_STANDARD
        )
    )
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsRepository.getAppSettings().collectLatest { appSettings ->
                _uiState.update {
                    it.copy(
                        themeBehavior = appSettings.themeBehavior
                    )
                }
            }
        }
    }
}