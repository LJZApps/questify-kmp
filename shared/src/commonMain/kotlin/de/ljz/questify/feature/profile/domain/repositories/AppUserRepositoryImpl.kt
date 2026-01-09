package de.ljz.questify.feature.profile.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.feature.profile.data.models.AppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class AppUserRepositoryImpl(
    private val appUserDataStore: DataStore<AppUser>,
    private val remoteDataSource: de.ljz.questify.core.data.remote.QuestifyRemoteDataSource
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
        // In an offline-first approach, we would mark it as unsynced and sync later.
        // For simplicity, we try to push it now if we have a username.
        val user = appUserDataStore.data.first()
        try {
            remoteDataSource.updateProfile(user.username, user.displayName, user.aboutMe)
        } catch (e: Exception) {
            // Handle error (silent in background sync)
        }
    }

    override suspend fun fetchRemoteProfile(): Result<AppUser> {
        return try {
            val remoteUser = remoteDataSource.getProfile()
            val user = AppUser(
                id = remoteUser.id ?: -1,
                username = remoteUser.username ?: "",
                displayName = remoteUser.realName ?: "Abenteurer",
                aboutMe = remoteUser.bio ?: "",
                email = remoteUser.email ?: "",
                profilePicture = remoteUser.avatarUrl ?: "",
                level = remoteUser.level ?: 1
            )
            appUserDataStore.updateData { user }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}