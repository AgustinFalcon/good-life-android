package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.domain.model.ValidationResult

/**
 * Valida el nombre completo del usuario.
 *
 * ## Reglas de validación:
 * - No puede estar vacío
 * - Mínimo 2 caracteres
 * - Solo letras (incluyendo acentos) y espacios
 *
 * @see ValidationResult
 */
class ValidateFullNameUseCase {

    companion object {
        private const val MIN_LENGTH = 2
        private val VALID_PATTERN = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
    }

    /**
     * Ejecuta la validación del nombre completo.
     *
     * @param fullName Nombre completo a validar
     * @return [ValidationResult] con el resultado de la validación
     */
    operator fun invoke(fullName: String): ValidationResult {
        if (fullName.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El nombre no puede estar vacío"
            )
        }

        if (fullName.length < MIN_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El nombre es muy corto"
            )
        }

        if (!fullName.matches(VALID_PATTERN)) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El nombre solo puede contener letras"
            )
        }

        return ValidationResult(isValid = true)
    }
}
