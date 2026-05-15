package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.TrainingCatalogRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.SearchExercisesResult
import kotlinx.coroutines.withContext

class SearchExercisesUseCase(
    private val repository: TrainingCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(
        query: String? = null,
        muscleGroupId: Long? = null,
        page: Int,
        pageSize: Int,
    ): SearchExercisesResult {
        return withContext(dispatcher.io) {
            when (val result = repository.searchExercises(
                query = query,
                muscleGroupId = muscleGroupId,
                page = page,
                pageSize = pageSize,
            )) {
                is Result.Success -> SearchExercisesResult.Success(result.data)
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.ServerException -> SearchExercisesResult.ServerError(ex.message)
                    else -> SearchExercisesResult.NetworkError
                }
            }
        }
    }
}
