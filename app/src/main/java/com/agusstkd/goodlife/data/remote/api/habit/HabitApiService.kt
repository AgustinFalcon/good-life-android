package com.agusstkd.goodlife.data.remote.api.habit

import com.agusstkd.goodlife.data.remote.dto.request.habit.CreateHabitRequest
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Endpoints del módulo Habit.
 *
 * FASE 1: Solo creación de hábito.
 * FASE 2 (futuro): GET listar, PATCH actualizar, PATCH activar/desactivar.
 *
 * @see com.agusstkd.goodlife.data.remote.datasource.remote.HabitRemoteDataSource
 */
interface HabitApiService {

    /**
     * Crea un nuevo hábito recurrente.
     *
     * @param request Datos del hábito a crear.
     * @return Respuesta envuelta en [BaseResponse] con el hábito creado.
     */
    @POST("api/v1/habits")
    suspend fun createHabit(
        @Body request: CreateHabitRequest,
    ): BaseResponse<HabitResponse>
}
