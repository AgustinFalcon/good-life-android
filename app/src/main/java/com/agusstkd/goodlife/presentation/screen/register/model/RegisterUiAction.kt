package com.agusstkd.goodlife.presentation.screen.register.model

import androidx.compose.runtime.Stable

/**
 * Acciones que el usuario puede realizar en la pantalla de registro.
 *
 * ## Categorías:
 * - **Cambios de campos**: Actualización de inputs
 * - **Toggles**: Alternar estados (visibilidad, términos)
 * - **Acciones principales**: Registrar, ir a login
 * - **Error handling**: Descartar errores
 *
 * @see RegisterUiState
 */
@Stable
sealed interface RegisterUiAction {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // CAMBIOS DE CAMPOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Cambio en el campo de nombre completo.
     * @property value Nuevo valor del nombre
     */
    data class OnFullNameChange(val value: String) : RegisterUiAction

    /**
     * Cambio en el campo de nombre de usuario.
     * @property value Nuevo valor del username
     */
    data class OnUserNameChange(val value: String) : RegisterUiAction

    /**
     * Cambio en el campo de email.
     * @property value Nuevo valor del email
     */
    data class OnEmailChange(val value: String) : RegisterUiAction

    /**
     * Cambio en el campo de contraseña.
     * @property value Nuevo valor de la contraseña
     */
    data class OnPasswordChange(val value: String) : RegisterUiAction

    /**
     * Cambio en el campo de confirmación de contraseña.
     * @property value Nuevo valor de la confirmación
     */
    data class OnConfirmPasswordChange(val value: String) : RegisterUiAction

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TOGGLES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Alternar aceptación de términos y condiciones.
     */
    data object OnTermsToggle : RegisterUiAction

    /**
     * Alternar visibilidad de la contraseña.
     */
    data object OnPasswordVisibilityToggle : RegisterUiAction

    /**
     * Alternar visibilidad de la confirmación de contraseña.
     */
    data object OnConfirmPasswordVisibilityToggle : RegisterUiAction

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ACCIONES PRINCIPALES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Click en el botón de registrar.
     * Trigger para ejecutar el proceso de registro.
     */
    data object OnRegisterClick : RegisterUiAction

    /**
     * Click en el link "¿Ya tienes cuenta?".
     * Navega a la pantalla de login.
     */
    data object OnLoginClick : RegisterUiAction

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ERROR HANDLING
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Descartar el mensaje de error.
     */
    data object OnDismissError : RegisterUiAction
}
