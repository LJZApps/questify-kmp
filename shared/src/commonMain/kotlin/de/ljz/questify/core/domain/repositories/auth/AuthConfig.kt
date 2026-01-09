package de.ljz.questify.core.domain.repositories.auth

object AuthConfig {
    const val BASE_URL = "https://id.omrix.net"
    const val AUTHORIZE_URL = "$BASE_URL/oauth/authorize"
    const val TOKEN_URL = "$BASE_URL/oauth/token"
    const val CLIENT_ID = "019ba1cc-10c5-71b6-a0c6-f063600fbd11"
    const val REDIRECT_URI = "questify://auth/redirect"
    const val SCOPE = "user-profile"

    const val PROFILE_URL = "$BASE_URL/api/user/profile"
    
    const val QUESTIFY_API_BASE_URL = "https://questify.omrix.net/api/v1"
    const val QUESTIFY_PROFILE_URL = "$QUESTIFY_API_BASE_URL/user"
}
