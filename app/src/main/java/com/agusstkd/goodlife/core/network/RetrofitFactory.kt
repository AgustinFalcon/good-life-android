package com.agusstkd.goodlife.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Factory para crear instancias de Retrofit.
 *
 * Configura Retrofit con:
 * - Base URL del backend
 * - OkHttpClient personalizado
 * - Kotlinx Serialization como converter
 *
 * @see NetworkConstants.BASE_URL
 */
object RetrofitFactory {

    private const val CONTENT_TYPE = "application/json"

    /**
     * Crea una instancia configurada de Retrofit.
     *
     * @param okHttpClient Cliente HTTP configurado con interceptors.
     * @param json Instancia de Kotlinx Serialization Json.
     * @param baseUrl URL base del backend (default: NetworkConstants.BASE_URL).
     * @return Instancia de Retrofit lista para crear ApiServices.
     */
    fun create(
        okHttpClient: OkHttpClient,
        json: Json,
        baseUrl: String = NetworkConstants.BASE_URL
    ): Retrofit {
        val contentType = CONTENT_TYPE.toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
