package com.agusstkd.goodlife.data.remote.api.training

import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.training.ExercisePageResponse
import com.agusstkd.goodlife.data.remote.dto.response.training.MuscleGroupResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TrainingCatalogApiService {

    @GET("api/v1/muscle-groups")
    suspend fun getMuscleGroups(): BaseResponse<List<MuscleGroupResponse>>

    @GET("api/v1/exercises")
    suspend fun searchExercises(
        @Query("muscleGroupId") muscleGroupId: Long? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): BaseResponse<ExercisePageResponse>
}
