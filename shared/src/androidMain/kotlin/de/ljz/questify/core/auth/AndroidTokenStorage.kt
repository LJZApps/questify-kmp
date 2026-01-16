package de.ljz.questify.core.auth

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class AndroidTokenStorage(private val context: Context) : TokenStorage {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token_encrypted")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_encrypted")
        private const val KEYSET_NAME = "master_key"
        private const val PREF_FILE_NAME = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }

    private val aead: Aead by lazy {
        AeadConfig.register()

        val keysetManager = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()

        keysetManager.keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        println("AndroidTokenStorage: saveTokens aufgerufen. AccessToken: ${accessToken.take(10)}..., RefreshToken: ${refreshToken.take(10)}...")
        val encryptedAccess = encrypt(accessToken)
        val encryptedRefresh = encrypt(refreshToken)

        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = encryptedAccess
            prefs[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
        println("AndroidTokenStorage: Tokens verschlüsselt und in DataStore gespeichert.")
    }

    override suspend fun getAccessToken(): String? {
        println("AndroidTokenStorage: getAccessToken aufgerufen")
        val encrypted = context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()
        val token = encrypted?.let { decrypt(it) }
        println("AndroidTokenStorage: AccessToken gefunden: ${token != null}")
        return token
    }

    override suspend fun getRefreshToken(): String? {
        println("AndroidTokenStorage: getRefreshToken aufgerufen")
        val encrypted = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()
        val token = encrypted?.let { decrypt(it) }
        println("AndroidTokenStorage: RefreshToken gefunden: ${token != null}")
        return token
    }

    override suspend fun clearTokens() {
        println("AndroidTokenStorage: clearTokens aufgerufen. Lösche Tokens aus DataStore.")
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }

    private fun encrypt(plainText: String): String {
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = aead.encrypt(bytes, null)
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(encryptedBase64: String): String? {
        return try {
            val bytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val decryptedBytes = aead.decrypt(bytes, null)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            println("AndroidTokenStorage: ERROR bei Decrypt: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}