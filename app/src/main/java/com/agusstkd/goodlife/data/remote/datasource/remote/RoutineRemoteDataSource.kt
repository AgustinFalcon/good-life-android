package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.executeOptionalApiCall
import com.agusstkd.goodlife.data.remote.api.training.RoutineApiService
import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateRoutineRequest
import com.agusstkd.goodlife.data.remote.dto.response.training.RoutineResponse


class RoutineRemoteDataSource(private val routineApiService: RoutineApiService) {
    suspend fun createRoutine(request: CreateRoutineRequest): Result<RoutineResponse> {
        return executeApiCall { routineApiService.createRoutine(request = request) }
    }

    suspend fun activateRoutine(id: Long): Result<RoutineResponse> {
        return executeApiCall { routineApiService.activateRoutine(id) }
    }

    suspend fun getActiveRoutine(): Result<RoutineResponse?> {
        return executeOptionalApiCall { routineApiService.getActiveRoutine() }
    }
}
