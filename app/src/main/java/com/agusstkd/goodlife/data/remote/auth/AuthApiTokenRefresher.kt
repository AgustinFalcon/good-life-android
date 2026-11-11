package com.agusstkd.goodlife.data.remote.auth

import com.agusstkd.goodlife.core.network.TokenRefresher
import com.agusstkd.goodlife.data.remote.api.auth.AuthApiService

/**
 * Refresh de access token vía [AuthApiService].
 *
 * El Authenticator recibe este puerto, no el service Retrofit.
 */
class AuthApiTokenRefresher(
    private val authApiService: AuthApiService,
) : TokenRefresher {

    override suspend fun refreshAccessToken(refreshToken: String): String? {
        val result = authApiService.refreshToken(
            grantType = GRANT_TYPE_REFRESH,
            refreshToken = refreshToken,
        )
        return result.data?.accessToken
    }

    private companion object {
        const val GRANT_TYPE_REFRESH = "refreshToken"
    }
}
