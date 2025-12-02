package de.ljz.questify.feature.settings.domain.use_cases

import de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior
import de.ljz.questify.feature.settings.domain.repositories.AppSettingsRepository

class SetDarkModeBehaviorUseCase(
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(value: ThemeBehavior) {
        appSettingsRepository.setDarkModeBehavior(value = value)
    }
}