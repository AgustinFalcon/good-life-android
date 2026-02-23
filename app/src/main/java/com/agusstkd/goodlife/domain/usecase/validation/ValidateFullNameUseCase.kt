package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.validation.ValidationResult

/**
 * Valida el nombre completo del usuario.
 *
 * ## Reglas:
 * - No puede estar vacío
 * - Mínimo 2 caracteres
 * - Solo letras (incluyendo acentos) y espacios
 */
class ValidateFullNameUseCase(
    private val language: AppLanguage
) {

    companion object {
        private const val MIN_LENGTH = 2
        private val VALID_PATTERN = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
    }

    operator fun invoke(fullName: String): ValidationResult {
        val texts = language.validationTexts

        if (fullName.isBlank()) {
            return ValidationResult(isValid = false, errorMessage = texts.fullNameRequired)
        }

        if (fullName.length < MIN_LENGTH) {
            return ValidationResult(isValid = false, errorMessage = texts.fullNameTooShort)
        }

        if (!fullName.matches(VALID_PATTERN)) {
            return ValidationResult(isValid = false, errorMessage = texts.fullNameInvalidChars)
        }

        return ValidationResult(isValid = true)
    }
}
