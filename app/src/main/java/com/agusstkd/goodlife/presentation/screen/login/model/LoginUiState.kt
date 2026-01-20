package com.agusstkd.goodlife.presentation.screen.login.model

import androidx.compose.runtime.Stable

/**
 * Estado de UI para la pantalla de Login.
 *
 * Define los diferentes estados posibles de la pantalla:
 * - [Loading]: Cargando datos iniciales
 * - [Content]: Formulario de login listo para usar
 * - [Error]: Error general de la pantalla
 * - [Success]: Login exitoso
 */
@Stable
sealed interface LoginUiState {

    /**
     * Estado de carga inicial.
     * Muestra un spinner mientras se prepara la pantalla.
     */
    data object Loading : LoginUiState

    /**
     * Estado principal con el formulario de login.
     *
     * @property email Correo electrónico o usuario ingresado
     * @property password Contraseña ingresada
     * @property rememberUser Si el usuario quiere ser recordado
     * @property isEmailError Si el campo de email tiene error de validación
     * @property isPasswordError Si el campo de contraseña tiene error de validación
     * @property isLoading Si se está procesando el login (muestra spinner en botón)
     * @property isBiometricAvailable Si el dispositivo tiene hardware biométrico disponible
     * @property isBiometricEnabled Si el usuario activó login con biometría
     * @property shouldShowBiometricPrompt Flag para triggear el BiometricPrompt desde el Owner
     */
    data class Content(
        val email: String = "",
        val password: String = "",
        val rememberUser: Boolean = false,
        val isEmailError: Boolean = false,
        val isPasswordError: Boolean = false,
        val isLoading: Boolean = false,
        val isBiometricAvailable: Boolean = false,
        val isBiometricEnabled: Boolean = false,
        val shouldShowBiometricPrompt: Boolean = false
    ) : LoginUiState

    /**
     * Estado de error general.
     * Se muestra cuando hay un error de red o de servidor.
     */
    data object Error : LoginUiState

    /**
     * Estado de éxito.
     * El login fue exitoso, se debe navegar a Home.
     */
    data object Success : LoginUiState
}
