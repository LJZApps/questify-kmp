package de.ljz.questify.core.utils

object Constants {
    const val OMRIX_ISSUER_URI = "https://id.omrix.net"
    const val OMRIX_TOKEN_ENDPOINT = "$OMRIX_ISSUER_URI/oauth/token"
    const val OMRIX_CLIENT_ID = "019bc584-9423-719b-9596-6f669a072102"
    const val OMRIX_REDIRECT_URI = "de.ljz.questify://callback"

    // 2. Resource Server (Dein Questify Backend)
    const val QUESTIFY_API_URL = "https://questify.omrix.net/api/v1"
    const val QUESTIFY_HOST = "questify.omrix.net"
}