package com.agusstkd.goodlife.core.result

/**
 * Wrapper genérico para operaciones que pueden fallar.
 *
 * Usado exclusivamente en funciones `suspend` (repositorios → UseCases).
 * Solo tiene dos estados posibles porque una función suspend o devuelve
 * un resultado o lanza una excepción — nunca está "cargando".
 *
 * El estado de carga es responsabilidad de la capa de presentación
 * ([com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState.Loading], etc.),
 * no del resultado de una operación.
 *
 * Es Kotlin puro, preparado para KMP.
 *
 * @param T El tipo de dato contenido en caso de éxito.
 * @see Success
 * @see Error
 */
sealed class Result<out T> {

    /**
     * Representa una operación exitosa.
     *
     * @param data Los datos resultantes de la operación.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Representa una operación fallida.
     *
     * @param exception La excepción que causó el error.
     */
    data class Error(
        val exception: Throwable
    ) : Result<Nothing>() {
        /**
         * Mensaje de error expuesto a las capas superiores.
         * Fuente de verdad: [exception.message].
         */
        val message: String? get() = exception.message
    }

    /** Indica si el resultado es exitoso. */
    val isSuccess: Boolean get() = this is Success

    /** Indica si el resultado es un error. */
    val isError: Boolean get() = this is Error

    /**
     * Obtiene el dato si es [Success], null en caso contrario.
     *
     * @return El dato contenido o null.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Ejecuta [action] si el resultado es [Success].
     *
     * @param action Acción a ejecutar con el dato.
     * @return Este mismo Result para encadenamiento.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Ejecuta [action] si el resultado es [Error].
     *
     * @param action Acción a ejecutar con el error.
     * @return Este mismo Result para encadenamiento.
     */
    inline fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }
}

/**
 * Transforma el dato de [Result.Success] a otro tipo.
 *
 * @param transform Función de transformación.
 * @return Nuevo Result con el dato transformado.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> this
}

/**
 * Ejecuta [block] y envuelve el resultado en [Result].
 * Captura cualquier excepción y la convierte en [Result.Error].
 *
 * @param block Bloque de código a ejecutar.
 * @return [Result.Success] con el resultado o [Result.Error] si falla.
 */
inline fun <T> resultOf(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}

/**
 * Versión suspendida de [resultOf] para funciones suspend.
 *
 * @param block Bloque suspend a ejecutar.
 * @return [Result.Success] con el resultado o [Result.Error] si falla.
 */
suspend inline fun <T> suspendResultOf(crossinline block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
