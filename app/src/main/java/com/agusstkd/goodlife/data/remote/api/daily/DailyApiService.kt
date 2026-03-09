package com.agusstkd.goodlife.data.remote.api.daily

import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoints del módulo Daily.
 *
 * Separado de otros services para que:
 * - [com.agusstkd.goodlife.data.remote.datasource.DailyRemoteDataSource] inyecte
 *   solo los endpoints que usa.
 * - Un cambio en Auth o Meals no afecte la compilación de Daily.
 */
interface DailyApiService {

    /**
     * Obtiene el daily log de una fecha específica.
     *
     * Endpoint: GET /api/v1/daily-logs/{date}
     * Formato de fecha: ISO-8601 (YYYY-MM-DD), ej: "2026-02-05"
     *
     * @param date Fecha en formato ISO-8601 ("2026-02-05")
     * @return [BaseResponse] con [DailyLogResponse] (log + items)
     */
    @GET("api/v1/daily-logs/{date}")
    suspend fun getDailyLogByDate(
        @Path("date") date: String
    ): BaseResponse<DailyLogResponse>

    /**
     * Actualiza el status de un item del daily log.
     *
     * Endpoint: PATCH /api/v1/daily-logs/items/{itemId}/status
     *
     * El backend recalcula automáticamente el completionRate del daily log
     * y devuelve el log completo actualizado.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status del item (COMPLETED, SKIPPED, PENDING)
     * @return [BaseResponse] con [DailyLogResponse] actualizado
     */
    @PATCH("api/v1/daily-logs/items/{itemId}/status")
    suspend fun updateItemStatus(
        @Path("itemId") itemId: Long,
        @Query("status") status: String
    ): BaseResponse<DailyLogResponse>
}
