package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.CreateRoutineResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class CreateRoutineUseCase(
    private val repository: RoutineRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(
        name: String,
        description: String?,
        difficultyLevel: DifficultyLevel?,
        goalType: GoalType?,
        scheduledTime: LocalTime?,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
        workouts: List<WorkoutDraft>,
    ): CreateRoutineResult {

        if (name.isBlank()) {
            return CreateRoutineResult.ValidationError("El nombre no puede estar vacío")
        }

        if (difficultyLevel == null) {
            return CreateRoutineResult.ValidationError("Debes seleccionar una dificultad")
        }

        if (goalType == null) {
            return CreateRoutineResult.ValidationError("Debes seleccionar un objetivo")
        }

        if (daysOfWeek.isEmpty()) {
            return CreateRoutineResult.ValidationError("Debes seleccionar al menos un día")
        }

        if (workouts.isEmpty()) {
            return CreateRoutineResult.ValidationError("Debes agregar al menos un workout")
        }

        val workoutWithoutExercises = workouts.firstOrNull { it.exercises.isEmpty() }
        if (workoutWithoutExercises != null) {
            return CreateRoutineResult.ValidationError("\"${workoutWithoutExercises.name}\" no tiene ejercicios")
        }

        val exerciseWithoutSets = workouts
            .flatMap { it.exercises }
            .firstOrNull { it.sets.isEmpty() }
        if (exerciseWithoutSets != null) {
            return CreateRoutineResult.ValidationError("\"${exerciseWithoutSets.exerciseName}\" no tiene sets configurados")
        }

        if (endDate != null && startDate != null && endDate < startDate) {
            return CreateRoutineResult.ValidationError("La fecha de fin no puede ser anterior al inicio")
        }

        return withContext(dispatcher.io) {
            val result = repository.createRoutine(
                name = name,
                description = description,
                difficultyLevel = difficultyLevel,
                goalType = goalType,
                scheduledTime = scheduledTime,
                daysOfWeek = daysOfWeek,
                startDate = startDate,
                endDate = endDate,
                workouts = workouts,
            )

            when (result) {
                is Result.Success -> CreateRoutineResult.Success(routineId = result.data.id)
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.BadRequestException -> CreateRoutineResult.ValidationError(ex.message)
                    is ApiException.ServerException -> CreateRoutineResult.ServerError(ex.message)
                    else -> CreateRoutineResult.NetworkError
                }
            }
        }
    }
}
