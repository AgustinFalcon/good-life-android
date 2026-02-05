package com.agusstkd.goodlife.core.network

import android.util.Log
import com.agusstkd.goodlife.core.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor principal de GoodLife.
 *
 * Funcionalidad:
 * - Añade header "Authorization: Bearer {token}" a requests privados
 * - Ignora endpoints públicos (login, register)
 * - Loggea URL, request y response para debugging
 *
 * @param tokenManager Gestor de tokens para obtener el access token
 */
class GoodLifeInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestPath = originalRequest.url.encodedPath
        val requestUrl = originalRequest.url.toString()
        val requestMethod = originalRequest.method
        
        // ═══════════════════════════════════════════════════════════════════
        // LOG REQUEST
        // ═══════════════════════════════════════════════════════════════════
        Log.d(TAG, "═══════════════════════════════════════════════════════════")
        Log.d(TAG, "📤 REQUEST")
        Log.d(TAG, "───────────────────────────────────────────────────────────")
        Log.d(TAG, "🔗 URL: $requestUrl")
        Log.d(TAG, "📋 Method: $requestMethod")
        Log.d(TAG, "📂 Path: $requestPath")
        Log.d(TAG, "🔐 Is Public: ${isPublicEndpoint(requestPath)}")

        // Si es un endpoint público, no añadir token
        val requestToSend = if (isPublicEndpoint(requestPath)) {
            Log.d(TAG, "⚪ No auth required (public endpoint)")
            originalRequest
        } else {
            // Obtener el token actual
            val accessToken = tokenManager.getAccessToken()

            if (accessToken.isNullOrEmpty()) {
                Log.w(TAG, "⚠️ No token available for protected endpoint!")
                originalRequest
            } else {
                Log.d(TAG, "🟢 Token attached (${accessToken.take(20)}...)")
                // Añadir el header de autorización
                originalRequest.newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
                    .build()
            }
        }
        
        // Log headers
        Log.d(TAG, "📝 Headers:")
        requestToSend.headers.forEach { (name, value) ->
            val displayValue = if (name.equals("Authorization", ignoreCase = true)) {
                "${value.take(30)}..." // No mostrar todo el token
            } else {
                value
            }
            Log.d(TAG, "   • $name: $displayValue")
        }

        // ═══════════════════════════════════════════════════════════════════
        // EXECUTE REQUEST
        // ═══════════════════════════════════════════════════════════════════
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(requestToSend)
        val duration = System.currentTimeMillis() - startTime

        // ═══════════════════════════════════════════════════════════════════
        // LOG RESPONSE
        // ═══════════════════════════════════════════════════════════════════
        Log.d(TAG, "───────────────────────────────────────────────────────────")
        Log.d(TAG, "📥 RESPONSE")
        Log.d(TAG, "───────────────────────────────────────────────────────────")
        Log.d(TAG, "🔗 URL: $requestUrl")
        
        val statusEmoji = when {
            response.isSuccessful -> "✅"
            response.code in 400..499 -> "⚠️"
            response.code >= 500 -> "❌"
            else -> "ℹ️"
        }
        Log.d(TAG, "$statusEmoji Code: ${response.code} ${response.message}")
        Log.d(TAG, "⏱️ Duration: ${duration}ms")
        Log.d(TAG, "═══════════════════════════════════════════════════════════")

        return response
    }

    /**
     * Verifica si el endpoint es público (no requiere autenticación).
     */
    private fun isPublicEndpoint(path: String): Boolean {
        return NetworkConstants.PUBLIC_ENDPOINTS.any { publicPath ->
            path.endsWith(publicPath)
        }
    }

    companion object {
        private const val TAG = "GoodLifeAPI"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
