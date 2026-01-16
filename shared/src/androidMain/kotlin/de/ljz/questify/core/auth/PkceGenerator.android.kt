package de.ljz.questify.core.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

actual object PkceGenerator {
    actual fun generateCodeVerifier(): String {
        println("PkceGenerator: Generiere Code Verifier")
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(32)
        secureRandom.nextBytes(codeVerifier)
        val result = Base64.encodeToString(
            codeVerifier,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        println("PkceGenerator: Verifier generiert (Länge: ${result.length})")
        return result
    }

    actual fun generateCodeChallenge(verifier: String): String {
        println("PkceGenerator: Generiere Code Challenge aus Verifier")
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()

        val result = Base64.encodeToString(
            digest,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        println("PkceGenerator: Challenge generiert: $result")
        return result
    }
}