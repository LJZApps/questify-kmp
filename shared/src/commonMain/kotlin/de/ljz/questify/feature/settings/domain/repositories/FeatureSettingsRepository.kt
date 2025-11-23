package de.ljz.questify.feature.settings.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.settings.data.models.FeatureSettings
import kotlinx.coroutines.flow.Flow

class FeatureSettingsRepository(
    private val featureSettingsDataStore: DataStore<FeatureSettings>
) {

    fun getFeatureSettings(): Flow<FeatureSettings> {
        return featureSettingsDataStore.data
    }

    suspend fun setQuestFastAddingEnabled(enabled: Boolean) {
        featureSettingsDataStore.updateData {
            it.copy(
                questFastAddingEnabled = enabled
            )
        }
    }
}