package de.ljz.questify.feature.auth.presentation.screens.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.domain.repositories.auth.AuthConfig
import de.ljz.questify.core.domain.repositories.auth.AuthRepository
import de.ljz.questify.core.util.PKCEUtil
import de.ljz.questify.feature.profile.domain.use_cases.FetchRemoteProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val fetchRemoteProfileUseCase: FetchRemoteProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnLoginClicked -> onLoginClicked(event.onNavigateToBrowser)
            is LoginUiEvent.HandleAuthCode -> handleAuthCode(event.code)
        }
    }

    private fun onLoginClicked(onNavigateToBrowser: (url: String) -> Unit) {
        val verifier = PKCEUtil.generateCodeVerifier()
        val challenge = PKCEUtil.generateCodeChallenge(verifier)
        authRepository.codeVerifier = verifier

        val authUrl = AuthConfig.AUTHORIZE_URL +
                "?response_type=code" +
                "&client_id=${AuthConfig.CLIENT_ID}" +
                "&redirect_uri=${AuthConfig.REDIRECT_URI}" +
                "&code_challenge=$challenge" +
                "&code_challenge_method=S256" +
                "&scope=${AuthConfig.SCOPE}"

        onNavigateToBrowser(authUrl)
    }

    private fun handleAuthCode(code: String) {
        val verifier = authRepository.codeVerifier ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.exchangeCodeForTokens(code, verifier)
            
            if (result.isSuccess) {
                val omrixTokens = result.getOrThrow()
                val questifyResult = authRepository.loginWithOmrix(omrixTokens.accessToken ?: "")

                if (questifyResult.isSuccess) {
                    val loginResponse = questifyResult.getOrThrow()
                    val profile = loginResponse.effectiveProfile
                    authRepository.codeVerifier = null

                    if (profile?.needsOnboarding == true) {
                        _uiState.update { it.copy(isLoading = false, navigateToOnboarding = true) }
                    } else {
                        // Profil abrufen und auf Abschluss warten, bevor wir fortfahren
                        // Damit die Daten sicher da sind, wenn wir zum Dashboard wechseln
                        try {
                            fetchRemoteProfileUseCase()
                        } catch (e: Exception) {
                            // Fehler beim Profil-Abruf loggen, aber Login trotzdem erlauben
                            e.printStackTrace()
                        }
                        _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = questifyResult.exceptionOrNull()?.message) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
