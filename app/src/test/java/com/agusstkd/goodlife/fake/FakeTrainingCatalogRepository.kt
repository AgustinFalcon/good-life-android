package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.MuscleGroup
import com.agusstkd.goodlife.domain.repository.ExercisePage
import com.agusstkd.goodlife.domain.repository.TrainingCatalogRepository

class FakeTrainingCatalogRepository : TrainingCatalogRepository {

    var getMuscleGroupsResult: Result<List<MuscleGroup>> = Result.Success(DEFAULT_MUSCLE_GROUPS)
    var searchExercisesResult: Result<ExercisePage> = Result.Success(DEFAULT_EXERCISE_PAGE)

    var getMuscleGroupsCallCount = 0
        private set
    var searchExercisesCallCount = 0
        private set

    override suspend fun getMuscleGroups(): Result<List<MuscleGroup>> {
        getMuscleGroupsCallCount++
        return getMuscleGroupsResult
    }

    override suspend fun searchExercises(
        query: String?,
        muscleGroupId: Long?,
        page: Int,
        pageSize: Int,
    ): Result<ExercisePage> {
        searchExercisesCallCount++
        return searchExercisesResult
    }

    companion object {
        val DEFAULT_MUSCLE_GROUPS = listOf(
            MuscleGroup(1, "CHEST", "Pecho", null),
            MuscleGroup(2, "BACK", "Espalda", null),
            MuscleGroup(3, "SHOULDERS", "Hombro", null),
        )

        val DEFAULT_EXERCISES = listOf(
            ExerciseMaster(1, "Bench Press", "Press de banca", null, 1, "Pecho", 8),
            ExerciseMaster(2, "Deadlift", "Peso muerto", null, 2, "Espalda", 9),
            ExerciseMaster(3, "Overhead Press", "Press militar", null, 3, "Hombro", 7),
        )

        val DEFAULT_EXERCISE_PAGE = PageResult(
            items = DEFAULT_EXERCISES,
            page = 0,
            pageSize = 20,
            totalItems = 3L,
            isLastPage = true,
        )
    }
}
