package de.ljz.questify.feature.profile.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.profile.data.models.AppUser
import kotlinx.coroutines.flow.Flow

internal class AppUserRepositoryImpl(
    private val appUserDataStore: DataStore<AppUser>
) : AppUserRepository {
    override fun getAppUser(): Flow<AppUser> {
        return appUserDataStore.data
    }

    override suspend fun saveProfile(
        displayName: String,
        aboutMe: String,
        imageUri: String
    ) {
        appUserDataStore.updateData { appUser ->
            appUser.copy(
                displayName = displayName,
                aboutMe = aboutMe,
                profilePicture = imageUri
            )
        }
    }
}