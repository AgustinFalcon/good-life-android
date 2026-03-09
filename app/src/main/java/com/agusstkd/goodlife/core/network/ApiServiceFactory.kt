package com.agusstkd.goodlife.core.network

import retrofit2.Retrofit

/**
 * Factory genérica para crear instancias de ApiService con Retrofit.
 *
 * Cada módulo de Koin crea su ApiService directamente con:
 * ```kotlin
 * single<TaskApiService> {
 *     get<Retrofit>().create(TaskApiService::class.java)
 * }
 * ```
 *
 * Esta factory queda disponible como alternativa genérica para
 * casos donde se necesite instanciar un service fuera de Koin.
 *
 * @see com.agusstkd.goodlife.data.remote.api.auth.AuthApiService
 * @see com.agusstkd.goodlife.data.remote.api.daily.DailyApiService
 * @see com.agusstkd.goodlife.data.remote.api.task.TaskApiService
 */
object ApiServiceFactory {

    /**
     * Crea una instancia de cualquier ApiService usando reified generics.
     *
     * @param T Tipo de la interfaz ApiService a crear.
     * @param retrofit Instancia de Retrofit configurada.
     * @return Implementación generada por Retrofit de la interfaz [T].
     */
    inline fun <reified T> create(retrofit: Retrofit): T {
        return retrofit.create(T::class.java)
    }
}
