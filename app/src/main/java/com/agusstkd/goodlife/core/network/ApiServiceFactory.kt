package com.agusstkd.goodlife.core.network

import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import retrofit2.Retrofit

/**
 * Factory para crear instancias de ApiService.
 *
 * Centraliza la creación de interfaces de API usando Retrofit.
 *
 * @see GoodLifeApiService
 */
object ApiServiceFactory {

    /**
     * Crea una instancia de GoodLifeApiService.
     *
     * @param retrofit Instancia de Retrofit configurada.
     * @return Instancia de GoodLifeApiService para hacer llamadas HTTP.
     */
    fun createGoodLifeApiService(retrofit: Retrofit): GoodLifeApiService {
        return retrofit.create(GoodLifeApiService::class.java)
    }
}
