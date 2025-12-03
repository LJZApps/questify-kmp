package de.ljz.questify.feature.profile.presentation.screens.view_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewProfileViewModel(
    private val getAppUserUseCase: GetAppUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = ViewProfileUiState(
            profilePictureUrl = "",
            displayName = "",
            aboutMe = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAppUserUseCase().collectLatest { appUser ->
                _uiState.update { currentState ->
                    currentState.copy(
                        displayName = appUser.displayName,
                        aboutMe = appUser.aboutMe,
                        profilePictureUrl = appUser.profilePicture
                    )
                }
            }
        }
    }

    fun onUiEvent(event: ViewProfileUiEvent) {
        when (event) {
            else -> Unit
        }
    }
}