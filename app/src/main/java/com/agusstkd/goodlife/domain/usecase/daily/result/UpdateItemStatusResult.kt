package com.agusstkd.goodlife.domain.usecase.daily.result

import com.agusstkd.goodlife.domain.model.daily.DailyLog

/**
 * Resultado específico de [UpdateItemStatusUseCase].
 *
 * Reemplaza el genérico [Result<DailyLog>] para expresar exactamente los casos
 * posibles al actualizar el status de un item, sin necesidad de castear a
 * [ApiException] en el ViewModel.
 *
 * ## Casos
 * - [Success]      — el item fue actualizado y el backend devolvió el daily log completo.
 * - [NotFound]     — el item a actualizar no existe (HTTP 404). El ViewModel debe recargar.
 * - [ServerError]  — el backend respondió con un error 5xx.
 * - [NetworkError] — no hubo conexión o la request falló antes de llegar al servidor.
 *
 * ## ¿Por qué el backend devuelve el DailyLog completo?
 * Al actualizar un item, el backend recalcula el [DailyLog.completionRate].
 * Devolver el log completo evita una segunda llamada para refrescar la pantalla.
 *
 * ## Mapping de errores
 * | Result.Error causa               | UpdateItemStatusResult         |
 * |----------------------------------|--------------------------------|
 * | [ApiException.NotFoundException] | [NotFound]                     |
 * | [ApiException.ServerException]   | [ServerError]                  |
 * | IOException / cualquier otro     | [NetworkError]                 |
 */
sealed interface UpdateItemStatusResult {

    /**
     * El item fue actualizado exitosamente.
     *
     * @param dailyLog Daily log completo con el completionRate recalculado.
     */
    data class Success(val dailyLog: DailyLog) : UpdateItemStatusResult

    /**
     * El item a actualizar no existe en el backend (HTTP 404).
     *
     * El estado local puede estar desactualizado — el ViewModel debería recargar el log.
     */
    data object NotFound : UpdateItemStatusResult

    /**
     * El servidor respondió con un error 5xx.
     *
     * @param message Detalle técnico del servidor; no debe cruzar sin sanitizar a `UiState`.
     */
    data class ServerError(val message: String) : UpdateItemStatusResult

    /**
     * La request falló antes de llegar al servidor (sin conexión, timeout, etc.).
     */
    data object NetworkError : UpdateItemStatusResult
}
