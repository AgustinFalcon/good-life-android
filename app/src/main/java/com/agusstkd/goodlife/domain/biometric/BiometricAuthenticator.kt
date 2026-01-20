package com.agusstkd.goodlife.domain.biometric

import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult

/**
 * Interface para autenticación biométrica.
 *
 * Define el contrato para autenticación biométrica de forma agnóstica a la plataforma.
 * Esto permite que el código de dominio y presentación sea KMP-compatible.
 *
 * ## Implementaciones:
 * - Android: [com.agusstkd.goodlife.platform.biometric.AndroidBiometricAuthenticator]
 * - iOS: (futuro) IosBiometricAuthenticator usando LocalAuthentication
 * - Desktop: (futuro) Podría no soportar biometría
 *
 * ## Uso en ViewModel:
 * ```kotlin
 * class LoginViewModel(
 *     private val biometricAuthenticator: BiometricAuthenticator
 * ) {
 *     fun checkBiometric() {
 *         val availability = biometricAuthenticator.checkAvailability()
 *     }
 * }
 * ```
 */
interface BiometricAuthenticator {

    /**
     * Verifica si el dispositivo soporta autenticación biométrica.
     *
     * @return [BiometricAvailability] indicando el estado del hardware biométrico
     */
    fun checkAvailability(): BiometricAvailability

    /**
     * Inicia el proceso de autenticación biométrica.
     *
     * NOTA: En Android, esto requiere ser llamado desde un contexto de UI (Composable/Activity).
     * El Owner/Screen debe manejar la interacción con el prompt.
     *
     * @param config Configuración del prompt (textos, etc.)
     * @param onResult Callback con el resultado de la autenticación
     */
    fun authenticate(
        config: BiometricPromptConfig,
        onResult: (BiometricResult) -> Unit
    )
}

/**
 * Configuración para el prompt de biometría.
 *
 * Los strings deben venir de resources (strings.xml) para soportar i18n.
 *
 * @property title Título del prompt
 * @property subtitle Subtítulo/descripción
 * @property negativeButtonText Texto del botón para cancelar/usar alternativa
 */
data class BiometricPromptConfig(
    val title: String,
    val subtitle: String,
    val negativeButtonText: String
)
