package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.RealDateProvider
import com.agusstkd.goodlife.core.dispatcher.AndroidDispatcherProvider
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.data.remote.datasource.AuthRemoteDataSource
import com.agusstkd.goodlife.data.repository.AuthRepositoryImpl
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationControllerImpl
import com.agusstkd.goodlife.presentation.screen.home.HomeViewModel
import com.agusstkd.goodlife.presentation.screen.login.LoginViewModel
import com.agusstkd.goodlife.presentation.screen.main.MainScaffoldViewModel
import com.agusstkd.goodlife.presentation.screen.register.RegisterViewModel
import com.agusstkd.goodlife.presentation.screen.splash.SplashViewModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyTabViewModel
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

    /**
     * DateProvider como singleton.
     *
     * Provee fecha/hora con timezone local correcto del dispositivo.
     * Resuelve bug de fechas UTC en emuladores Android.
     *
     * ## Producción:
     * Usa [RealDateProvider] que cachea TimeZone.currentSystemDefault()
     * y calcula fecha local con clock.now().toLocalDateTime(timeZone).date
     *
     * ## Testing:
     * Inyectar [FakeDateProvider] con fecha fija para tests deterministas.
     *
     * ## Uso en ViewModels:
     * ```kotlin
     * class DailyTabViewModel(
     *     private val dateProvider: DateProvider,
     *     private val language: AppLanguage
     * ) : ViewModel() {
     *     val today = dateProvider.today()
     * }
     * ```
     *
     * @see DateProvider Interface abstraída
     * @see RealDateProvider Implementación real
     * @see FakeDateProvider Implementación fake para tests
     */
    single<DateProvider> { RealDateProvider() }

    /**
     * AppLanguage como singleton.
     *
     * Define el idioma de la aplicación para formateo de fechas y textos.
     * Actualmente fijo en Spanish, futuro: desde SharedPreferences.
     *
     * @see AppLanguage Sealed interface con Spanish, English, Portuguese
     */
    single<AppLanguage> { AppLanguage.Spanish }

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
     * ValidateFullNameUseCase - Validación de nombre completo.
     * Factory porque no tiene estado interno.
     */
    factory { ValidateFullNameUseCase() }

    /**
     * ValidateUserNameUseCase - Validación de nombre de usuario.
     * Factory porque no tiene estado interno.
     */
    factory { ValidateUserNameUseCase() }

    /**
     * ValidatePasswordMatchUseCase - Validación de coincidencia de contraseñas.
     * Factory porque no tiene estado interno.
     */
    factory { ValidatePasswordMatchUseCase() }

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

    /**
     * RegisterUseCase - Ejecutar registro con validaciones.
     * Factory porque no tiene estado interno.
     */
    factory {
        RegisterUseCase(
            authRepository = get(),
            validateUserName = get(),
            validateFullName = get(),
            validateEmail = get(),
            validatePassword = get(),
            validatePasswordMatch = get()
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
            navigationController = get(),
            biometricAuthenticator = get(),
            credentialsStorage = get(),
            dispatcherProvider = get()
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

    /**
     * RegisterViewModel - Pantalla de registro.
     */
    viewModel {
        RegisterViewModel(
            registerUseCase = get(),
            navigationController = get(),
            dispatcherProvider = get()
        )
    }

    /**
     * MainScaffoldViewModel - Scaffold principal con tabs.
     */
    viewModel {
        MainScaffoldViewModel(
            dateProvider = get(),
            language = get()
        )
    }

    /**
     * DailyTabViewModel - Tab de tareas diarias.
     */
    viewModel {
        DailyTabViewModel(
            dateProvider = get(),
            language = get()
        )
    }
}
