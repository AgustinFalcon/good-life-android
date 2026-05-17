package com.agusstkd.goodlife.domain.usecase.nutrition.result

import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary

/**
 * Resultado semántico del caso de uso [GetMealPlansUseCase].
 *
 * Sigue el mismo patrón que [GetDailyItemsResult] para consistencia arquitectónica.
 * Evita que el ViewModel maneje excepciones directamente.
 */
sealed interface GetMealPlansResult {

    /**
     * Planes de comida cargados correctamente.
     *
     * @property plans Lista de planes del día (nunca vacía — usar [NotFound] para días sin planes).
     */
    data class Success(val plans: List<DailyMealPlanSummary>) : GetMealPlansResult

    /**
     * No hay planes de comida para la fecha consultada (backend devuelve 404 o lista vacía).
     *
     * No es un error — simplemente no existe un plan para ese día.
     */
    data object NotFound : GetMealPlansResult

    /**
     * Error del servidor (5xx, 4xx no semántico).
     *
     * @property message Mensaje de error del servidor.
     */
    data class ServerError(val message: String) : GetMealPlansResult

    /**
     * Error de red (sin conexión, timeout).
     */
    data object NetworkError : GetMealPlansResult
}
