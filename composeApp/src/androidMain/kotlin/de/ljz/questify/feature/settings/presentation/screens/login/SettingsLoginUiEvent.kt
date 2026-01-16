package de.ljz.questify.feature.settings.presentation.screens.login

sealed interface SettingsLoginUiEvent {
    data class HandleAuthCode(val code: String): SettingsLoginUiEvent
    data object ErrorShown : SettingsLoginUiEvent
    data object NavigationHandled : SettingsLoginUiEvent

    data class OnShowDialog(val dialogState: SettingsLoginDialogState) : SettingsLoginUiEvent
    object OnCloseDialog : SettingsLoginUiEvent
}