package de.ljz.questify.feature.auth.presentation.screens.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val navigateToOnboarding: Boolean = false
)
