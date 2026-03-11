package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.TrainingCatalogRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.GetMuscleGroupsResult
import kotlinx.coroutines.withContext

class GetMuscleGroupsUseCase(
    private val repository: TrainingCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(): GetMuscleGroupsResult {
        return withContext(dispatcher.io) {
            when (val result = repository.getMuscleGroups()) {
                is Result.Success -> GetMuscleGroupsResult.Success(result.data)
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.ServerException -> GetMuscleGroupsResult.ServerError(ex.message)
                    else -> GetMuscleGroupsResult.NetworkError
                }
            }
        }
    }
}
