package de.ljz.questify.core.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.domain.repositories.auth.AuthRepository
import de.ljz.questify.feature.settings.domain.use_cases.GetAppSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getAppSettingsUseCase().collectLatest { appSettings ->
                    _uiState.update {
                        it.copy(isSetupDone = appSettings.onboardingState)
                    }
                }
            }
            
            launch {
                authRepository.authTokens.collectLatest { authTokens ->
                    _uiState.update {
                        it.copy(isLoggedIn = authTokens.questifyToken != null)
                    }
                    _isAppReady.update { true }
                }
            }
        }
    }
}