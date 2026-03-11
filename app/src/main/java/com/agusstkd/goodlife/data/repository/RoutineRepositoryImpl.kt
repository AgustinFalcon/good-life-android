package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.datasource.RoutineRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateRoutineRequest
import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateWorkoutExerciseRequest
import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateWorkoutRequest
import com.agusstkd.goodlife.data.remote.dto.request.routine.CreateWorkoutSetRequest
import com.agusstkd.goodlife.data.remote.dto.response.training.toDomain
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

class RoutineRepositoryImpl(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
) : RoutineRepository {

    override suspend fun createRoutine(
        name: String,
        description: String?,
        difficultyLevel: DifficultyLevel,
        goalType: GoalType,
        scheduledTime: LocalTime?,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
        workouts: List<WorkoutDraft>,
    ): Result<Routine> {
        val request = CreateRoutineRequest(
            name = name,
            description = description,
            difficultyLevel = difficultyLevel.name,
            goalType = goalType.name,
            scheduledTime = scheduledTime?.toString(),
            daysOfWeek = daysOfWeek.map { it.isoDayNumber }.sorted(),
            startDate = startDate?.toString(),
            endDate = endDate?.toString(),
            workouts = workouts.map { workout ->
                CreateWorkoutRequest(
                    name = workout.name,
                    exercises = workout.exercises.map { exercise ->
                        CreateWorkoutExerciseRequest(
                            exerciseId = exercise.exerciseId,
                            orderIndex = exercise.orderIndex,
                            notes = exercise.notes,
                            sets = exercise.sets.map { set ->
                                CreateWorkoutSetRequest(
                                    setNumber = set.setNumber,
                                    targetReps = set.targetReps,
                                    targetWeight = set.targetWeight,
                                )
                            },
                        )
                    },
                )
            },
        )

        return when (val result = routineRemoteDataSource.createRoutine(request)) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.exception)
        }
    }

    override suspend fun activateRoutine(routineId: Long): Result<Unit> {
        return when (val result = routineRemoteDataSource.activateRoutine(routineId)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
