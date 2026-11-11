package com.agusstkd.goodlife.core.network

/**
 * Puerto de refresh de access token.
 *
 * Vive en core para que [GoodLifeAuthenticator] no importe Retrofit ni `AuthApiService`.
 * La implementación está en data.
 */
fun interface TokenRefresher {

    /**
     * Pide un access token nuevo con el refresh token persistido.
     *
     * @param refreshToken Refresh token vigente.
     * @return Access token nuevo, o `null` si el backend no lo envió.
     */
    suspend fun refreshAccessToken(refreshToken: String): String?
}
