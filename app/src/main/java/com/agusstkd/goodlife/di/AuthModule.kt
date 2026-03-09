package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.remote.api.auth.AuthApiService
import com.agusstkd.goodlife.data.remote.datasource.AuthRemoteDataSource
import com.agusstkd.goodlife.data.repository.AuthRepositoryImpl
import com.agusstkd.goodlife.domain.auth.BiometricLoginHandler
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase
import com.agusstkd.goodlife.domain.usecase.session.CheckSessionUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase
import com.agusstkd.goodlife.presentation.screen.login.LoginViewModel
import com.agusstkd.goodlife.presentation.screen.main.MainScaffoldViewModel
import com.agusstkd.goodlife.presentation.screen.register.RegisterViewModel
import com.agusstkd.goodlife.presentation.screen.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature de autenticación.
 *
 * Agrupa todas las dependencias de Auth siguiendo el mismo patrón que [dailyModule]:
 * DataSource → Repository → UseCases → ViewModels.
 *
 * Fue extraído de `AppModule` para que:
 * - Un error de wiring en Auth no afecte el startup del resto de la app.
 * - Cada feature nueva siga el mismo patrón sin crecer `appModule`.
 * - Las dependencias de Auth sean visibles y trazables en un solo lugar.
 *
 * ## Dependencias externas (resueltas por otros módulos)
 * - [networkModule]: [AuthApiService], [TokenManager]
 * - [databaseModule]: UserDao
 * - [biometricModule]: BiometricAuthenticator, SecureCredentialsStorage
 * - [coreModule]: [AppLanguage], [DispatcherProvider], [ComposeNavigationController]
 */
val authModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    factory {
        AuthRemoteDataSource(apiService = get<AuthApiService>())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Singleton porque maneja el estado del usuario autenticado actualmente.
     */
    single<AuthRepository> {
        AuthRepositoryImpl(
            remoteDataSource = get(),
            tokenManager = get(),
            userDao = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // USE CASES — Validación
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    factory { ValidateEmailUseCase(language = get()) }
    factory { ValidatePasswordUseCase(language = get()) }
    factory { ValidateFullNameUseCase(language = get()) }
    factory { ValidateUserNameUseCase(language = get()) }
    factory { ValidatePasswordMatchUseCase(language = get()) }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // USE CASES — Negocio
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    factory {
        LoginUseCase(
            validateEmail = get(),
            validatePassword = get(),
            authRepository = get(),
            dispatcher = get(),
            language = get()
        )
    }

    factory {
        RegisterUseCase(
            authRepository = get(),
            validateUserName = get(),
            validateFullName = get(),
            validateEmail = get(),
            validatePassword = get(),
            validatePasswordMatch = get(),
            dispatcher = get(),
            language = get()
        )
    }

    factory { CheckSessionUseCase(tokenManager = get()) }
    factory { GetCurrentUserUseCase(authRepository = get()) }
    factory { LogoutUseCase(authRepository = get()) }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // HANDLERS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    factory {
        BiometricLoginHandler(
            biometricAuthenticator = get(),
            credentialsStorage = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    viewModel {
        SplashViewModel(
            navigationController = get(),
            checkSessionUseCase = get(),
            language = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            navigationController = get(),
            biometricLoginHandler = get(),
            language = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUseCase = get(),
            navigationController = get(),
            language = get()
        )
    }

    viewModel {
        MainScaffoldViewModel(
            navigationController = get(),
            dateProvider = get(),
            language = get(),
        )
    }
}
