package com.agusstkd.goodlife.domain.usecase.routine.result

sealed interface ActivateRoutineResult {
    data object Success : ActivateRoutineResult
    data class ServerError(val message: String) : ActivateRoutineResult
    data object NetworkError : ActivateRoutineResult
}
