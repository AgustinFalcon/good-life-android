package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.domain.model.ValidationResult

/**
 * Valida el nombre de usuario.
 *
 * ## Reglas de validación:
 * - No puede estar vacío
 * - Mínimo 3 caracteres
 * - Solo letras, números y guiones bajos
 * - Sin espacios
 *
 * @see ValidationResult
 */
class ValidateUserNameUseCase {

    companion object {
        private const val MIN_LENGTH = 3
        private val VALID_PATTERN = Regex("^[a-zA-Z0-9_]+$")
    }

    /**
     * Ejecuta la validación del nombre de usuario.
     *
     * @param username Nombre de usuario a validar
     * @return [ValidationResult] con el resultado de la validación
     */
    operator fun invoke(username: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El nombre de usuario no puede estar vacío"
            )
        }

        if (username.length < MIN_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El nombre de usuario debe tener al menos $MIN_LENGTH caracteres"
            )
        }

        if (!username.matches(VALID_PATTERN)) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Solo letras, números y guiones bajos"
            )
        }

        return ValidationResult(isValid = true)
    }
}
