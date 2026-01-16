package de.ljz.questify.feature.profile.domain.repositories

import de.ljz.questify.feature.profile.data.models.AppUser
import kotlinx.coroutines.flow.Flow

interface AppUserRepository {
    fun getAppUser(): Flow<AppUser>

    suspend fun saveProfile(
        username: String,
        displayName: String,
        aboutMe: String,
        imageUri: String
    )

    suspend fun syncProfile()

    suspend fun checkUsername(username: String): Boolean
}