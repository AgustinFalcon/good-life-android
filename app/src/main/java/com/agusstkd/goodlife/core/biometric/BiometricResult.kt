package com.agusstkd.goodlife.core.biometric


/**
 * Resultado de la autenticación biométrica.
 */
sealed interface BiometricResult {
    /** Autenticación exitosa */
    data object Success : BiometricResult

    /** Huella no reconocida (puede reintentar) */
    data object Failed : BiometricResult

    /** Usuario canceló el prompt */
    data object Cancelled : BiometricResult

    /** Demasiados intentos fallidos */
    data class Lockout(val message: String) : BiometricResult

    /** Error del sistema */
    data class Error(val code: Int, val message: String) : BiometricResult
}
