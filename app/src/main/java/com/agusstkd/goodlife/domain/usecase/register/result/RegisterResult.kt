package com.agusstkd.goodlife.domain.usecase.register.result

import com.agusstkd.goodlife.domain.model.auth.User

/**
 * Resultado específico de [com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase].
 *
 * Expresa los tres casos posibles del proceso de registro:
 * validación de formulario, error de servidor, y éxito.
 *
 * ## Casos
 * - [Success]         — el usuario fue registrado exitosamente en el backend.
 * - [ValidationError] — uno o más campos del formulario fallaron la validación local.
 *                       El registro NO llegó al backend.
 * - [ServerError]     — el backend respondió con un error (email duplicado, error 5xx, etc.).
 *
 * ## Uso en ViewModel
 * ```kotlin
 * when (val result = registerUseCase(...)) {
 *     is RegisterResult.Success         -> navigateToLogin()
 *     is RegisterResult.ValidationError -> showFieldError(result.field, result.message)
 *     is RegisterResult.ServerError     -> showErrorBanner(result.message)
 * }
 * ```
 *
 * ## ¿Por qué no hay NetworkError?
 * Los errores de red se mapean a [ServerError] con el mensaje del sistema.
 * Para un formulario de registro, el usuario solo necesita reintentar — no hay
 * diferencia de UX entre un error de red y un error de servidor.
 */
sealed interface RegisterResult {

    /**
     * El usuario fue registrado exitosamente.
     *
     * @param user Modelo de dominio del usuario recién creado.
     */
    data class Success(val user: User) : RegisterResult

    /**
     * Uno o más campos del formulario son inválidos.
     *
     * @param field Campo que falló la validación.
     * @param message Mensaje de error localizado para mostrar debajo del campo.
     */
    data class ValidationError(val field: Field, val message: String) : RegisterResult

    /**
     * El backend respondió con un error, o la red no estuvo disponible.
     *
     * @param message Mensaje de error para mostrar al usuario.
     */
    data class ServerError(val message: String) : RegisterResult

    /**
     * Campos del formulario de registro sujetos a validación.
     */
    enum class Field {
        USERNAME,
        FULL_NAME,
        EMAIL,
        PASSWORD,
        CONFIRM_PASSWORD
    }
}
