package de.ljz.questify.feature.profile.presentation.screens.username_setup

sealed interface UsernameSetupUiEvent {
    data class UpdateUsername(val username: String) : UsernameSetupUiEvent
    data object Submit : UsernameSetupUiEvent
}
