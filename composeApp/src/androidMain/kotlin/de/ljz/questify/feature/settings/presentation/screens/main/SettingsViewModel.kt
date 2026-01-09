package de.ljz.questify.feature.settings.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.domain.repositories.auth.AuthRepository
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getAppUserUseCase: GetAppUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = SettingsUIState(
            userName = "",
            userProfilePicture = "",
            aboutMe = "",
            isLoggedIn = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getAppUserUseCase.invoke(),
                authRepository.authTokens
            ) { appUser, tokens ->
                _uiState.update {
                    it.copy(
                        userName = appUser.displayName,
                        userProfilePicture = appUser.profilePicture,
                        aboutMe = appUser.aboutMe,
                        isLoggedIn = tokens.accessToken != null
                    )
                }
            }.collect()
        }
    }
}