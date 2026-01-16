package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.data.models.auth.PlayerProfileDto

interface AuthRepository {
    suspend fun performLogin(authCode: String, codeVerifier: String): Result<PlayerProfileDto>
    suspend fun logout()
}