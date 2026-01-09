package com.agusstkd.goodlife.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Factory para crear instancias de OkHttpClient.
 *
 * Configura el cliente HTTP con:
 * - Timeouts personalizables
 * - Interceptor de autenticación (GoodLifeInterceptor)
 * - Interceptor de logging para debug
 *
 * @see GoodLifeInterceptor
 * @see NetworkConstants
 */
object OkHttpClientFactory {

    /**
     * Crea una instancia configurada de OkHttpClient.
     *
     * @param authInterceptor Interceptor para añadir Bearer token a requests.
     * @param loggingInterceptor Interceptor de logging para debug.
     * @param connectTimeout Timeout de conexión en segundos (default: NetworkConstants).
     * @param readTimeout Timeout de lectura en segundos (default: NetworkConstants).
     * @param writeTimeout Timeout de escritura en segundos (default: NetworkConstants).
     * @return Instancia de OkHttpClient configurada.
     */
    fun create(
        authInterceptor: GoodLifeInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        connectTimeout: Long = NetworkConstants.CONNECT_TIMEOUT,
        readTimeout: Long = NetworkConstants.READ_TIMEOUT,
        writeTimeout: Long = NetworkConstants.WRITE_TIMEOUT
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Crea un interceptor de logging configurado.
     *
     * @param level Nivel de logging (default: BODY para ver request/response completos).
     * @return Instancia de HttpLoggingInterceptor.
     */
    fun createLoggingInterceptor(
        level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            this.level = level
        }
    }
}
