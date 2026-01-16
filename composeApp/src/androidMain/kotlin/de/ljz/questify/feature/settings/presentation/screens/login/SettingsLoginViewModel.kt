package de.ljz.questify.feature.settings.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.auth.PkceGenerator
import de.ljz.questify.core.domain.repositories.AuthRepository
import de.ljz.questify.core.domain.use_cases.SyncUseCase
import de.ljz.questify.core.utils.Constants
import de.ljz.questify.feature.profile.domain.use_cases.SyncProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsLoginViewModel(
    private val authRepository: AuthRepository,
    private val syncProfileUseCase: SyncProfileUseCase,
    private val syncUseCase: SyncUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = SettingsLoginUiState()
    )
    val uiState = _uiState.asStateFlow()

    private var currentCodeVerifier: String? = null

    fun getLoginUrl(): String {
        val verifier = PkceGenerator.generateCodeVerifier()
        val challenge = PkceGenerator.generateCodeChallenge(verifier)

        currentCodeVerifier = verifier

        return "${Constants.OMRIX_ISSUER_URI}/oauth/authorize" +
                "?response_type=code" +
                "&client_id=${Constants.OMRIX_CLIENT_ID}" +
                "&redirect_uri=${Constants.OMRIX_REDIRECT_URI}" +
                "&scope=user-profile email" +
                "&code_challenge=$challenge" +
                "&code_challenge_method=S256"
    }

    fun onUiEvent(event: SettingsLoginUiEvent) {
        when (event) {
            is SettingsLoginUiEvent.OnShowDialog -> {
                _uiState.update {
                    it.copy(dialogState = event.dialogState)
                }
            }

            is SettingsLoginUiEvent.OnCloseDialog -> {
                _uiState.update {
                    it.copy(dialogState = SettingsLoginDialogState.None)
                }
            }

            is SettingsLoginUiEvent.NavigationHandled -> {
                _uiState.update { it.copy(navigationTarget = null) }
            }

            is SettingsLoginUiEvent.ErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }

            is SettingsLoginUiEvent.HandleAuthCode -> {
                val verifier = currentCodeVerifier
                if (verifier == null) {
                    _uiState.update { it.copy(error = "Sicherheitsfehler: Verifier verloren.") }
                    return
                }

                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }

                    val result = authRepository.performLogin(event.code, verifier)

                    result.onSuccess { profileDto ->
                        _uiState.update { it.copy(isLoading = true) }
                        syncProfileUseCase()
                        syncUseCase()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                navigationTarget = if (profileDto.username == null) {
                                    LoginNavigationTarget.UsernameSetup
                                } else {
                                    LoginNavigationTarget.Back
                                },
                            )
                        }
                    }.onFailure { error ->
                        error.printStackTrace()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Login fehlgeschlagen"
                            )
                        }
                    }
                }
            }
        }
    }
}