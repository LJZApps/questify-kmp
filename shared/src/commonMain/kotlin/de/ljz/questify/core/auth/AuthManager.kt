package de.ljz.questify.core.auth

interface PkceGenerator {
    fun generateVerifier(): String
    fun generateChallenge(verifier: String): String
}