package de.ljz.questify.feature.profile.domain.repositories

import de.ljz.questify.feature.profile.data.models.AppUser
import kotlinx.coroutines.flow.Flow

interface AppUserRepository {
    fun getAppUser(): Flow<AppUser>

    suspend fun saveProfile(
        displayName: String,
        aboutMe: String,
        imageUri: String
    )
}