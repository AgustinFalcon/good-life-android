package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.RealDateProvider
import com.agusstkd.goodlife.core.dispatcher.AndroidDispatcherProvider
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.English
import com.agusstkd.goodlife.core.datetime.language.Portuguese
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationControllerImpl
import java.util.Locale
import org.koin.dsl.module

/**
 * Módulo Core de Koin.
 *
 * Provee las dependencias transversales a toda la app:
 * - [DispatcherProvider]: Dispatchers de coroutines
 * - [ComposeNavigationController]: Navegación reactiva
 * - [DateProvider]: Fechas locales con timezone correcto
 * - [AppLanguage]: Sistema de internacionalización KMP-ready
 *
 * Todos los demás módulos (authModule, dailyModule, etc.) dependen de este.
 *
 * ## Otros módulos
 * - [networkModule]: Retrofit, OkHttp, ApiServices, TokenManager
 * - [databaseModule]: Room, DAOs
 * - [biometricModule]: BiometricAuthenticator, SecureCredentialsStorage
 * - [authModule]: Auth DataSource, Repository, UseCases, ViewModels
 * - [dailyModule]: Daily DataSource, Repository, UseCases, ViewModel
 */
val coreModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DISPATCHERS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Singleton: provee dispatchers de coroutines para toda la app.
     * En tests se reemplaza por TestDispatcherProvider con dispatchers síncronos.
     */
    single<DispatcherProvider> {
        AndroidDispatcherProvider()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Singleton: mantiene el estado de navegación global.
     */
    single<ComposeNavigationController> {
        ComposeNavigationControllerImpl()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATE PROVIDER
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Provee fecha/hora con timezone local correcto del dispositivo.
     * En tests inyectar FakeDateProvider con fecha fija para determinismo.
     */
    single<DateProvider> { RealDateProvider() }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // APP LANGUAGE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Selecciona la implementación de [AppLanguage] según el locale del dispositivo.
     *
     * | Código BCP-47 | Implementación  |
     * |---------------|-----------------|
     * | `es`          | [Spanish]       |
     * | `pt`          | [Portuguese]    |
     * | cualquier otro| [English]       |
     */
    single<AppLanguage> {
        when (Locale.getDefault().language) {
            "es" -> Spanish
            "pt" -> Portuguese
            else -> English
        }
    }
}
