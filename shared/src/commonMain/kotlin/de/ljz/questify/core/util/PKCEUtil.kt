package de.ljz.questify.core.util

expect object PKCEUtil {
    fun generateCodeVerifier(): String
    fun generateCodeChallenge(codeVerifier: String): String
}
