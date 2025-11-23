package de.ljz.questify.core.di

import de.ljz.questify.core.data.datastore.createDataStore
import de.ljz.questify.core.data.datastore.dataStorePreferencesPath
import de.ljz.questify.core.data.datastore.serializer.AppSettingsSerializer
import de.ljz.questify.core.data.datastore.serializer.SortingPreferencesSerializer
import de.ljz.questify.core.data.models.SortingPreferences
import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.data.models.FeatureSettings
import de.ljz.questify.feature.settings.data.serializer.FeatureSettingsSerializer
import org.koin.dsl.module

val dataStoreModule = module {
    single {
        createDataStore(
            producePath = { dataStorePreferencesPath("sorting_preferences.json") },
            serializer = SortingPreferencesSerializer,
            defaultValue = SortingPreferences()
        )
    }

    single {
        createDataStore(
            producePath = { dataStorePreferencesPath("app_settings.json") },
            serializer = AppSettingsSerializer,
            defaultValue = AppSettings()
        )
    }

    single {
        createDataStore(
            producePath = { dataStorePreferencesPath("feature_settings.json") },
            serializer = FeatureSettingsSerializer,
            defaultValue = FeatureSettings()
        )
    }
}