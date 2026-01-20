package com.agusstkd.goodlife.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import com.agusstkd.goodlife.domain.usecase.login.LoginResult
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.navigation.route.navigateToMain
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login.
 *
 * ## Responsabilidades:
 * - Mantener el estado de UI (LoginUiState)
 * - Procesar las acciones del usuario (LoginUiAction)
 * - Delegar validaciones y login al LoginUseCase
 * - Navegar a otras pantallas via ComposeNavigationController
 *
 * ## Arquitectura Clean:
 * ```
 * LoginViewModel
 *     └── LoginUseCase
 *             ├── ValidateEmailUseCase
 *             ├── ValidatePasswordUseCase
 *             └── AuthRepository
 * ```
 *
 * @property loginUseCase Caso de uso que encapsula la lógica de login
 * @property navigationController Controlador de navegación reactivo
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val navigationController: ComposeNavigationController,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val credentialsStorage: SecureCredentialsStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkBiometricStatus()
    }

    private fun checkBiometricStatus() {
        val isAvailable = biometricAuthenticator.checkAvailability() == BiometricAvailability.Available
        val isEnabled = credentialsStorage.isBiometricEnabled()
        val hasCredentials = credentialsStorage.hasCredentials()
        
        // Si tiene biometría activada y credenciales, cargar el email guardado
        val savedEmail = if (isEnabled && hasCredentials) {
            credentialsStorage.getSavedEmail()
        } else null

        // Si tiene biometría activada y credenciales, mostrar prompt automáticamente
        val shouldShowPrompt = isAvailable && isEnabled && hasCredentials

        _uiState.value = LoginUiState.Content(
            email = savedEmail ?: "",
            isBiometricAvailable = isAvailable,
            isBiometricEnabled = isEnabled && hasCredentials,
            shouldShowBiometricPrompt = shouldShowPrompt
        )
    }

    /**
     * Procesa las acciones del usuario.
     */
    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailChange -> updateEmail(action.value)
            is LoginUiAction.OnPasswordChange -> updatePassword(action.value)
            is LoginUiAction.OnRememberUserToggle -> toggleRememberUser()
            is LoginUiAction.OnLoginClick -> performLogin()
            is LoginUiAction.OnRegisterClick -> navigateToRegister()
            is LoginUiAction.OnForgotPasswordClick -> navigateToForgotPassword()
            is LoginUiAction.OnGoogleLoginClick -> loginWithGoogle()
            is LoginUiAction.OnAppleLoginClick -> loginWithApple()
            is LoginUiAction.OnTermsClick -> navigateToTerms()
            is LoginUiAction.OnPrivacyClick -> navigateToPrivacy()
            is LoginUiAction.OnBiometricToggle -> handleBiometricToggle()
            is LoginUiAction.OnBiometricIconClick -> handleBiometricIconClick()
            is LoginUiAction.OnBiometricResult -> handleBiometricResult(action.result)
            is LoginUiAction.OnBiometricPromptShown -> handleBiometricPromptShown()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // UPDATE STATE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun updateEmail(value: String) {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                email = value,
                isEmailError = false // Limpiar error al escribir
            )
        }
    }

    private fun updatePassword(value: String) {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                password = value,
                isPasswordError = false // Limpiar error al escribir
            )
        }
    }

    private fun toggleRememberUser() {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                rememberUser = !currentState.rememberUser
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // LOGIN ACTION - Usa LoginUseCase
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun performLogin() {
        val currentState = _uiState.value
        if (currentState !is LoginUiState.Content) return

        // Evitar múltiples clicks
        if (currentState.isLoading) return

        viewModelScope.launch {
            // Mostrar loading en el botón (no cambiar toda la pantalla)
            _uiState.value = currentState.copy(isLoading = true)

            // Ejecutar login via UseCase
            val result = loginUseCase(
                email = currentState.email,
                password = currentState.password
            )

            // Procesar resultado
            when (result) {
                is LoginResult.Success -> {
                    // Guardar credenciales ANTES de cambiar el estado (si biometría está activada)
                    if (currentState.isBiometricEnabled) {
                        credentialsStorage.saveCredentials(
                            currentState.email,
                            currentState.password
                        )
                    }
                    _uiState.value = LoginUiState.Success
                    navigationController.navigateToMain()
                }

                is LoginResult.ValidationError -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isEmailError = result.emailError != null,
                        isPasswordError = result.passwordError != null
                    )
                    // TODO: Mostrar mensaje de error específico
                }

                is LoginResult.Error -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isEmailError = true,
                        isPasswordError = true
                    )
                    // TODO: Mostrar mensaje de error general
                }
            }
        }
    }


    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // BIOMETRIC
    // ═══════════════════════════════════════════════════════════════════════════════════════════
    private fun handleBiometricToggle() {
        val current = (_uiState.value as? LoginUiState.Content) ?: return
        val newEnabled = !current.isBiometricEnabled

        // Solo actualizar preferencia, las credenciales se guardan al hacer login exitoso
        credentialsStorage.setBiometricEnabled(newEnabled)

        _uiState.value = current.copy(isBiometricEnabled = newEnabled)
    }

    private fun handleBiometricIconClick() {
        val current = (_uiState.value as? LoginUiState.Content) ?: return

        // Solo mostrar prompt si está habilitado y hay credenciales
        if (current.isBiometricEnabled && credentialsStorage.hasCredentials()) {
            _uiState.value = current.copy(shouldShowBiometricPrompt = true)
        }
    }

    private fun handleBiometricPromptShown() {
        val current = (_uiState.value as? LoginUiState.Content) ?: return
        _uiState.value = current.copy(shouldShowBiometricPrompt = false)
    }

    private fun handleBiometricResult(result: BiometricResult) {
        when (result) {
            is BiometricResult.Success -> {
                // Obtener credenciales y hacer login automáticamente
                val credentials = credentialsStorage.getCredentials()
                if (credentials != null) {
                    performLoginWithCredentials(credentials.first, credentials.second)
                }
            }
            is BiometricResult.Cancelled -> {
                // Usuario canceló → El email ya está autocompletado, solo resetear el flag
                val current = (_uiState.value as? LoginUiState.Content) ?: return
                _uiState.value = current.copy(shouldShowBiometricPrompt = false)
            }
            is BiometricResult.Failed -> {
                // Huella no reconocida - Android maneja reintentos automáticamente
            }
            is BiometricResult.Lockout -> {
                // Bloqueo temporal por muchos intentos fallidos
                val current = (_uiState.value as? LoginUiState.Content) ?: return
                _uiState.value = current.copy(shouldShowBiometricPrompt = false)
                // TODO: Mostrar Snackbar con mensaje de bloqueo
            }
            is BiometricResult.Error -> {
                // Error del sistema
                val current = (_uiState.value as? LoginUiState.Content) ?: return
                _uiState.value = current.copy(shouldShowBiometricPrompt = false)
            }
        }
    }

    private fun performLoginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            val current = (_uiState.value as? LoginUiState.Content) ?: return@launch
            _uiState.value = current.copy(isLoading = true)

            when (val result = loginUseCase(email, password)) {
                is LoginResult.Success -> {
                    _uiState.value = LoginUiState.Success
                    navigationController.navigateToMain()
                }
                is LoginResult.ValidationError -> {
                    _uiState.value = current.copy(
                        isLoading = false,
                        isEmailError = result.emailError != null,
                        isPasswordError = result.passwordError != null
                    )
                }
                is LoginResult.Error -> {
                    _uiState.value = current.copy(
                        isLoading = false,
                        isEmailError = true,
                        isPasswordError = true
                    )
                }
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
        // navigationController.navigateTo(AppRoute.ForgotPassword)
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
