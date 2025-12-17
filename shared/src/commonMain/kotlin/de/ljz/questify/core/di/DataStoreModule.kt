package de.ljz.questify.core.di

import de.ljz.questify.core.data.datastore.createDataStore
import de.ljz.questify.core.data.datastore.dataStorePreferencesPath
import de.ljz.questify.core.data.datastore.serializer.SortingPreferencesSerializer
import de.ljz.questify.core.data.models.SortingPreferences
import de.ljz.questify.feature.profile.data.models.AppUser
import de.ljz.questify.feature.profile.data.serializer.AppUserSerializer
import de.ljz.questify.feature.settings.data.models.AppSettings
import de.ljz.questify.feature.settings.data.models.FeatureSettings
import de.ljz.questify.feature.settings.data.serializer.AppSettingsSerializer
import de.ljz.questify.feature.settings.data.serializer.FeatureSettingsSerializer
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dataStoreModule = module {
    single(named("sorting_preferences")) {
        createDataStore(
            producePath = { dataStorePreferencesPath("sorting_preferences.json") },
            serializer = SortingPreferencesSerializer,
            defaultValue = SortingPreferences()
        )
    }

    single(named("app_settings")) {
        createDataStore(
            producePath = { dataStorePreferencesPath("app_settings.json") },
            serializer = AppSettingsSerializer,
            defaultValue = AppSettings()
        )
    }

    single(named("feature_settings")) {
        createDataStore(
            producePath = { dataStorePreferencesPath("feature_settings.json") },
            serializer = FeatureSettingsSerializer,
            defaultValue = FeatureSettings()
        )
    }

    single(named("app_user")) {
        createDataStore(
            producePath = { dataStorePreferencesPath("app_user.json") },
            serializer = AppUserSerializer,
            defaultValue = AppUser()
        )
    }
}