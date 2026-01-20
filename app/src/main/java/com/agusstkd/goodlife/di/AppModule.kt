package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.dispatcher.AndroidDispatcherProvider
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.data.remote.datasource.AuthRemoteDataSource
import com.agusstkd.goodlife.data.repository.AuthRepositoryImpl
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationControllerImpl
import com.agusstkd.goodlife.presentation.screen.home.HomeViewModel
import com.agusstkd.goodlife.presentation.screen.login.LoginViewModel
import com.agusstkd.goodlife.presentation.screen.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo principal de Koin.
 *
 * Define dependencias de la aplicación:
 * - Core: Dispatchers
 * - Navigation: Controller
 * - DataSources: Remote y Local
 * - Repositories: Implementaciones
 * - UseCases: Lógica de negocio
 * - ViewModels: Presentación
 *
 * ## Otros módulos:
 * - [networkModule]: Retrofit, OkHttp, ApiService
 * - [databaseModule]: Room, DAOs
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
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * AuthRemoteDataSource - Llamadas HTTP de autenticación.
     * Factory porque no tiene estado interno.
     */
    factory {
        AuthRemoteDataSource(apiService = get())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * AuthRepository - Repositorio de autenticación.
     * Singleton porque maneja el estado del usuario actual.
     */
    single<AuthRepository> {
        AuthRepositoryImpl(
            remoteDataSource = get(),
            tokenManager = get(),
            userDao = get()
        )
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

    /**
     * GetCurrentUserUseCase - Obtener usuario logueado.
     * Factory porque no tiene estado interno.
     */
    factory {
        GetCurrentUserUseCase(authRepository = get())
    }

    /**
     * LogoutUseCase - Cerrar sesión.
     * Factory porque no tiene estado interno.
     */
    factory {
        LogoutUseCase(authRepository = get())
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
            navigationController = get(),
            biometricAuthenticator = get(),
            credentialsStorage = get()
        )
    }

    /**
     * HomeViewModel - Pantalla principal.
     */
    viewModel {
        HomeViewModel(
            getCurrentUserUseCase = get(),
            logoutUseCase = get(),
            navigationController = get()
        )
    }
}
