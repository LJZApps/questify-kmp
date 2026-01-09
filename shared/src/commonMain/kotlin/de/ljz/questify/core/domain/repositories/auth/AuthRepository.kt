package de.ljz.questify.core.domain.repositories.auth

import de.ljz.questify.core.data.models.AuthTokens
import de.ljz.questify.core.data.models.QuestifyLoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authTokens: Flow<AuthTokens>
    var codeVerifier: String?
    suspend fun exchangeCodeForTokens(code: String, codeVerifier: String): Result<AuthTokens>
    suspend fun loginWithOmrix(omrixAccessToken: String): Result<QuestifyLoginResponse>
    suspend fun refreshTokens(): Result<AuthTokens>
    suspend fun logout()
    suspend fun getAccessToken(): String?
}
