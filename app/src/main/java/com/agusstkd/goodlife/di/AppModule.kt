package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.dispatcher.AndroidDispatcherProvider
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationControllerImpl
import com.agusstkd.goodlife.presentation.screen.login.LoginViewModel
import com.agusstkd.goodlife.presentation.screen.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo principal de Koin.
 *
 * Define todas las dependencias de la aplicación.
 * Se inicializa en [GoodLifeApp].
 */
val appModule = module {

    // ===== CORE =====

    /**
     * DispatcherProvider como singleton.
     * Provee los dispatchers de coroutines para toda la app.
     */
    single<DispatcherProvider> {
        AndroidDispatcherProvider()
    }

    // ===== NAVIGATION =====

    /**
     * ComposeNavigationController como singleton.
     * Debe ser singleton para mantener el estado de navegación global.
     */
    single<ComposeNavigationController> {
        ComposeNavigationControllerImpl()
    }

    // ===== VIEWMODELS =====

    /**
     * SplashViewModel - Pantalla de carga inicial.
     */
    viewModel { SplashViewModel(navigationController = get()) }

    /**
     * LoginViewModel - Pantalla de login.
     */
    viewModel { LoginViewModel(navigationController = get()) }
}
