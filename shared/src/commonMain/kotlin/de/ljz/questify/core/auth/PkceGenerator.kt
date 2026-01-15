package de.ljz.questify.core.auth

expect object PkceGenerator {
    fun generateCodeVerifier(): String
    fun generateCodeChallenge(verifier: String): String
}