package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.network.GoodLifeAuthenticator
import com.agusstkd.goodlife.core.network.GoodLifeInterceptor
import com.agusstkd.goodlife.core.network.JsonSerializerFactory
import com.agusstkd.goodlife.core.network.OkHttpClientFactory
import com.agusstkd.goodlife.core.network.RetrofitFactory
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.remote.api.auth.AuthApiService
import com.agusstkd.goodlife.data.remote.api.daily.DailyApiService
import com.agusstkd.goodlife.data.remote.api.habit.HabitApiService
import com.agusstkd.goodlife.data.remote.api.media.MediaApiService
import com.agusstkd.goodlife.data.remote.api.task.TaskApiService
import com.agusstkd.goodlife.data.remote.api.training.RoutineApiService
import com.agusstkd.goodlife.data.remote.api.training.TrainingCatalogApiService
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
 * - [TokenManager]: Gestión de tokens JWT
 * - [Json]: Serializador Kotlinx
 * - [GoodLifeInterceptor]: Interceptor de autenticación (Bearer token)
 * - [GoodLifeAuthenticator]: Refresh automático en 401
 * - [OkHttpClient]: Cliente HTTP con interceptors
 * - [Retrofit]: Cliente REST
 * - [AuthApiService]: Endpoints de Auth (login, refreshToken, register)
 * - [DailyApiService]: Endpoints de Daily (getDailyLog, updateItemStatus)
 * - [TaskApiService]: Endpoints de Task (createTask)
 *
 * ## Cómo agregar un nuevo service
 * ```kotlin
 * single<WorkoutsApiService> {
 *     get<Retrofit>().create(WorkoutsApiService::class.java)
 * }
 * ```
 * No es necesario tocar nada más — Retrofit reutiliza el mismo [OkHttpClient].
 *
 * @see JsonSerializerFactory
 * @see OkHttpClientFactory
 * @see RetrofitFactory
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

    single<Json> {
        JsonSerializerFactory.create()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // INTERCEPTORS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Interceptor de autenticación.
     * Añade Bearer token a todos los requests privados.
     */
    single<GoodLifeInterceptor> {
        GoodLifeInterceptor(tokenManager = get())
    }

    /**
     * Interceptor de logging (solo para debug).
     */
    single<HttpLoggingInterceptor> {
        OkHttpClientFactory.createLoggingInterceptor()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // AUTHENTICATOR (401 refresh)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * GoodLifeAuthenticator — refresh automático de access token en 401.
     *
     * Usa inject() (Lazy<AuthApiService>) para romper la dependencia circular:
     * OkHttpClient → Authenticator → AuthApiService → Retrofit → OkHttpClient
     */
    single<Authenticator> {
        GoodLifeAuthenticator(
            tokenManager = get(),
            authApiService = inject()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // OKHTTP CLIENT
    // ═══════════════════════════════════════════════════════════════════════════════════════════

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

    single<Retrofit> {
        RetrofitFactory.create(
            okHttpClient = get(),
            json = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // API SERVICES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * AuthApiService — endpoints de autenticación.
     * Usado por [com.agusstkd.goodlife.data.remote.datasource.remote.AuthRemoteDataSource]
     * y por [GoodLifeAuthenticator] (via Lazy para el refresh token).
     */
    single<AuthApiService> {
        get<Retrofit>().create(AuthApiService::class.java)
    }

    /**
     * DailyApiService — endpoints del módulo Daily.
     * Usado por [com.agusstkd.goodlife.data.remote.datasource.remote.DailyRemoteDataSource].
     */
    single<DailyApiService> {
        get<Retrofit>().create(DailyApiService::class.java)
    }

    /**
     * TaskApiService — endpoints del módulo Task.
     * Usado por [com.agusstkd.goodlife.data.remote.datasource.remote.TaskRemoteDataSource].
     */
    single<TaskApiService> {
        get<Retrofit>().create(TaskApiService::class.java)
    }

    single<HabitApiService> {
        get<Retrofit>().create(HabitApiService::class.java)
    }

    single<TrainingCatalogApiService> {
        get<Retrofit>().create(TrainingCatalogApiService::class.java)
    }

    single<RoutineApiService> {
        get<Retrofit>().create(RoutineApiService::class.java)
    }

    single<MediaApiService> {
        get<Retrofit>().create(MediaApiService::class.java)
    }
}
