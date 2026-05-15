package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.datasource.remote.TrainingCatalogRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.training.toDomain
import com.agusstkd.goodlife.data.remote.dto.response.toDomain
import com.agusstkd.goodlife.domain.model.training.MuscleGroup
import com.agusstkd.goodlife.domain.repository.ExercisePage
import com.agusstkd.goodlife.domain.repository.TrainingCatalogRepository

class TrainingCatalogRepositoryImpl(
    private val trainingCatalogRemoteDataSource: TrainingCatalogRemoteDataSource,
) : TrainingCatalogRepository {

    override suspend fun getMuscleGroups(): Result<List<MuscleGroup>> {
        return when (val result = trainingCatalogRemoteDataSource.getMuscleGroups()) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> result
        }
    }

    override suspend fun searchExercises(
        query: String?,
        muscleGroupId: Long?,
        page: Int,
        pageSize: Int,
    ): Result<ExercisePage> {
        return when (val result = trainingCatalogRemoteDataSource.searchExercises(
            query = query,
            muscleGroupId = muscleGroupId,
            page = page,
            pageSize = pageSize,
        )) {
            is Result.Success -> Result.Success(result.data.toDomain { it.toDomain() })
            is Result.Error -> result
        }
    }
}
