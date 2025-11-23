package de.ljz.questify.core.presentation.screens

/*
@HiltViewModel
class AppViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                val appSettings = appSettingsRepository.getAppSettings().firstOrNull()

                _uiState.update {
                    it.copy(
                        isSetupDone = appSettings?.onboardingState == true
                    )
                }

                _isAppReady.update { true }
            }

            launch {
                notificationHelper.createNotificationChannel()
            }
        }
    }
}
*/
