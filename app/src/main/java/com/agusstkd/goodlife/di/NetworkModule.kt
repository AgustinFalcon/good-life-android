package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.network.ApiServiceFactory
import com.agusstkd.goodlife.core.network.GoodLifeAuthenticator
import com.agusstkd.goodlife.core.network.GoodLifeInterceptor
import com.agusstkd.goodlife.core.network.JsonSerializerFactory
import com.agusstkd.goodlife.core.network.OkHttpClientFactory
import com.agusstkd.goodlife.core.network.RetrofitFactory
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Módulo de Koin para configuración de red.
 *
 * Provee:
 * - TokenManager: Gestión de tokens JWT
 * - Json: Serializador Kotlinx
 * - OkHttpClient: Cliente HTTP con interceptors
 * - Retrofit: Cliente REST
 * - GoodLifeApiService: Interface de API
 *
 * Las factories están en clases separadas para:
 * - Mayor testabilidad
 * - Reutilización
 * - Separación de responsabilidades
 *
 * @see JsonSerializerFactory
 * @see OkHttpClientFactory
 * @see RetrofitFactory
 * @see ApiServiceFactory
 */
val networkModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TOKEN MANAGER
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * TokenManager como singleton.
     * Gestiona tokens JWT en SharedPreferences.
     */
    single<TokenManager> {
        TokenManager(androidContext())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // JSON SERIALIZER
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Configuración de Kotlinx Serialization.
     * Usa JsonSerializerFactory para la creación.
     */
    single<Json> {
        JsonSerializerFactory.create()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // INTERCEPTORS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Interceptor de autenticación.
     * Añade Bearer token a requests privados.
     */
    single<GoodLifeInterceptor> {
        GoodLifeInterceptor(tokenManager = get())
    }

    /**
     * Interceptor de logging (solo para debug).
     * Usa OkHttpClientFactory para la creación.
     */
    single<HttpLoggingInterceptor> {
        OkHttpClientFactory.createLoggingInterceptor()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // AUTHENTICATOR (401 refresh)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Authenticator como singleton (registrado por interfaz para que Koin lo resuelva).
     * Usa inject() (Lazy) para romper dependencia circular:
     * OkHttpClient → Authenticator → ApiService → Retrofit → OkHttpClient
     */
    single<Authenticator> {
        GoodLifeAuthenticator(
            tokenManager = get(),
            apiService = inject()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // OKHTTP CLIENT
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * OkHttpClient configurado con interceptors y authenticator.
     */
    single<OkHttpClient> {
        OkHttpClientFactory.create(
            authInterceptor = get(),
            loggingInterceptor = get(),
            authenticator = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // RETROFIT
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Retrofit configurado.
     * Usa RetrofitFactory para la creación.
     */
    single<Retrofit> {
        RetrofitFactory.create(
            okHttpClient = get(),
            json = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // API SERVICE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * GoodLifeApiService - Interface principal de API.
     * Usa ApiServiceFactory para la creación.
     */
    single<GoodLifeApiService> {
        ApiServiceFactory.createGoodLifeApiService(retrofit = get())
    }
}
