package de.ljz.questify.core.domain.repositories

import de.ljz.questify.core.data.models.auth.TokenResponse

interface AuthRepository {
    suspend fun performLogin(authCode: String, codeVerifier: String): Result<Unit>
    suspend fun refreshToken(refreshToken: String): TokenResponse?
    suspend fun logout()
}