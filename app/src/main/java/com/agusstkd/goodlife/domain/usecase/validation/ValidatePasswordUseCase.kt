package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.domain.model.ValidationResult

/**
 * Caso de uso para validar contraseña.
 *
 * ## Reutilización:
 * - Login: Validar contraseña de acceso
 * - Register: Validar nueva contraseña
 * - Change Password: Validar contraseña actual y nueva
 * - Reset Password: Validar nueva contraseña
 *
 * ## Reglas de validación:
 * 1. No puede estar vacía
 * 2. Mínimo 4 caracteres (para desarrollo, en producción sería más)
 * 3. Máximo 50 caracteres
 *
 * ## Reglas adicionales para registro (opcional):
 * - Al menos una mayúscula
 * - Al menos un número
 * - Al menos un carácter especial
 */
class ValidatePasswordUseCase {

    /**
     * Valida una contraseña con reglas básicas.
     * Usar para Login donde solo verificamos que no esté vacía.
     *
     * @param password Contraseña a validar
     * @return ValidationResult indicando si es válida o el error
     */
    operator fun invoke(password: String): ValidationResult {
        return validate(password, strictMode = false)
    }

    /**
     * Valida una contraseña con reglas estrictas.
     * Usar para Register/Change Password donde necesitamos contraseñas seguras.
     *
     * @param password Contraseña a validar
     * @return ValidationResult indicando si es válida o el error
     */
    fun validateStrict(password: String): ValidationResult {
        return validate(password, strictMode = true)
    }

    private fun validate(password: String, strictMode: Boolean): ValidationResult {
        // Regla 1: No vacía
        if (password.isBlank()) {
            return ValidationResult.error("La contraseña no puede estar vacía")
        }

        // Regla 2: Mínimo caracteres
        val minLength = if (strictMode) MIN_LENGTH_STRICT else MIN_LENGTH_BASIC
        if (password.length < minLength) {
            return ValidationResult.error("La contraseña debe tener al menos $minLength caracteres")
        }

        // Regla 3: Máximo caracteres
        if (password.length > MAX_LENGTH) {
            return ValidationResult.error("La contraseña no puede exceder $MAX_LENGTH caracteres")
        }

        // Reglas adicionales solo en modo estricto (para registro)
        if (strictMode) {
            if (!password.any { it.isUpperCase() }) {
                return ValidationResult.error("La contraseña debe contener al menos una mayúscula")
            }

            if (!password.any { it.isDigit() }) {
                return ValidationResult.error("La contraseña debe contener al menos un número")
            }

            if (!password.any { !it.isLetterOrDigit() }) {
                return ValidationResult.error("La contraseña debe contener al menos un carácter especial")
            }
        }

        return ValidationResult.success()
    }

    companion object {
        private const val MIN_LENGTH_BASIC = 4      // Para login (desarrollo)
        private const val MIN_LENGTH_STRICT = 8    // Para registro (producción)
        private const val MAX_LENGTH = 50
    }
}
