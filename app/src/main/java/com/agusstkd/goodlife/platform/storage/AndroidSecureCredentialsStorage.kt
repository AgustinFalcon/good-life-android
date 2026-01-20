package com.agusstkd.goodlife.platform.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage

/**
 * Implementación Android de [SecureCredentialsStorage].
 *
 * Usa EncryptedSharedPreferences para almacenar credenciales de forma segura.
 *
 * ## Seguridad:
 * - MasterKey con AES256-GCM para encriptación de la clave maestra
 * - PrefKeyEncryptionScheme.AES256_SIV para claves
 * - PrefValueEncryptionScheme.AES256_GCM para valores
 *
 * NOTA: Esta clase es específica de Android.
 * El código compartido debe usar la interface [SecureCredentialsStorage].
 *
 * @param context Context de Android
 */
class AndroidSecureCredentialsStorage(
    context: Context
) : SecureCredentialsStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREFS_NAME = "goodlife_secure_credentials"
        private const val KEY_EMAIL = "biometric_email"
        private const val KEY_PASSWORD = "biometric_password"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    override fun saveCredentials(email: String, password: String) {
        prefs.edit {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
        }
    }

    override fun getCredentials(): Pair<String, String>? {
        val email = prefs.getString(KEY_EMAIL, null)
        val password = prefs.getString(KEY_PASSWORD, null)
        return if (email != null && password != null) {
            Pair(email, password)
        } else null
    }

    override fun getSavedEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    override fun hasCredentials(): Boolean {
        return getCredentials() != null
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
        }
    }

    override fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    override fun clearCredentials() {
        prefs.edit { clear() }
    }
}
