package de.ljz.questify.feature.main.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ljz.questify.core.data.sync.SyncCoordinator
import de.ljz.questify.feature.profile.domain.repositories.AppUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val appUserRepository: AppUserRepository,
    private val syncCoordinator: SyncCoordinator
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        value = MainUiState(
            userPoints = 0,
            userXP = 0,
            userLevel = 0,
            userName = "Adventurer",
            userProfilePicture = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        // Starte globale Synchronisation (Profil + Quests)
        syncCoordinator.syncAll()

        viewModelScope.launch {
            appUserRepository.getAppUser().collectLatest { appUser ->
                _uiState.update {
                    it.copy(
                        userName = appUser.displayName,
                        userProfilePicture = appUser.profilePicture,
                        userLevel = appUser.level
                    )
                }
            }
        }
    }
}
