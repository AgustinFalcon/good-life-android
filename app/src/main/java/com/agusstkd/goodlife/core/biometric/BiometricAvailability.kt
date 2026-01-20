package com.agusstkd.goodlife.core.biometric


/**
 * Representa la disponibilidad de biometría en el dispositivo.
 */
sealed interface BiometricAvailability {
    /** Biometría disponible y lista para usar */
    data object Available : BiometricAvailability

    /** El dispositivo no tiene hardware biométrico */
    data object NoHardware : BiometricAvailability

    /** Hardware no disponible temporalmente */
    data object HardwareUnavailable : BiometricAvailability

    /** No hay huellas/caras registradas en el dispositivo */
    data object NoBiometricEnrolled : BiometricAvailability

    /** Estado desconocido */
    data object Unknown : BiometricAvailability
}
