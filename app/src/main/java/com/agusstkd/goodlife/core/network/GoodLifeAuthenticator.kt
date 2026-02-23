package com.agusstkd.goodlife.core.network

import android.util.Log
import com.agusstkd.goodlife.core.session.SessionEventBus
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Authenticator de OkHttp para refresh automático de access token.
 *
 * OkHttp llama a [authenticate] solo cuando el servidor responde 401.
 * A diferencia de un Interceptor, OkHttp serializa internamente las
 * llamadas al Authenticator — si múltiples requests fallan con 401
 * simultáneamente, solo uno ejecuta el refresh y los demás esperan.
 *
 * ## Flujo:
 * ```
 * Request → 401 → authenticate()
 *   ├── Header X-Retry → ya se intentó refresh → limpiar sesión → null
 *   └── Sin header → refresh token → nuevo access token → retry request
 * ```
 *
 * @param tokenManager Gestión de tokens JWT
 * @param apiService Lazy para romper dependencia circular con OkHttpClient/Retrofit
 */
class GoodLifeAuthenticator(
    private val tokenManager: TokenManager,
    private val apiService: Lazy<GoodLifeApiService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header(HEADER_RETRY) != null) {
            Log.w(TAG, "⛔ Retry tras refresh también falló — forzando logout.")
            tokenManager.clearTokens()
            SessionEventBus.emit(SessionEventBus.SessionEvent.SessionExpired)
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Log.w(TAG, "⚠️ No hay refresh token disponible.")
            return null
        }

        return try {
            val newRequest = runBlocking {
                val result = apiService.value.refreshToken(
                    grantType = GRANT_TYPE_REFRESH,
                    refreshToken = refreshToken
                )

                val newAccessToken = result.data?.accessToken
                if (newAccessToken == null) {
                    Log.w(TAG, "⚠️ Refresh respondió sin access token (code=${result.code}).")
                    return@runBlocking null
                }

                val now = System.currentTimeMillis()
                tokenManager.updateAccessToken(newAccessToken, now + ACCESS_TOKEN_DURATION_MS)
                Log.d(TAG, "✅ Access token renovado correctamente.")

                response.request.newBuilder()
                    .header(HEADER_AUTH, "$BEARER_PREFIX$newAccessToken")
                    .header(HEADER_RETRY, "true")
                    .build()
            }

            newRequest
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error durante refresh token.", e)
            null
        }
    }

    companion object {
        private const val TAG = "GoodLifeAuth"
        private const val HEADER_AUTH = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val HEADER_RETRY = "X-Retry-After-Refresh"
        private const val GRANT_TYPE_REFRESH = "refreshToken"
        private const val ACCESS_TOKEN_DURATION_MS = 30 * 60 * 1000L
    }
}
