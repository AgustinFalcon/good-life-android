package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.dispatcher.AndroidDispatcherProvider
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.data.repository.AuthRepositoryImpl
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.LoginUseCase
import com.agusstkd.goodlife.domain.usecase.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.ValidatePasswordUseCase
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
 *
 * ## Estructura de dependencias:
 * ```
 * ViewModel
 *     └── UseCase
 *             └── Repository (interface)
 *                     └── RepositoryImpl (implementation)
 * ```
 */
val appModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // CORE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DispatcherProvider como singleton.
     * Provee los dispatchers de coroutines para toda la app.
     */
    single<DispatcherProvider> {
        AndroidDispatcherProvider()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * ComposeNavigationController como singleton.
     * Debe ser singleton para mantener el estado de navegación global.
     */
    single<ComposeNavigationController> {
        ComposeNavigationControllerImpl()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * AuthRepository - Repositorio de autenticación.
     * Singleton porque maneja el estado del usuario actual.
     */
    single<AuthRepository> {
        AuthRepositoryImpl()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // USE CASES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * ValidateEmailUseCase - Validación de email/usuario.
     * Factory porque no tiene estado interno.
     */
    factory { ValidateEmailUseCase() }

    /**
     * ValidatePasswordUseCase - Validación de contraseña.
     * Factory porque no tiene estado interno.
     */
    factory { ValidatePasswordUseCase() }

    /**
     * LoginUseCase - Ejecutar login con validaciones.
     * Factory porque no tiene estado interno.
     */
    factory {
        LoginUseCase(
            validateEmail = get(),
            validatePassword = get(),
            authRepository = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * SplashViewModel - Pantalla de carga inicial.
     */
    viewModel {
        SplashViewModel(navigationController = get())
    }

    /**
     * LoginViewModel - Pantalla de login.
     */
    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            navigationController = get()
        )
    }
}
