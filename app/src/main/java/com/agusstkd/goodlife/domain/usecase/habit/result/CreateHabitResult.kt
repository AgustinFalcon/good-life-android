package com.agusstkd.goodlife.domain.usecase.habit.result

/**
 * Resultado semántico de la operación de creación de hábito.
 *
 * Sigue el patrón Option B del proyecto: cada variante es un subtipo
 * exhaustivo que el ViewModel puede manejar sin castear excepciones.
 *
 * No incluye Loading — eso es estado de UI, no de resultado.
 *
 * @see com.agusstkd.goodlife.domain.usecase.habit.CreateHabitUseCase
 */
sealed interface CreateHabitResult {

    /** Hábito creado exitosamente. */
    data object Success : CreateHabitResult

    /** Error de validación local (datos inválidos antes de ir a red). */
    data class ValidationError(val message: String) : CreateHabitResult

    /** Error del servidor (4xx/5xx con mensaje del backend). */
    data class ServerError(val message: String) : CreateHabitResult

    /** Error de red (sin conexión, timeout, etc.). */
    data object NetworkError : CreateHabitResult
}
