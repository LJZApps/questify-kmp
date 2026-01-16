package de.ljz.questify.feature.profile.presentation.screens.username_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.domain.use_cases.SyncUseCase
import de.ljz.questify.feature.profile.domain.use_cases.CheckUsernameUseCase
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsernameSetupViewModel(
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getAppUserUseCase: GetAppUserUseCase,
    private val syncUseCase: SyncUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsernameSetupUiState())
    val uiState = _uiState.asStateFlow()

    private var checkJob: Job? = null

    fun onUiEvent(event: UsernameSetupUiEvent) {
        when (event) {
            is UsernameSetupUiEvent.UpdateUsername -> {
                _uiState.update { it.copy(username = event.username, isAvailable = null) }
                checkUsername(event.username)
            }

            is UsernameSetupUiEvent.Submit -> {
                submitUsername()
            }
        }
    }

    private fun checkUsername(username: String) {
        checkJob?.cancel()
        if (username.isBlank() || username.length < 3) {
            _uiState.update { it.copy(isAvailable = null, isChecking = false) }
            return
        }

        checkJob = viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            delay(500) // Debounce
            val isAvailable = checkUsernameUseCase(username)
            _uiState.update { it.copy(isAvailable = isAvailable, isChecking = false) }
        }
    }

    private fun submitUsername() {
        val currentState = _uiState.value
        if (currentState.isAvailable != true) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val appUser = getAppUserUseCase().first()
                saveProfileUseCase(
                    username = currentState.username,
                    displayName = appUser.displayName,
                    aboutMe = appUser.aboutMe,
                    imageUri = appUser.profilePicture
                )
                syncUseCase()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
