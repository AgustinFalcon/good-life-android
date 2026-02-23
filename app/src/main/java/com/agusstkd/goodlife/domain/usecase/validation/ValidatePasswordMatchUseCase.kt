package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.validation.ValidationResult

/**
 * Valida que dos contraseñas coincidan.
 *
 * Se utiliza en el formulario de registro.
 */
class ValidatePasswordMatchUseCase(
    private val language: AppLanguage
) {

    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        if (password != confirmPassword) {
            return ValidationResult(
                isValid = false,
                errorMessage = language.validationTexts.passwordsDontMatch
            )
        }
        return ValidationResult(isValid = true)
    }
}
