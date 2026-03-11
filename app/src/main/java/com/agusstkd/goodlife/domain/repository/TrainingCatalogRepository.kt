package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.MuscleGroup

typealias ExercisePage = PageResult<ExerciseMaster>

interface TrainingCatalogRepository {

    suspend fun getMuscleGroups(): Result<List<MuscleGroup>>

    suspend fun searchExercises(
        query: String? = null,
        muscleGroupId: Long? = null,
        page: Int,
        pageSize: Int,
    ): Result<ExercisePage>
}
