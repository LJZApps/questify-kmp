package de.ljz.questify.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.auth.PkceGenerator
import de.ljz.questify.core.domain.repositories.AuthRepository
import de.ljz.questify.core.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object NavigateToHome : LoginUiState()
    data class NavigateToOnboarding(val username: String?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var currentCodeVerifier: String? = null

    fun getLoginUrl(): String {
        println("LoginViewModel: Generiere Login URL")
        val verifier = PkceGenerator.generateCodeVerifier()
        val challenge = PkceGenerator.generateCodeChallenge(verifier)

        currentCodeVerifier = verifier
        println("LoginViewModel: CodeVerifier generiert: $verifier")

        val url = "${Constants.OMRIX_ISSUER_URI}/oauth/authorize" +
                "?response_type=code" +
                "&client_id=${Constants.OMRIX_CLIENT_ID}" +
                "&redirect_uri=${Constants.OMRIX_REDIRECT_URI}" +
                "&scope=user-profile email" +
                "&code_challenge=$challenge" +
                "&code_challenge_method=S256"
        
        println("LoginViewModel: URL erstellt: $url")
        return url
    }

    fun handleAuthCode(code: String) {
        println("LoginViewModel: handleAuthCode aufgerufen mit Code: $code")
        val verifier = currentCodeVerifier
        if (verifier == null) {
            println("LoginViewModel: ERROR - currentCodeVerifier ist NULL!")
            _uiState.value = LoginUiState.Error("Sicherheitsfehler: Verifier verloren. Bitte App neu starten.")
            return
        }

        viewModelScope.launch {
            println("LoginViewModel: Starte performLogin in AuthRepository")
            _uiState.value = LoginUiState.Loading

            val result = authRepository.performLogin(code, verifier)

            result.onSuccess { profile ->
                println("LoginViewModel: Login erfolgreich für ${profile.username}. NeedsOnboarding: ${profile.needsOnboarding}")
                if (profile.needsOnboarding) {
                    _uiState.value = LoginUiState.NavigateToOnboarding(profile.username)
                } else {
                    _uiState.value = LoginUiState.NavigateToHome
                }
            }.onFailure { error ->
                println("LoginViewModel: Login fehlgeschlagen mit Error: ${error.message}")
                error.printStackTrace()
                _uiState.value = LoginUiState.Error(error.message ?: "Login fehlgeschlagen")
            }
        }
    }
}