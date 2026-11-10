package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.CreateRoutineResult
import com.agusstkd.goodlife.domain.usecase.routine.result.CreateRoutineValidation
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Crea una rutina: valida el draft, delega al repositorio y mapea [Result] a [CreateRoutineResult].
 *
 * No conoce AppLanguage ni HTTP. Cada [CreateRoutineRule] es una strategy;
 * agregar un caso es una regla nueva y un [CreateRoutineValidation], sin tocar las anteriores.
 */
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
        val draft = CreateRoutineDraft(
            name = name,
            difficultyLevel = difficultyLevel,
            goalType = goalType,
            daysOfWeek = daysOfWeek,
            startDate = startDate,
            endDate = endDate,
            workouts = workouts,
        )
        CreateRoutineRule.ordered.firstNotNullOfOrNull { it.check(draft) }
            ?.let { return CreateRoutineResult.Invalid(it) }

        return withContext(dispatcher.io) {
            toResult(
                repository.createRoutine(
                    name = name,
                    description = description,
                    difficultyLevel = requireNotNull(difficultyLevel),
                    goalType = requireNotNull(goalType),
                    scheduledTime = scheduledTime,
                    daysOfWeek = daysOfWeek,
                    startDate = startDate,
                    endDate = endDate,
                    workouts = workouts,
                )
            )
        }
    }

    private fun toResult(result: Result<Routine>): CreateRoutineResult = when (result) {
        is Result.Success -> CreateRoutineResult.Success(routineId = result.data.id)
        is Result.Error -> when (val ex = result.exception) {
            is ApiException.BadRequestException ->
                CreateRoutineResult.Invalid(CreateRoutineValidation.RemoteRejected)
            is ApiException.ServerException -> CreateRoutineResult.ServerError(ex.message)
            else -> CreateRoutineResult.NetworkError
        }
    }

    private data class CreateRoutineDraft(
        val name: String,
        val difficultyLevel: DifficultyLevel?,
        val goalType: GoalType?,
        val daysOfWeek: Set<DayOfWeek>,
        val startDate: LocalDate?,
        val endDate: LocalDate?,
        val workouts: List<WorkoutDraft>,
    )

    /**
     * Una responsabilidad por regla. El recorrido en [ordered] decide el orden;
     * abrir el flujo a una regla nueva no reescribe las anteriores.
     */
    private sealed interface CreateRoutineRule {
        fun check(draft: CreateRoutineDraft): CreateRoutineValidation?

        data object NamePresent : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                if (draft.name.isBlank()) return CreateRoutineValidation.EmptyName
                return null
            }
        }

        data object DifficultyPresent : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                if (draft.difficultyLevel == null) return CreateRoutineValidation.MissingDifficulty
                return null
            }
        }

        data object GoalPresent : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                if (draft.goalType == null) return CreateRoutineValidation.MissingGoal
                return null
            }
        }

        data object DaysPresent : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                if (draft.daysOfWeek.isEmpty()) return CreateRoutineValidation.EmptyDays
                return null
            }
        }

        data object WorkoutsPresent : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                if (draft.workouts.isEmpty()) return CreateRoutineValidation.EmptyWorkouts
                return null
            }
        }

        data object WorkoutsHaveExercises : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                val workout = draft.workouts.firstOrNull { it.exercises.isEmpty() } ?: return null
                return CreateRoutineValidation.WorkoutWithoutExercises(workout.name)
            }
        }

        data object ExercisesHaveSets : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                val exercise = draft.workouts.flatMap { it.exercises }
                    .firstOrNull { it.sets.isEmpty() }
                    ?: return null
                return CreateRoutineValidation.ExerciseWithoutSets(exercise.exerciseName)
            }
        }

        data object DatesOrdered : CreateRoutineRule {
            override fun check(draft: CreateRoutineDraft): CreateRoutineValidation? {
                val start = draft.startDate ?: return null
                val end = draft.endDate ?: return null
                if (end < start) return CreateRoutineValidation.EndBeforeStart
                return null
            }
        }

        companion object {
            val ordered: List<CreateRoutineRule> = listOf(
                NamePresent,
                DifficultyPresent,
                GoalPresent,
                DaysPresent,
                WorkoutsPresent,
                WorkoutsHaveExercises,
                ExercisesHaveSets,
                DatesOrdered,
            )
        }
    }
}
