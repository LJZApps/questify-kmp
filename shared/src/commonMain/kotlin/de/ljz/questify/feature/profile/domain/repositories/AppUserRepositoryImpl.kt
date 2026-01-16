package de.ljz.questify.feature.profile.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.profile.data.models.AppUser
import de.ljz.questify.feature.profile.data.network.ProfileService
import de.ljz.questify.feature.profile.data.network.dto.ProfileUpdateRequestDto
import kotlinx.coroutines.flow.Flow

internal class AppUserRepositoryImpl(
    private val appUserDataStore: DataStore<AppUser>,
    private val profileService: ProfileService
) : AppUserRepository {
    override fun getAppUser(): Flow<AppUser> {
        return appUserDataStore.data
    }

    override suspend fun saveProfile(
        username: String,
        displayName: String,
        aboutMe: String,
        imageUri: String
    ) {
        // Lokal speichern
        appUserDataStore.updateData { appUser ->
            appUser.copy(
                username = username,
                displayName = displayName,
                aboutMe = aboutMe,
                profilePicture = imageUri
            )
        }

        // An API senden
        try {
            profileService.updateProfile(
                ProfileUpdateRequestDto(
                    username = username,
                    realName = displayName,
                    bio = aboutMe
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun syncProfile() {
        try {
            val remoteProfile = profileService.getProfile()
            appUserDataStore.updateData { appUser ->
                appUser.copy(
                    id = remoteProfile.id ?: appUser.id,
                    username = remoteProfile.username ?: appUser.username,
                    displayName = remoteProfile.realName ?: appUser.displayName,
                    aboutMe = remoteProfile.bio ?: appUser.aboutMe,
                    profilePicture = remoteProfile.avatarUrl ?: appUser.profilePicture,
                    email = remoteProfile.email ?: appUser.email,
                    level = remoteProfile.level ?: appUser.level,
                    xp = remoteProfile.xp ?: appUser.xp
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun checkUsername(username: String): Boolean {
        return try {
            profileService.checkUsername(username).available
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}