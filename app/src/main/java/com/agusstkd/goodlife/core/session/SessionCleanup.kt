package com.agusstkd.goodlife.core.session

/**
 * Borra el estado local de sesión (tokens, biométrica, Room).
 *
 * El Authenticator y el logout explícito hablan con este puerto;
 * la implementación vive en data para no ciclar OkHttp con AuthRepository.
 */
interface SessionCleanup {
    suspend fun clear()
    suspend fun clearBestEffort()

    /**
     * Borra usuario Room y cache Daily, no los tokens.
     * El login lo usa para no mostrar PII de otra cuenta si el expiry falló a medias.
     */
    suspend fun discardLocalAccountBestEffort()
}
