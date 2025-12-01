package de.ljz.questify.feature.settings.domain.use_cases

import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.Flow

class GetAppSettingsUseCase(
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(): Flow<AppSettings> {
        return appSettingsRepository.getAppSettings()
    }
}