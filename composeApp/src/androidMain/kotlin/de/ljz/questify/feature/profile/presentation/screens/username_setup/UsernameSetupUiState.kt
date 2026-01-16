package de.ljz.questify.feature.profile.presentation.screens.username_setup

data class UsernameSetupUiState(
    val username: String = "",
    val isAvailable: Boolean? = null,
    val isLoading: Boolean = false,
    val isChecking: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
