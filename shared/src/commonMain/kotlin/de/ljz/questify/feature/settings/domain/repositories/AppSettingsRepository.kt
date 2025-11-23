package de.ljz.questify.feature.settings.domain.repositories

import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getAppSettings(): Flow<AppSettings>

    suspend fun setOnboardingDone()

    suspend fun resetOnboarding()

    suspend fun setDarkModeBehavior(value: ThemeBehavior)
}
