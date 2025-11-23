package de.ljz.questify.core.presentation.theme

/*@HiltViewModel
class ThemeViewModel @Inject constructor(
    appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    val uiState: StateFlow<ThemeUiState> = appSettingsRepository.getAppSettings()
        .map { settings ->
            // Wandelt das Einstellungs-Objekt direkt in den UI-State um
            ThemeUiState(
                themeBehavior = settings.themeBehavior
            )
        }
        .stateIn(
            scope = viewModelScope,
            // Der Flow ist nur aktiv, wenn die UI ihn wirklich braucht
            started = SharingStarted.WhileSubscribed(5000),
            // Startwert, bevor der erste Wert vom Flow ankommt
            initialValue = ThemeUiState()
        )
}*/
