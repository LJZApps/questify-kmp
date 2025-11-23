package de.ljz.questify.feature.settings.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.data.models.descriptors.ThemeBehavior
import kotlinx.coroutines.flow.Flow

class AppSettingsRepositoryImpl(
    private val appSettingsDataStore: DataStore<AppSettings>
) : AppSettingsRepository {

    override fun getAppSettings(): Flow<AppSettings> {
        return appSettingsDataStore.data
    }

    override suspend fun setOnboardingDone() {
        appSettingsDataStore.updateData {
            it.copy(
                onboardingState = true
            )
        }
    }

    override suspend fun resetOnboarding() {
        appSettingsDataStore.updateData {
            it.copy(
                onboardingState = false
            )
        }
    }

    override suspend fun setDarkModeBehavior(value: ThemeBehavior) {
        appSettingsDataStore.updateData {
            it.copy(
                themeBehavior = value
            )
        }
    }
}