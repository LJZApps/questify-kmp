package de.ljz.questify.core.auth

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.reinterpret
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.dataUsingEncoding
import platform.Foundation.dataWithLength
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

actual object PkceGenerator {
    @OptIn(ExperimentalForeignApi::class)
    actual fun generateCodeVerifier(): String {
        val length = 32
        val data = NSMutableData.dataWithLength(length.toULong())!!

        // Zufallsbytes generieren
        val result = SecRandomCopyBytes(kSecRandomDefault, length.toULong(), data.mutableBytes)

        if (result != 0) throw IllegalStateException("Failed to generate random bytes")

        return data.base64UrlEncodedString()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun generateCodeChallenge(verifier: String): String {
        val data = (verifier as NSString).dataUsingEncoding(NSUTF8StringEncoding)!!

        // SHA256 Hash erstellen
        val digestLength = CC_SHA256_DIGEST_LENGTH
        val hashData = NSMutableData.dataWithLength(digestLength.toULong())!!

        data.bytes?.let { inputBytes ->
            CC_SHA256(inputBytes, data.length.toUInt(), hashData.mutableBytes?.reinterpret())
        }

        return hashData.base64UrlEncodedString()
    }

    // Helper Extension für URL-Safe Base64 ohne Padding
    private fun NSData.base64UrlEncodedString(): String {
        val base64 = this.base64EncodedStringWithOptions(0.toULong())
        return base64
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }
}