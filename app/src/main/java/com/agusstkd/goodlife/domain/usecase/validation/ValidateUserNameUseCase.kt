package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.extensions.formatArgs
import com.agusstkd.goodlife.domain.model.validation.ValidationResult

/**
 * Valida el nombre de usuario.
 *
 * ## Reglas:
 * - No puede estar vacío
 * - Mínimo 3 caracteres
 * - Solo letras, números y guiones bajos
 */
class ValidateUserNameUseCase(
    private val language: AppLanguage
) {

    companion object {
        private const val MIN_LENGTH = 3
        private val VALID_PATTERN = Regex("^[a-zA-Z0-9_]+$")
    }

    operator fun invoke(username: String): ValidationResult {
        val texts = language.validationTexts

        if (username.isBlank()) {
            return ValidationResult(isValid = false, errorMessage = texts.usernameRequired)
        }

        if (username.length < MIN_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = texts.usernameMinLengthFormat.formatArgs(MIN_LENGTH)
            )
        }

        if (!username.matches(VALID_PATTERN)) {
            return ValidationResult(isValid = false, errorMessage = texts.usernameInvalidChars)
        }

        return ValidationResult(isValid = true)
    }
}
