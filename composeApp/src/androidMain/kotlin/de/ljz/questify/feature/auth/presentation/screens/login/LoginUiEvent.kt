package de.ljz.questify.feature.auth.presentation.screens.login

sealed interface LoginUiEvent {
    data class OnLoginClicked(val onNavigateToBrowser: (url: String) -> Unit) : LoginUiEvent
    data class HandleAuthCode(val code: String) : LoginUiEvent
}
