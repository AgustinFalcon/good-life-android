package com.agusstkd.goodlife.core.network

/**
 * Constantes de configuración de red.
 *
 * Centraliza URLs y configuraciones de timeout.
 */
object NetworkConstants {

    /**
     * URL base del backend.
     * Desplegado en VM DEV con Nginx + SSL.
     */
    const val BASE_URL = "https://good-life.ddns.net/"

    /**
     * Timeouts de conexión (en segundos)
     */
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    /**
     * Endpoints públicos que NO requieren token.
     * El AuthInterceptor los ignora.
     */
    val PUBLIC_ENDPOINTS = listOf(
        "/api/v1/token",
        "/api/v1/register"
    )
}
