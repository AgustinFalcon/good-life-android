package com.agusstkd.goodlife.core.result

/**
 * Wrapper genérico para operaciones que pueden fallar.
 *
 * Proporciona un manejo uniforme de éxito, error y estado de carga
 * en toda la aplicación. Es Kotlin puro, preparado para KMP.
 *
 * @param T El tipo de dato contenido en caso de éxito.
 * @see Success
 * @see Error
 * @see Loading
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
     * @param message Mensaje opcional para mostrar al usuario.
     */
    data class Error(
        val exception: Throwable,
        val message: String? = null
    ) : Result<Nothing>()

    /**
     * Representa una operación en progreso.
     */
    data object Loading : Result<Nothing>()

    /** Indica si el resultado es exitoso. */
    public val isSuccess: Boolean get() = this is Success

    /** Indica si el resultado es un error. */
    val isError: Boolean get() = this is Error

    /** Indica si la operación está en progreso. */
    val isLoading: Boolean get() = this is Loading

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

    /**
     * Ejecuta [action] si el resultado es [Loading].
     *
     * @param action Acción a ejecutar.
     * @return Este mismo Result para encadenamiento.
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
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
    is Result.Error -> this
    is Result.Loading -> this
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
    Result.Error(e, e.message)
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
    Result.Error(e, e.message)
}
