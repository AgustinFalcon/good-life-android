package com.agusstkd.goodlife.domain.usecase.task.result

sealed interface CreateTaskResult {
    data object Success : CreateTaskResult
    data class ValidationError(val message: String): CreateTaskResult
    data class ServerError(val message: String) : CreateTaskResult
    data object NetworkError: CreateTaskResult
}
