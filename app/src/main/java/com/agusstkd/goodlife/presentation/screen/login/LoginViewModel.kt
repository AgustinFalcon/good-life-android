package com.agusstkd.goodlife.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.AuthScreenTexts
import com.agusstkd.goodlife.domain.auth.BiometricLoginHandler
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig
import com.agusstkd.goodlife.domain.usecase.login.LoginResult
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.navigation.route.navigateToMain
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login.
 *
 * ## Responsabilidades
 * - Mantener el estado de UI ([LoginUiState])
 * - Procesar las acciones del usuario ([LoginUiAction])
 * - Delegar login al [LoginUseCase]
 * - Delegar lógica biométrica al [BiometricLoginHandler]
 * - Navegar a otras pantallas via [ComposeNavigationController]
 *
 * ## Sobre el dispatcher
 * Este ViewModel NO recibe ni usa DispatcherProvider.
 * El dispatcher IO es responsabilidad del [LoginUseCase].
 *
 * @property loginUseCase Caso de uso que encapsula validación y login
 * @property navigationController Controlador de navegación reactivo
 * @property biometricLoginHandler State machine de autenticación biométrica
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val navigationController: ComposeNavigationController,
    private val biometricLoginHandler: BiometricLoginHandler,
    private val language: AppLanguage,
) : ViewModel() {

    val screenTexts: AuthScreenTexts
        get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState
        .onSubscription { initializeBiometricState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LoginUiState.Loading)

    init {
        // collectBiometricEvents permanece en init: es un Channel collector que debe
        // correr durante todo el ciclo de vida del ViewModel, no es una carga de datos.
        collectBiometricEvents()
    }

    /**
     * Inicializa el estado biométrico de forma asíncrona.
     * [BiometricLoginHandler.buildInitialState] es suspend porque
     * EncryptedSharedPreferences puede bloquear en la primera lectura.
     */
    private fun initializeBiometricState() {
        viewModelScope.launch {
            val state = biometricLoginHandler.buildInitialState()
            _uiState.value = LoginUiState.Content(
                email = state.savedEmail ?: "",
                isBiometricAvailable = state.isAvailable,
                isBiometricEnabled = state.isEnabled,
                shouldShowBiometricPrompt = state.shouldShowPrompt
            )
        }
    }

    /**
     * Colecta eventos del [BiometricLoginHandler] via Channel (garantiza entrega).
     * Cuando la biometría es exitosa, el handler emite [BiometricLoginHandler.Event.CredentialsReady]
     * y el ViewModel ejecuta el login con esas credenciales.
     */
    private fun collectBiometricEvents() {
        viewModelScope.launch {
            biometricLoginHandler.events.collect { event ->
                when (event) {
                    is BiometricLoginHandler.Event.CredentialsReady ->
                        performLoginWithCredentials(event.email, event.password)
                }
            }
        }
    }

    fun onAction(action: LoginUiAction) {
        if ((_uiState.value as? LoginUiState.Content)?.isLoading == true) return
        when (action) {
            is LoginUiAction.OnEmailChange            -> updateEmail(action.value)
            is LoginUiAction.OnPasswordChange         -> updatePassword(action.value)
            is LoginUiAction.OnRememberUserToggle     -> toggleRememberUser()
            is LoginUiAction.OnLoginClick             -> performLogin()
            is LoginUiAction.OnRegisterClick          -> navigateToRegister()
            is LoginUiAction.OnForgotPasswordClick    -> navigateToForgotPassword()
            is LoginUiAction.OnGoogleLoginClick       -> loginWithGoogle()
            is LoginUiAction.OnAppleLoginClick        -> loginWithApple()
            is LoginUiAction.OnTermsClick             -> navigateToTerms()
            is LoginUiAction.OnPrivacyClick           -> navigateToPrivacy()
            is LoginUiAction.OnBiometricToggle        -> handleBiometricToggle()
            is LoginUiAction.OnBiometricIconClick     -> handleBiometricIconClick()
            is LoginUiAction.OnBiometricAuthenticate  -> authenticateBiometric(action.platformContext)
            is LoginUiAction.OnBiometricPromptShown   -> handleBiometricPromptShown()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // UPDATE STATE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun updateEmail(value: String) {
        val current = _uiState.value as? LoginUiState.Content ?: return
        _uiState.value = current.copy(email = value, isEmailError = false, errorMessage = null)
    }

    private fun updatePassword(value: String) {
        val current = _uiState.value as? LoginUiState.Content ?: return
        _uiState.value = current.copy(password = value, isPasswordError = false, errorMessage = null)
    }

    private fun toggleRememberUser() {
        val current = _uiState.value as? LoginUiState.Content ?: return
        _uiState.value = current.copy(rememberUser = !current.rememberUser)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // LOGIN
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private var latestLoginRequestId: Long = 0L
    private var loginJob: Job? = null

    private fun updateLoginFailure(
        requestId: Long,
        submittedEmail: String,
        submittedPassword: String,
        validationError: LoginResult.ValidationError? = null,
        showGeneralErrorWhenEdited: Boolean = false,
    ) {
        _uiState.update { state ->
            val latest = state as? LoginUiState.Content ?: return@update state
            if (requestId != latestLoginRequestId) return@update latest
            val credentialsAreUnchanged =
                latest.email == submittedEmail && latest.password == submittedPassword

            latest.copy(
                isLoading = false,
                isEmailError = credentialsAreUnchanged && validationError?.emailError != null,
                isPasswordError = credentialsAreUnchanged && validationError?.passwordError != null,
                errorMessage = if (showGeneralErrorWhenEdited || (validationError == null && credentialsAreUnchanged)) {
                    language.errorTexts.loginError
                } else {
                    null
                },
            )
        }
    }

    private fun performLogin() {
        val current = _uiState.value as? LoginUiState.Content ?: return
        if (current.isLoading || loginJob?.isActive == true) return

        loginJob = viewModelScope.launch {
            val requestId = ++latestLoginRequestId
            _uiState.update { state ->
                (state as? LoginUiState.Content)?.copy(isLoading = true, errorMessage = null) ?: state
            }

            when (val result = loginUseCase(email = current.email, password = current.password)) {
                is LoginResult.Success -> {
                    biometricLoginHandler.saveCredentialsIfEnabled(
                        email = current.email,
                        password = current.password,
                        isEnabled = current.isBiometricEnabled,
                    )
                    _uiState.value = LoginUiState.Success
                    navigationController.navigateToMain()
                }
                is LoginResult.ValidationError -> updateLoginFailure(requestId, current.email, current.password, result)
                is LoginResult.Error -> updateLoginFailure(requestId, current.email, current.password)
            }
        }
    }

    private fun performLoginWithCredentials(email: String, password: String) {
        val current = _uiState.value as? LoginUiState.Content ?: return
        if (current.isLoading || loginJob?.isActive == true) return

        loginJob = viewModelScope.launch {
            val requestId = ++latestLoginRequestId
            _uiState.update { state ->
                (state as? LoginUiState.Content)?.copy(isLoading = true, errorMessage = null) ?: state
            }

            when (val result = loginUseCase(email, password)) {
                is LoginResult.Success -> {
                    _uiState.value = LoginUiState.Success
                    navigationController.navigateToMain()
                }
                is LoginResult.ValidationError -> updateLoginFailure(requestId, email, password, result, showGeneralErrorWhenEdited = true)
                is LoginResult.Error -> updateLoginFailure(requestId, email, password, showGeneralErrorWhenEdited = true)
            }
        }
    }
    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // BIOMETRIC — delegado a BiometricLoginHandler
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun handleBiometricToggle() {
        val current = _uiState.value as? LoginUiState.Content ?: return
        val newEnabled = biometricLoginHandler.handleToggle(current.isBiometricEnabled)
        _uiState.value = current.copy(isBiometricEnabled = newEnabled)
    }

    private fun handleBiometricIconClick() {
        val current = _uiState.value as? LoginUiState.Content ?: return
        if (current.isBiometricEnabled && biometricLoginHandler.canShowPrompt()) {
            _uiState.value = current.copy(shouldShowBiometricPrompt = true)
        }
    }

    private fun handleBiometricPromptShown() {
        val current = _uiState.value as? LoginUiState.Content ?: return
        _uiState.value = current.copy(shouldShowBiometricPrompt = false)
    }

    /**
     * Ejecuta la autenticación biométrica a través del handler.
     * El handler orquesta: BiometricPrompt → resultado → emite CredentialsReady si éxito.
     * El callback [onUiResult] solo maneja actualizaciones de estado de UI (cancel, error, lockout).
     */
    private fun authenticateBiometric(platformContext: Any?) {
        val config = BiometricPromptConfig(
            title = language.authTexts.biometricPromptTitle,
            subtitle = language.authTexts.biometricPromptSubtitle,
        )
        biometricLoginHandler.authenticate(config, platformContext) { result ->
            val current = _uiState.value as? LoginUiState.Content ?: return@authenticate
            when (result) {
                is BiometricResult.Cancelled,
                is BiometricResult.Lockout,
                is BiometricResult.Error -> _uiState.value = current.copy(shouldShowBiometricPrompt = false)
                else -> Unit
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun navigateToRegister() {
        navigationController.navigateTo(AppRoute.Register)
    }

    private fun navigateToForgotPassword() {
        // TODO: Implementar navegación a pantalla de recuperar contraseña
    }

    private fun loginWithGoogle() {
        // TODO: Implementar login con Google
    }

    private fun loginWithApple() {
        // TODO: Implementar login con Apple
    }

    private fun navigateToTerms() {
        // TODO: Navegar a términos y condiciones
    }

    private fun navigateToPrivacy() {
        // TODO: Navegar a política de privacidad
    }
}