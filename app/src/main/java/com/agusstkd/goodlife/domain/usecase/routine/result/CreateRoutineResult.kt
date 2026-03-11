package com.agusstkd.goodlife.domain.usecase.routine.result

sealed interface CreateRoutineResult {
    data object Success : CreateRoutineResult
    data class ValidationError(val message: String) : CreateRoutineResult
    data class ServerError(val message: String) : CreateRoutineResult
    data object NetworkError : CreateRoutineResult
}
