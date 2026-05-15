package com.agusstkd.goodlife.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.agusstkd.goodlife.domain.model.auth.AuthToken

/**
 * Gestor de tokens JWT con almacenamiento cifrado.
 *
 * Usa [EncryptedSharedPreferences] con [MasterKey] respaldada por Android Keystore.
 * - Claves cifradas con AES256-SIV (determinista, necesario para lookup por nombre).
 * - Valores cifrados con AES256-GCM (nonce aleatorio por operación, seguridad semántica).
 * - La MasterKey vive en el chip de seguridad del dispositivo y no puede extraerse.
 *
 * @param context Contexto de la aplicación
 */
class TokenManager(context: Context) {

    // `create()` está deprecado a favor de una API basada en Tink que requiere dependencias
    // adicionales no incluidas en security-crypto:1.1.0-alpha06. Supprimimos el warning
    // explícitamente — la seguridad (AES256-SIV + AES256-GCM + Android Keystore) es idéntica.
    @Suppress("DEPRECATION")
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_ENCRYPTED_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda los tokens de autenticación.
     *
     * @param authToken Tokens a guardar
     */
    fun saveTokens(authToken: AuthToken) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, authToken.accessToken)
            putString(KEY_REFRESH_TOKEN, authToken.refreshToken)
            putLong(KEY_ACCESS_TOKEN_EXPIRES_AT, authToken.accessTokenExpiresAt)
            putLong(KEY_REFRESH_TOKEN_EXPIRES_AT, authToken.refreshTokenExpiresAt ?: 0L)
        }
    }

    /**
     * Obtiene el access token actual.
     *
     * @return Access token o null si no existe
     */
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    /**
     * Obtiene el refresh token actual.
     *
     * @return Refresh token o null si no existe
     */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    /**
     * Obtiene los tokens completos como AuthToken.
     *
     * @return AuthToken o null si no hay tokens guardados
     */
    fun getAuthToken(): AuthToken? {
        val accessToken = getAccessToken() ?: return null
        return AuthToken(
            accessToken = accessToken,
            refreshToken = getRefreshToken(),
            accessTokenExpiresAt = prefs.getLong(KEY_ACCESS_TOKEN_EXPIRES_AT, 0L),
            refreshTokenExpiresAt = prefs.getLong(KEY_REFRESH_TOKEN_EXPIRES_AT, 0L).takeIf { it > 0 }
        )
    }

    /**
     * Verifica si hay un token válido guardado.
     *
     *
     * @return true si existe un access token no expirado o un refresh token válido
     */
    fun hasValidToken(): Boolean {
        return getAuthToken()?.hasValidTokens() == true
    }

    /**
     * Verifica si el access token actual está expirado.
     *
     * @return true si está expirado o no existe
     */
    fun isAccessTokenExpired(): Boolean {
        return getAuthToken()?.isAccessTokenExpired() ?: true
    }

    /**
     * Limpia todos los tokens guardados.
     * Usar al hacer logout.
     */
    fun clearTokens() {
        prefs.edit { clear() }
    }

    /**
     * Actualiza solo el access token (usado después de refresh).
     *
     * @param newAccessToken Nuevo access token
     * @param expiresAt Timestamp de expiración
     */
    fun updateAccessToken(newAccessToken: String, expiresAt: Long) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, newAccessToken)
            putLong(KEY_ACCESS_TOKEN_EXPIRES_AT, expiresAt)
        }
    }

    companion object {
        private const val PREFS_ENCRYPTED_NAME = "goodlife_auth_prefs_encrypted"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ACCESS_TOKEN_EXPIRES_AT = "access_token_expires_at"
        private const val KEY_REFRESH_TOKEN_EXPIRES_AT = "refresh_token_expires_at"
    }
}
