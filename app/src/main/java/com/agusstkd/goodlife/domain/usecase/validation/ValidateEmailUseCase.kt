package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.validation.ValidationResult

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
class ValidateEmailUseCase(
    private val language: AppLanguage
) {

    operator fun invoke(value: String): ValidationResult {
        val texts = language.validationTexts

        if (value.isBlank()) {
            return ValidationResult.error(texts.fieldRequired)
        }

        if (value.length < MIN_LENGTH) {
            return ValidationResult.error(
                texts.minLengthFormat.replace("{0}", MIN_LENGTH.toString())
            )
        }

        if (value.contains("@") && !isValidEmailFormat(value)) {
            return ValidationResult.error(texts.invalidEmailFormat)
        }

        return ValidationResult.success()
    }

    private fun isValidEmailFormat(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(email)
    }

    companion object {
        private const val MIN_LENGTH = 3
    }
}
