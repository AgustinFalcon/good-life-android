package com.agusstkd.goodlife.domain.usecase.routine.result

import com.agusstkd.goodlife.domain.model.training.MuscleGroup

sealed interface GetMuscleGroupsResult {
    data class Success(val muscleGroups: List<MuscleGroup>) : GetMuscleGroupsResult
    data class ServerError(val message: String) : GetMuscleGroupsResult
    data object NetworkError : GetMuscleGroupsResult
}
