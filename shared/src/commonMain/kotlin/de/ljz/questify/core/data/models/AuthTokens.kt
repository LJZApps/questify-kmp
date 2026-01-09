package de.ljz.questify.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val questifyToken: String? = null,
    val idToken: String? = null,
    val expiresIn: Long? = null,
    val issuedAt: Long? = null
)
