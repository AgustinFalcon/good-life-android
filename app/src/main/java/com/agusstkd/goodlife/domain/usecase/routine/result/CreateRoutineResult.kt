package com.agusstkd.goodlife.domain.usecase.routine.result

sealed interface CreateRoutineResult {
    data class Success(val routineId: Long) : CreateRoutineResult
    data class ValidationError(val message: String) : CreateRoutineResult
    data class ServerError(val message: String) : CreateRoutineResult
    data object NetworkError : CreateRoutineResult
}
