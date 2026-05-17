package com.agusstkd.goodlife.presentation.screen.tabs.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.usecase.routine.GetActiveRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.result.GetActiveRoutineResult
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel del tab Workouts (rutina y entrenamientos).
 *
 * ## Responsabilidades:
 * - Cargar la rutina activa del usuario usando [GetActiveRoutineUseCase]
 * - Mapear Domain (Routine) → UiState (WorkoutsUiState)
 * - Resolver labels de dificultad y objetivo usando [AppLanguage]
 *
 * ## Flujo de datos:
 * ```
 * loadData()
 *     ↓
 * GetActiveRoutineUseCase()        ← dueño del dispatcher IO
 *     ↓
 * RoutineRepository.getActiveRoutine()
 *     ↓
 * RoutineRemoteDataSource → GET /api/v1/routines/active
 *     ↓
 * GetActiveRoutineResult (subtipo semántico)
 *     ↓
 * WorkoutsUiState.Success | NoRoutine | Error
 * ```
 *
 * ## Sobre el dispatcher:
 * Este ViewModel NO usa DispatcherProvider directamente.
 * El dispatcher es responsabilidad exclusiva del UseCase (decisión arquitectónica #1).
 */
class WorkoutsTabViewModel(
    private val language: AppLanguage,
    private val getActiveRoutineUseCase: GetActiveRoutineUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState> = _uiState
        .onSubscription { loadData() }
        .stateIn(viewModelScope, SharingStarted.Lazily, WorkoutsUiState.Loading)

    fun refresh() {
        loadData()
    }

    fun onAction(action: WorkoutsUiAction) {
        when (action) {
            WorkoutsUiAction.OnRefresh -> loadData()
            WorkoutsUiAction.OnCreateRoutine -> navigateToCreateRoutine()
            is WorkoutsUiAction.OnWorkoutClick -> navigateToWorkoutDetail(action.workoutId)
        }
    }

    /**
     * Carga la rutina activa del backend.
     *
     * Muestra [WorkoutsUiState.Loading] (skeleton) mientras se hace la request.
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading

            when (val result = getActiveRoutineUseCase()) {
                is GetActiveRoutineResult.Success ->
                    _uiState.value = buildSuccessState(result.routine)

                GetActiveRoutineResult.NotFound ->
                    _uiState.value = WorkoutsUiState.NoRoutine

                is GetActiveRoutineResult.ServerError ->
                    _uiState.value = WorkoutsUiState.Error(result.message)

                GetActiveRoutineResult.NetworkError ->
                    _uiState.value = WorkoutsUiState.Error(language.errorTexts.connectionError)
            }
        }
    }

    /**
     * Construye el UiState.Success mapeando [Routine] (Domain) → [WorkoutsUiState.Success] (UI).
     *
     * Resuelve los labels de dificultad y objetivo usando [AppLanguage.createRoutineTexts].
     */
    private fun buildSuccessState(routine: Routine): WorkoutsUiState.Success {
        val texts = language.createRoutineTexts
        return WorkoutsUiState.Success(
            routineName = routine.name,
            routineDescription = routine.description,
            difficultyLabel = resolveDifficultyLabel(routine.difficultyLevel),
            goalLabel = resolveGoalLabel(routine.goalType),
            daysOfWeek = routine.daysOfWeek,
            workouts = routine.workouts.map { workout ->
                WorkoutUiModel(
                    id = workout.id,
                    name = workout.name,
                    dayOfWeek = workout.dayOfWeek,
                    exerciseCount = workout.exercises.size,
                    totalSets = workout.exercises.sumOf { it.sets.size },
                )
            },
        )
    }

    private fun resolveDifficultyLabel(level: DifficultyLevel): String {
        val texts = language.createRoutineTexts
        return when (level) {
            DifficultyLevel.BEGINNER -> texts.difficultyBeginner
            DifficultyLevel.INTERMEDIATE -> texts.difficultyIntermediate
            DifficultyLevel.ADVANCED -> texts.difficultyAdvanced
        }
    }

    private fun resolveGoalLabel(goal: GoalType): String {
        val texts = language.createRoutineTexts
        return when (goal) {
            GoalType.MUSCLE_GAIN -> texts.goalMuscleGain
            GoalType.WEIGHT_LOSS -> texts.goalWeightLoss
            GoalType.STRENGTH -> texts.goalStrength
            GoalType.ENDURANCE -> texts.goalEndurance
            GoalType.FLEXIBILITY -> texts.goalFlexibility
        }
    }

    private fun navigateToCreateRoutine() {
        // TODO: Navegar a CreateRoutineViewModel usando navigationController
    }

    private fun navigateToWorkoutDetail(workoutId: Long) {
        // TODO: Navegar a detalle de workout
    }
}
