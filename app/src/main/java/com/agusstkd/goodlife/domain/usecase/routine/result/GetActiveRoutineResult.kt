package com.agusstkd.goodlife.domain.usecase.routine.result

import com.agusstkd.goodlife.domain.model.training.Routine

sealed interface GetActiveRoutineResult {
    data class Success(val routine: Routine) : GetActiveRoutineResult
    data object NotFound : GetActiveRoutineResult
    data class ServerError(val message: String) : GetActiveRoutineResult
    data object NetworkError : GetActiveRoutineResult
}
