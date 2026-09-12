package com.agusstkd.goodlife.domain.usecase.daily.result

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.daily.DailyLog

/**
 * Resultado específico de [GetDailyItemsUseCase].
 *
 * Reemplaza el genérico [Result<DailyLog>] para expresar exactamente los casos
 * posibles al consultar el daily log, sin necesidad de castear a [ApiException]
 * en el ViewModel.
 *
 * ## Casos
 * - [Success]     — el daily log fue obtenido correctamente.
 * - [NotFound]    — el backend respondió 404 (no hay log para esa fecha, no es un error).
 * - [ServerError] — el backend respondió 5xx.
 * - [NetworkError] — no hubo conexión o la request falló antes de llegar al servidor.
 *
 * ## Uso en ViewModel
 * ```kotlin
 * when (val result = getDailyItemsUseCase(date)) {
 *     is GetDailyItemsResult.Success      -> buildSuccessState(result.dailyLog)
 *     is GetDailyItemsResult.NotFound     -> showEmptyState()
 *     is GetDailyItemsResult.ServerError  -> showServerError()
 *     is GetDailyItemsResult.NetworkError -> showNetworkError()
 * }
 * ```
 *
 * ## ¿Por qué no hay ValidationError?
 * Este UseCase es un query puro — no recibe inputs del usuario a validar.
 * Los únicos fallos posibles son técnicos (red, servidor).
 */
sealed interface GetDailyItemsResult {

    /**
     * El daily log fue obtenido exitosamente.
     *
     * @param dailyLog Modelo de dominio con los items del día.
     */
    data class Success(val dailyLog: DailyLog) : GetDailyItemsResult

    /**
     * No existe daily log para la fecha solicitada (HTTP 404).
     *
     * No es un error técnico — simplemente no hay datos para ese día.
     * El ViewModel debe mostrar un empty state, no un error.
     */
    data object NotFound : GetDailyItemsResult

    /**
     * El servidor respondió con un error 5xx.
     *
     * @param message Detalle técnico del servidor; no debe cruzar sin sanitizar a `UiState`.
     */
    data class ServerError(val message: String) : GetDailyItemsResult

    /**
     * La request falló antes de llegar al servidor (sin conexión, timeout, etc.).
     */
    data object NetworkError : GetDailyItemsResult
}
