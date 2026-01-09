package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.domain.model.ValidationResult

/**
 * Caso de uso para validar email/usuario.
 *
 * ## Reutilización:
 * - Login: Validar campo de email/usuario
 * - Register: Validar email del nuevo usuario
 * - Forgot Password: Validar email para recuperación
 * - Edit Profile: Validar nuevo email
 *
 * ## Reglas de validación:
 * 1. No puede estar vacío
 * 2. Mínimo 3 caracteres
 * 3. Si contiene @, debe ser un email válido
 */
class ValidateEmailUseCase {

    /**
     * Valida un email o nombre de usuario.
     *
     * @param value Valor a validar (email o username)
     * @return ValidationResult indicando si es válido o el error
     */
    operator fun invoke(value: String): ValidationResult {
        // Regla 1: No vacío
        if (value.isBlank()) {
            return ValidationResult.error("El campo no puede estar vacío")
        }

        // Regla 2: Mínimo 3 caracteres
        if (value.length < MIN_LENGTH) {
            return ValidationResult.error("Debe tener al menos $MIN_LENGTH caracteres")
        }

        // Regla 3: Si parece email, validar formato
        if (value.contains("@")) {
            if (!isValidEmailFormat(value)) {
                return ValidationResult.error("El formato del email no es válido")
            }
        }

        return ValidationResult.success()
    }

    /**
     * Valida el formato de un email.
     * Patrón simple: texto@texto.texto
     */
    private fun isValidEmailFormat(email: String): Boolean {
        val emailPattern = Regex(
            pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        return emailPattern.matches(email)
    }

    companion object {
        private const val MIN_LENGTH = 3
    }
}
