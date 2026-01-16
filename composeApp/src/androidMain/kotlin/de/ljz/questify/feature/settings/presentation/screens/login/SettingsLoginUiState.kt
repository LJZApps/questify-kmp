package de.ljz.questify.feature.settings.presentation.screens.login

data class SettingsLoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dialogState: SettingsLoginDialogState = SettingsLoginDialogState.None,
    val navigationTarget: LoginNavigationTarget? = null
)

sealed interface LoginNavigationTarget {
    data object Back : LoginNavigationTarget
    data object UsernameSetup : LoginNavigationTarget
}

sealed class SettingsLoginDialogState {
    object None : SettingsLoginDialogState()
    object OmrixInformation : SettingsLoginDialogState()
}