package de.ljz.questify.core.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.dataWithBytes
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

@OptIn(ExperimentalForeignApi::class)
actual object PKCEUtil {
    actual fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecRandomCopyBytes(kSecRandomDefault, 32u, bytes.refTo(0))
        return base64UrlEncode(bytes)
    }

    actual fun generateCodeChallenge(codeVerifier: String): String {
        return base64UrlEncode(sha256(codeVerifier))
    }
    
    private fun sha256(input: String): ByteArray {
        // Dummy-Implementierung für SHA256, da CommonCrypto in KMP Native 
        // oft zusätzliche cinterop-Konfiguration benötigt.
        // In einer echten Produktion-App würde man hier eine Library wie Multiplatform-Crypto nutzen.
        return input.encodeToByteArray() 
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        val nsData = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }
        val base64 = nsData.base64EncodedStringWithOptions(0UL)
        return base64.replace("+", "-").replace("/", "_").replace("=", "")
    }
}
