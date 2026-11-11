package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.training.WorkoutLogApiService
import com.agusstkd.goodlife.data.remote.dto.request.workout.FinishWorkoutRequest

/**
 * DataSource remoto del log de entrenamiento.
 *
 * Ejecuta [WorkoutLogApiService] y envuelve la respuesta en [Result] con [executeApiCall].
 * Recibe DTO, no draft de dominio.
 *
 * @property workoutLogApiService Retrofit de `POST /api/v1/workouts/log/finish`.
 */
class WorkoutLogRemoteDataSource(private val workoutLogApiService: WorkoutLogApiService) {


    suspend fun finishWorkout(request: FinishWorkoutRequest): Result<Long> {
        return executeApiCall {
            workoutLogApiService.finishWorkout(request = request)
        }
    }

}