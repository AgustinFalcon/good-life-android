package com.agusstkd.goodlife.data.remote.datasource

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogResponse
import com.agusstkd.goodlife.domain.model.daily.ItemStatus

/**
 * DataSource remoto para operaciones de daily logs.
 *
 * Responsabilidades:
 * - Ejecutar llamadas HTTP al backend
 * - Usar [executeApiCall] para manejo uniforme de errores
 * - Convertir respuestas a Result<T>
 */
class DailyRemoteDataSource(
    private val apiService: GoodLifeApiService
) {

    /**
     * Obtiene el daily log de una fecha específica.
     *
     * @param date Fecha en formato ISO-8601 ("2026-02-05")
     * @return Result con DailyLogResponse en caso de éxito
     */
    suspend fun getDailyLogByDate(date: String): Result<DailyLogResponse> {
        return executeApiCall {
            apiService.getDailyLogByDate(date)
        }
    }

    /**
     * Actualiza el status de un item del daily log.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status del item
     * @return Result con DailyLogResponse actualizado
     */
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLogResponse> {
        return executeApiCall {
            apiService.updateItemStatus(
                itemId = itemId,
                status = status.name  // ItemStatus.COMPLETED → "COMPLETED"
            )
        }
    }
}
