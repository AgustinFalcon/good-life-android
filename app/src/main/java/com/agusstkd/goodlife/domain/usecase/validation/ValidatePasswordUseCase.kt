package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.validation.ValidationResult

/**
 * Caso de uso para validar contraseña.
 *
 * ## Reglas de validación:
 * 1. No puede estar vacía
 * 2. Mínimo 4 caracteres (básico) o 8 (estricto)
 * 3. Máximo 50 caracteres
 *
 * ## Reglas adicionales para registro (strictMode):
 * - Al menos una mayúscula
 * - Al menos un número
 * - Al menos un carácter especial
 */
class ValidatePasswordUseCase(
    private val language: AppLanguage
) {

    operator fun invoke(password: String): ValidationResult {
        return validate(password, strictMode = false)
    }

    fun validateStrict(password: String): ValidationResult {
        return validate(password, strictMode = true)
    }

    private fun validate(password: String, strictMode: Boolean): ValidationResult {
        val texts = language.validationTexts

        if (password.isBlank()) {
            return ValidationResult.error(texts.passwordRequired)
        }

        val minLength = if (strictMode) MIN_LENGTH_STRICT else MIN_LENGTH_BASIC
        if (password.length < minLength) {
            return ValidationResult.error(
                texts.passwordMinLengthFormat.replace("{0}", minLength.toString())
            )
        }

        if (password.length > MAX_LENGTH) {
            return ValidationResult.error(
                texts.passwordMaxLengthFormat.replace("{0}", MAX_LENGTH.toString())
            )
        }

        if (strictMode) {
            if (!password.any { it.isUpperCase() }) {
                return ValidationResult.error(texts.passwordNeedsUppercase)
            }
            if (!password.any { it.isDigit() }) {
                return ValidationResult.error(texts.passwordNeedsNumber)
            }
            if (!password.any { !it.isLetterOrDigit() }) {
                return ValidationResult.error(texts.passwordNeedsSpecialChar)
            }
        }

        return ValidationResult.success()
    }

    companion object {
        private const val MIN_LENGTH_BASIC = 4
        private const val MIN_LENGTH_STRICT = 8
        private const val MAX_LENGTH = 50
    }
}
