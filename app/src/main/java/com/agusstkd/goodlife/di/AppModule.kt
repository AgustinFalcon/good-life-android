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
import com.agusstkd.goodlife.domain.usecase.session.CheckSessionUseCase
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
     * Detecta el idioma del dispositivo y selecciona el AppLanguage correspondiente.
     * Fallback a English si el idioma no está soportado.
     *
     * TODO: En el futuro, permitir override manual desde SharedPreferences/Settings.
     */
    single<AppLanguage> {
        val locale = java.util.Locale.getDefault().language
        when (locale) {
            "es" -> AppLanguage.Spanish
            "pt" -> AppLanguage.Portuguese
            else -> AppLanguage.English
        }
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
    factory { ValidateEmailUseCase(language = get()) }

    factory { ValidatePasswordUseCase(language = get()) }

    factory { ValidateFullNameUseCase(language = get()) }

    factory { ValidateUserNameUseCase(language = get()) }

    factory { ValidatePasswordMatchUseCase(language = get()) }

    factory {
        LoginUseCase(
            validateEmail = get(),
            validatePassword = get(),
            authRepository = get(),
            language = get()
        )
    }

    factory { CheckSessionUseCase(tokenManager = get()) }

    factory { GetCurrentUserUseCase(authRepository = get()) }

    factory { LogoutUseCase(authRepository = get()) }

    factory {
        RegisterUseCase(
            authRepository = get(),
            validateUserName = get(),
            validateFullName = get(),
            validateEmail = get(),
            validatePassword = get(),
            validatePasswordMatch = get(),
            language = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    viewModel {
        SplashViewModel(
            navigationController = get(),
            checkSessionUseCase = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            navigationController = get(),
            biometricAuthenticator = get(),
            credentialsStorage = get(),
            dispatcherProvider = get(),
            language = get()
        )
    }

    viewModel {
        HomeViewModel(
            getCurrentUserUseCase = get(),
            logoutUseCase = get(),
            navigationController = get(),
            language = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUseCase = get(),
            navigationController = get(),
            dispatcherProvider = get(),
            language = get()
        )
    }

    viewModel {
        MainScaffoldViewModel(
            dateProvider = get(),
            language = get()
        )
    }
}
