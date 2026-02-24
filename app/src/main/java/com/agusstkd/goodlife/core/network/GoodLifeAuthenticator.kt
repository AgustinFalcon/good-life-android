package com.agusstkd.goodlife.core.network

import android.util.Log
import com.agusstkd.goodlife.core.session.SessionEventBus
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Authenticator de OkHttp para refresh automático de access token.
 *
 * OkHttp llama a [authenticate] solo cuando el servidor responde 401.
 *
 * ## Problema resuelto: requests concurrentes
 * OkHttp NO serializa automáticamente las llamadas al Authenticator.
 * Si múltiples requests fallan con 401 simultáneamente, cada una dispara
 * su propio authenticate(). Usamos un [Mutex] de coroutines para garantizar
 * que solo UNO refresque el token y los demás reusen el token ya actualizado.
 *
 * ## Por qué Mutex y no @Synchronized
 * @Synchronized bloquea el thread nativo. Como authenticate() usa runBlocking,
 * el thread del refresh (OkHttp interno) podría intentar adquirir el lock del
 * thread bloqueado → deadlock. El Mutex de coroutines suspende la coroutine
 * sin bloquear el thread subyacente, evitando el deadlock.
 *
 * ## Flujo:
 * ```
 * Request → 401 → authenticate()
 *   ├── Es endpoint /token → ya no hay cómo refrescar → logout → null
 *   ├── Header X-Retry → retry post-refresh también falló → logout → null
 *   ├── Token ya fue renovado por otro thread → retry con token nuevo
 *   └── Sin renovar → refresh token → nuevo access token → retry request
 * ```
 *
 * @param tokenManager Gestión de tokens JWT
 * @param apiService Lazy para romper dependencia circular con OkHttpClient/Retrofit
 */
class GoodLifeAuthenticator(
    private val tokenManager: TokenManager,
    private val apiService: Lazy<GoodLifeApiService>
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Guarda 1: si el endpoint que falló ES el de token, no podemos refrescar más.
        // Devolver null aquí evita la recursión infinita:
        // authenticate() → refreshToken() → 401 → authenticate() → ...
        if (response.request.url.encodedPath.endsWith(TOKEN_ENDPOINT)) {
            Log.w(TAG, "⛔ El endpoint de token retornó 401 — forzando logout.")
            tokenManager.clearTokens()
            SessionEventBus.emit(SessionEventBus.SessionEvent.SessionExpired)
            return null
        }

        // Guarda 2: si ya hicimos un retry post-refresh y sigue fallando, logout.
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
            runBlocking {
                // Mutex: solo un thread a la vez ejecuta el refresh.
                // Los demás esperan suspendidos y al adquirir el lock
                // detectan que el token ya fue renovado → retry inmediato.
                refreshMutex.withLock {
                    val currentToken = tokenManager.getAccessToken()
                    val requestToken = response.request.header(HEADER_AUTH)
                        ?.removePrefix(BEARER_PREFIX)

                    // Si el token ya cambió (otro thread lo renovó antes), reusar.
                    if (currentToken != null && currentToken != requestToken) {
                        Log.d(TAG, "♻️ Token ya renovado por otro hilo. Reintentando.")
                        return@withLock response.request.newBuilder()
                            .header(HEADER_AUTH, "$BEARER_PREFIX$currentToken")
                            .build()
                    }

                    val result = apiService.value.refreshToken(
                        grantType = GRANT_TYPE_REFRESH,
                        refreshToken = refreshToken
                    )

                    val newAccessToken = result.data?.accessToken
                    if (newAccessToken == null) {
                        Log.w(TAG, "⚠️ Refresh respondió sin access token (code=${result.code}).")
                        return@withLock null
                    }

                    val now = System.currentTimeMillis()
                    tokenManager.updateAccessToken(newAccessToken, now + ACCESS_TOKEN_DURATION_MS)
                    Log.d(TAG, "✅ Access token renovado correctamente.")

                    response.request.newBuilder()
                        .header(HEADER_AUTH, "$BEARER_PREFIX$newAccessToken")
                        .header(HEADER_RETRY, "true")
                        .build()
                }
            }
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
        private const val TOKEN_ENDPOINT = "/api/v1/token"
    }
}
