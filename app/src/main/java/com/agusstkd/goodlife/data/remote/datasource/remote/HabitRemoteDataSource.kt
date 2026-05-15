package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.habit.HabitApiService
import com.agusstkd.goodlife.data.remote.dto.request.habit.CreateHabitRequest
import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitResponse

/**
 * Data source remoto para operaciones de hábitos.
 *
 * Envuelve las llamadas a [HabitApiService] con [executeApiCall]
 * para convertir respuestas HTTP a [Result].
 *
 * @property habitApiService Interfaz Retrofit de hábitos.
 */
class HabitRemoteDataSource(
    private val habitApiService: HabitApiService,
) {

    /**
     * Crea un hábito en el backend.
     *
     * @param request DTO con los datos del hábito.
     * @return [Result] con la respuesta o el error.
     */
    suspend fun createHabit(request: CreateHabitRequest): Result<HabitResponse> {
        return executeApiCall { habitApiService.createHabit(request) }
    }
}
