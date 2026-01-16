package de.ljz.questify.feature.settings.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getAppUserUseCase: GetAppUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = SettingsUIState(
            userName = "",
            userProfilePicture = "",
            aboutMe = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAppUserUseCase.invoke().collectLatest { appUser ->
                _uiState.update {
                    it.copy(
                        userName = appUser.displayName,
                        userProfilePicture = appUser.profilePicture,
                        aboutMe = appUser.aboutMe,
                        isLoggedIn = appUser.id != -1
                    )
                }
            }
        }
    }
}