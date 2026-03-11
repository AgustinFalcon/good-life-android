package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.ActivateRoutineResult
import kotlinx.coroutines.withContext

class ActivateRoutineUseCase(
    private val repository: RoutineRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(routineId: Long): ActivateRoutineResult {
        return withContext(dispatcher.io) {
            when (val result = repository.activateRoutine(routineId)) {
                is Result.Success -> ActivateRoutineResult.Success
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.ServerException -> ActivateRoutineResult.ServerError(ex.message)
                    else -> ActivateRoutineResult.NetworkError
                }
            }
        }
    }
}
