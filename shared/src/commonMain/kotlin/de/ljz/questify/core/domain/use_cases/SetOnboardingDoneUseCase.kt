package de.ljz.questify.core.domain.use_cases

import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepositoryImpl

class SetOnboardingDoneUseCase(
    private val appSettingsRepository: AppSettingsRepositoryImpl
) {
    suspend operator fun invoke() {
        appSettingsRepository.setOnboardingDone()
    }
}