package com.agusstkd.goodlife.core.network

import com.agusstkd.goodlife.core.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the bearer token to private requests without logging sensitive request data.
 */
class GoodLifeInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestToSend = if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            originalRequest
        } else {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken.isNullOrEmpty()) {
                originalRequest
            } else {
                originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
                    .build()
            }
        }

        return chain.proceed(requestToSend)
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return NetworkConstants.PUBLIC_ENDPOINTS.any { publicPath -> path.endsWith(publicPath) }
    }

    private companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}