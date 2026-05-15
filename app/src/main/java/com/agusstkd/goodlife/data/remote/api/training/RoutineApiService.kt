package com.agusstkd.goodlife.data.remote.api.training

import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateRoutineRequest
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.training.RoutineResponse
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RoutineApiService {

    @POST("api/v1/routines")
    suspend fun createRoutine(
        @Body request: CreateRoutineRequest,
    ): BaseResponse<RoutineResponse>

    @PATCH("api/v1/routines/{id}/activate")
    suspend fun activateRoutine(
        @Path("id") routineId: Long,
    ): BaseResponse<RoutineResponse>
}
