package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.domain.model.validation.ValidationResult

/**
 * Valida que dos contraseñas coincidan.
 *
 * Se utiliza en el formulario de registro para verificar
 * que la contraseña y su confirmación sean iguales.
 *
 * @see ValidationResult
 */
class ValidatePasswordMatchUseCase {

    /**
     * Ejecuta la validación de coincidencia de contraseñas.
     *
     * @param password Contraseña original
     * @param confirmPassword Contraseña de confirmación
     * @return [ValidationResult] con el resultado de la validación
     */
    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        if (password != confirmPassword) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Las contraseñas no coinciden"
            )
        }

        return ValidationResult(isValid = true)
    }
}
