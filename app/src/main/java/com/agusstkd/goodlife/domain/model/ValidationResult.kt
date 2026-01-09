package com.agusstkd.goodlife.domain.model

/**
 * Resultado de una validación.
 *
 * Encapsula si una validación fue exitosa o no, junto con un mensaje de error opcional.
 *
 * @property isValid Indica si la validación pasó
 * @property errorMessage Mensaje de error si la validación falló (null si fue exitosa)
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        /**
         * Resultado de validación exitosa.
         */
        fun success() = ValidationResult(isValid = true)

        /**
         * Resultado de validación fallida.
         *
         * @param message Mensaje de error descriptivo
         */
        fun error(message: String) = ValidationResult(
            isValid = false,
            errorMessage = message
        )
    }
}
