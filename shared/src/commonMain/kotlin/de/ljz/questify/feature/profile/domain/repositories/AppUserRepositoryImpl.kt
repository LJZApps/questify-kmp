package de.ljz.questify.feature.profile.domain.repositories

import androidx.datastore.core.DataStore
import de.ljz.questify.core.data.remote.util.NetworkResult
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
        
        val user = appUserDataStore.data.first()
        // Fire and forget update (could be handled via sync manager in future)
        remoteDataSource.updateProfile(user.username, user.displayName, user.aboutMe)
    }

    override suspend fun fetchRemoteProfile(): Result<AppUser> {
        return when (val result = remoteDataSource.getProfile()) {
            is NetworkResult.Success -> {
                val remoteUser = result.data
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
            }
            is NetworkResult.Error -> Result.failure(Exception("API Error: ${result.code} ${result.message}"))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }
}
