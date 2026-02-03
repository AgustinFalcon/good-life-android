package com.agusstkd.goodlife.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.agusstkd.goodlife.domain.model.auth.AuthToken

/**
 * Gestor de tokens JWT usando SharedPreferences.
 *
 * Almacena de forma segura los tokens de autenticación.
 * En producción se podría migrar a EncryptedSharedPreferences.
 *
 * @param context Contexto de la aplicación
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
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
        private const val PREFS_NAME = "goodlife_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ACCESS_TOKEN_EXPIRES_AT = "access_token_expires_at"
        private const val KEY_REFRESH_TOKEN_EXPIRES_AT = "refresh_token_expires_at"
    }
}
