package com.agusstkd.goodlife.data.remote.api.training

import com.agusstkd.goodlife.data.remote.dto.request.workout.FinishWorkoutRequest
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Endpoints del workout log.
 *
 * Separado de [RoutineApiService] (ISP). Bearer token lo pone
 * [com.agusstkd.goodlife.core.network.GoodLifeInterceptor].
 */
interface WorkoutLogApiService {
    @POST("api/v1/workouts/log/finish")
    suspend fun finishWorkout(@Body request: FinishWorkoutRequest): BaseResponse<Long>
}
