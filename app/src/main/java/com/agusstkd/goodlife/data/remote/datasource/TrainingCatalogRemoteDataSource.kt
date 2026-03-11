package com.agusstkd.goodlife.data.remote.datasource

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.training.TrainingCatalogApiService
import com.agusstkd.goodlife.data.remote.dto.response.training.ExercisePageResponse
import com.agusstkd.goodlife.data.remote.dto.response.training.MuscleGroupResponse


class TrainingCatalogRemoteDataSource(private val trainingCatalogApiService: TrainingCatalogApiService) {

    suspend fun getMuscleGroups(): Result<List<MuscleGroupResponse>> {
        return executeApiCall { trainingCatalogApiService.getMuscleGroups() }
    }


    suspend fun searchExercises(
        query: String? = null,
        muscleGroupId: Long? = null,
        page: Int,
        pageSize: Int,
    ): Result<ExercisePageResponse> {
        return executeApiCall { trainingCatalogApiService.searchExercises(
            search = query,
            muscleGroupId = muscleGroupId,
            page = page,
            size = pageSize
        ) }
    }
}
