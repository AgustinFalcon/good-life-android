package com.agusstkd.goodlife.domain.usecase.routine.result

import com.agusstkd.goodlife.domain.repository.ExercisePage

sealed interface SearchExercisesResult {
    data class Success(val page: ExercisePage) : SearchExercisesResult
    data class ServerError(val message: String) : SearchExercisesResult
    data object NetworkError : SearchExercisesResult
}
