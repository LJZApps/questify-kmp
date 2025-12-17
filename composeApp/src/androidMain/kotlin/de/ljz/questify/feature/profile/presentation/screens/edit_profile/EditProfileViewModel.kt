package de.ljz.questify.feature.profile.presentation.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.feature.profile.domain.use_cases.GetAppUserUseCase
import de.ljz.questify.feature.profile.domain.use_cases.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getAppUserUseCase: GetAppUserUseCase,
    private val saveProfileUseCase: SaveProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = EditProfileUiState(
            profilePictureUrl = "",
            displayName = "Adventurer",
            aboutMe = "",
            pickedProfilePicture = false
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

    fun onUiEvent(event: EditProfileUiEvent) {
        when (event) {
            is EditProfileUiEvent.SaveProfile -> {
                viewModelScope.launch {
                    saveProfileUseCase(
                        displayName = _uiState.value.displayName,
                        aboutMe = _uiState.value.aboutMe,
                        imageUri = event.profilePictureUrl
                    )
                }
            }

            is EditProfileUiEvent.UpdateProfilePicture -> {
                _uiState.update {
                    it.copy(
                        profilePictureUrl = event.profilePictureUrl,
                        pickedProfilePicture = true
                    )
                }
            }

            is EditProfileUiEvent.UpdateDisplayName -> {
                _uiState.update {
                    it.copy(displayName = event.displayName)
                }
            }

            is EditProfileUiEvent.UpdateAboutMe -> {
                _uiState.update {
                    it.copy(aboutMe = event.aboutMe)
                }
            }

            else -> Unit
        }
    }
}