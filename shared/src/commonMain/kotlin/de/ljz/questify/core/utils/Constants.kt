package de.ljz.questify.core.utils

object Constants {
    const val OMRIX_ISSUER_URI = "https://id.omrix.net"
    const val OMRIX_TOKEN_ENDPOINT = "$OMRIX_ISSUER_URI/oauth/token"
    const val OMRIX_CLIENT_ID = "questify-app"
    const val OMRIX_REDIRECT_URI = "de.ljz.questify://callback"

    // 2. Resource Server (Dein Questify Backend)
    const val QUESTIFY_API_URL = "https://questify.omrix.net/api"
    const val QUESTIFY_HOST = "questify.omrix.net"
}