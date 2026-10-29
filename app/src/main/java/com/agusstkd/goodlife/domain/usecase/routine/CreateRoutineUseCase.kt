package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
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
 * No conoce AppLanguage ni HTTP. Las reglas de validación son strategies ordenadas;
 * agregar un caso es agregar una regla y un [CreateRoutineValidation].
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
        val invalid = rules.firstNotNullOfOrNull { it.check(draft) }
        if (invalid != null) {
            return CreateRoutineResult.Invalid(invalid)
        }

        return withContext(dispatcher.io) {
            val result = repository.createRoutine(
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

            when (result) {
                is Result.Success -> CreateRoutineResult.Success(routineId = result.data.id)
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.BadRequestException ->
                        CreateRoutineResult.Invalid(CreateRoutineValidation.RemoteRejected)
                    is ApiException.ServerException -> CreateRoutineResult.ServerError(ex.message)
                    else -> CreateRoutineResult.NetworkError
                }
            }
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

    private fun interface CreateRoutineRule {
        fun check(draft: CreateRoutineDraft): CreateRoutineValidation?
    }

    private val rules: List<CreateRoutineRule> = listOf(
        CreateRoutineRule { draft ->
            CreateRoutineValidation.EmptyName.takeIf { draft.name.isBlank() }
        },
        CreateRoutineRule { draft ->
            CreateRoutineValidation.MissingDifficulty.takeIf { draft.difficultyLevel == null }
        },
        CreateRoutineRule { draft ->
            CreateRoutineValidation.MissingGoal.takeIf { draft.goalType == null }
        },
        CreateRoutineRule { draft ->
            CreateRoutineValidation.EmptyDays.takeIf { draft.daysOfWeek.isEmpty() }
        },
        CreateRoutineRule { draft ->
            CreateRoutineValidation.EmptyWorkouts.takeIf { draft.workouts.isEmpty() }
        },
        CreateRoutineRule { draft ->
            draft.workouts.firstOrNull { it.exercises.isEmpty() }
                ?.let { CreateRoutineValidation.WorkoutWithoutExercises(it.name) }
        },
        CreateRoutineRule { draft ->
            draft.workouts.flatMap { it.exercises }
                .firstOrNull { it.sets.isEmpty() }
                ?.let { CreateRoutineValidation.ExerciseWithoutSets(it.exerciseName) }
        },
        CreateRoutineRule { draft ->
            val end = draft.endDate
            val start = draft.startDate
            CreateRoutineValidation.EndBeforeStart.takeIf { end != null && start != null && end < start }
        },
    )
}
