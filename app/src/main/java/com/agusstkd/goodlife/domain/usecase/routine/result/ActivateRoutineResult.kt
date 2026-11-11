package com.agusstkd.goodlife.domain.usecase.routine.result

sealed interface ActivateRoutineResult {
    data object Success : ActivateRoutineResult
    data object ServerError : ActivateRoutineResult
    data object NetworkError : ActivateRoutineResult
}
