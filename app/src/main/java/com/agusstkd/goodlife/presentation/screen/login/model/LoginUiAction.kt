package com.agusstkd.goodlife.presentation.screen.login.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.core.biometric.BiometricResult

/**
 * Acciones de UI para la pantalla de Login.
 *
 * Define todas las interacciones posibles del usuario:
 * - Cambios en campos de texto
 * - Clicks en botones
 * - Navegación
 */
@Stable
sealed interface LoginUiAction {

    /**
     * Usuario cambió el valor del campo de email/usuario.
     *
     * @property value Nuevo valor del campo
     */
    data class OnEmailChange(val value: String) : LoginUiAction

    /**
     * Usuario cambió el valor del campo de contraseña.
     *
     * @property value Nuevo valor del campo
     */
    data class OnPasswordChange(val value: String) : LoginUiAction

    /**
     * Usuario tocó el checkbox de "Recordar usuario".
     */
    data object OnRememberUserToggle : LoginUiAction

    /**
     * Usuario tocó el botón "Iniciar Sesión".
     */
    data object OnLoginClick : LoginUiAction

    /**
     * Usuario tocó el botón "Crear Cuenta".
     */
    data object OnRegisterClick : LoginUiAction

    /**
     * Usuario tocó el link "¿Olvidaste tu contraseña?".
     */
    data object OnForgotPasswordClick : LoginUiAction

    /**
     * Usuario tocó el botón de login con Google.
     */
    data object OnGoogleLoginClick : LoginUiAction

    /**
     * Usuario tocó el botón de login con Apple.
     */
    data object OnAppleLoginClick : LoginUiAction

    /**
     * Usuario tocó el link de "Términos".
     */
    data object OnTermsClick : LoginUiAction

    /**
     * Usuario tocó el link de "Privacidad".
     */
    data object OnPrivacyClick : LoginUiAction

    // ═══════ BIOMETRÍA ═══════
    /** Toggle del checkbox de activar biometría */
    data object OnBiometricToggle : LoginUiAction

    /** Click en el ícono de huella (abre prompt) */
    data object OnBiometricIconClick : LoginUiAction

    /** Resultado de la autenticación biométrica */
    data class OnBiometricResult(val result: BiometricResult) : LoginUiAction

    /** Resetear flag de mostrar prompt */
    data object OnBiometricPromptShown : LoginUiAction
}
