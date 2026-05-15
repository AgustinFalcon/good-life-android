package com.agusstkd.goodlife.presentation.screen.add.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.core.extensions.toggleDay
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseDraft
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.usecase.routine.ActivateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.CreateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.GetMuscleGroupsUseCase
import com.agusstkd.goodlife.domain.usecase.routine.SearchExercisesUseCase
import com.agusstkd.goodlife.domain.usecase.routine.result.ActivateRoutineResult
import com.agusstkd.goodlife.domain.usecase.routine.result.CreateRoutineResult
import com.agusstkd.goodlife.domain.usecase.routine.result.GetMuscleGroupsResult
import com.agusstkd.goodlife.domain.usecase.routine.result.SearchExercisesResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineDatePickerField
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineWizardStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateRoutineViewModel(
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
    private val createRoutineUseCase: CreateRoutineUseCase,
    private val activateRoutineUseCase: ActivateRoutineUseCase,
    private val getMuscleGroupsUseCase: GetMuscleGroupsUseCase,
    private val searchExercisesUseCase: SearchExercisesUseCase,
) : ViewModel() {

    val routineTexts: CreateRoutineTexts get() = language.createRoutineTexts
    val sharedTexts: CreateItemSharedTexts get() = language.createItemSharedTexts
    val accessibilityTexts get() = language.accessibilityTexts
    val datePickerTexts get() = language.datePickerTexts
    val dayNames: List<String> get() = language.dayNamesShort.names

    val difficultyEntries: List<Pair<DifficultyLevel, String>> by lazy {
        val t = routineTexts
        listOf(
            DifficultyLevel.BEGINNER to t.difficultyBeginner,
            DifficultyLevel.INTERMEDIATE to t.difficultyIntermediate,
            DifficultyLevel.ADVANCED to t.difficultyAdvanced,
        )
    }

    val goalEntries: List<Pair<GoalType, String>> by lazy {
        val t = routineTexts
        listOf(
            GoalType.MUSCLE_GAIN to t.goalMuscleGain,
            GoalType.WEIGHT_LOSS to t.goalWeightLoss,
            GoalType.STRENGTH to t.goalStrength,
            GoalType.ENDURANCE to t.goalEndurance,
            GoalType.FLEXIBILITY to t.goalFlexibility,
        )
    }

    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState: StateFlow<CreateRoutineUiState> = _uiState.asStateFlow()

    fun onAction(action: CreateRoutineUiAction) {
        when (action) {
            // ── Wizard nav ──
            CreateRoutineUiAction.OnNextStep -> goToNextStep()
            CreateRoutineUiAction.OnPreviousStep -> goToPreviousStep()
            CreateRoutineUiAction.OnDismiss -> navigationController.navigateUp()

            // ── Paso 1 ──
            is CreateRoutineUiAction.OnNameChange ->
                _uiState.update { it.copy(name = action.name) }

            is CreateRoutineUiAction.OnDescriptionChange ->
                _uiState.update { it.copy(description = action.description) }

            is CreateRoutineUiAction.OnDifficultySelected ->
                _uiState.update { it.copy(difficultyLevel = action.level) }

            is CreateRoutineUiAction.OnGoalTypeSelected ->
                _uiState.update { it.copy(goalType = action.goal) }

            is CreateRoutineUiAction.OnDayToggled ->
                _uiState.update { it.copy(selectedDays = it.selectedDays.toggleDay(action.day)) }

            CreateRoutineUiAction.OnHasTimeToggle ->
                _uiState.update { it.copy(hasTime = !it.hasTime) }

            CreateRoutineUiAction.OnHasEndDateToggle ->
                _uiState.update { it.copy(hasEndDate = !it.hasEndDate) }

            is CreateRoutineUiAction.OnDatePickerOpen ->
                _uiState.update { it.copy(activeDatePickerField = action.field) }

            CreateRoutineUiAction.OnDatePickerDismiss ->
                _uiState.update { it.copy(activeDatePickerField = null) }

            is CreateRoutineUiAction.OnDateSelected -> dateSelected(action.field, action.date)

            CreateRoutineUiAction.OnTimePickerOpen ->
                _uiState.update { it.copy(showTimePicker = true) }

            CreateRoutineUiAction.OnTimePickerDismiss ->
                _uiState.update { it.copy(showTimePicker = false) }

            is CreateRoutineUiAction.OnTimeSelected ->
                _uiState.update { it.copy(scheduledTime = action.time, showTimePicker = false) }

            // ── Paso 2 ──
            CreateRoutineUiAction.OnShowAddWorkoutDialog ->
                _uiState.update { it.copy(showAddWorkoutDialog = true) }

            CreateRoutineUiAction.OnDismissAddWorkoutDialog ->
                _uiState.update { it.copy(showAddWorkoutDialog = false) }

            is CreateRoutineUiAction.OnAddWorkout -> addWorkout(action.name)
            is CreateRoutineUiAction.OnEditWorkout -> editWorkout(action.index)
            is CreateRoutineUiAction.OnDeleteWorkout -> deleteWorkout(action.index)

            // ── Paso 3 ──
            is CreateRoutineUiAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            is CreateRoutineUiAction.OnMuscleGroupFilter -> onMuscleGroupFilter(action.muscleGroupId)
            CreateRoutineUiAction.OnLoadMoreExercises -> loadMoreExercises()
            is CreateRoutineUiAction.OnAddExercise -> addExerciseToWorkout(action.exercise)
            is CreateRoutineUiAction.OnRemoveExercise -> removeExerciseFromWorkout(action.orderIndex)
            is CreateRoutineUiAction.OnOpenSetEditor ->
                _uiState.update { it.copy(showSetEditorForExerciseIndex = action.exerciseOrderIndex) }

            CreateRoutineUiAction.OnDismissSetEditor ->
                _uiState.update { it.copy(showSetEditorForExerciseIndex = null) }

            is CreateRoutineUiAction.OnSaveExerciseSets ->
                saveExerciseSets(action.orderIndex, action.sets, action.notes)

            CreateRoutineUiAction.OnSaveWorkout -> saveWorkoutAndGoBack()

            // ── Paso 4 ──
            is CreateRoutineUiAction.OnActivateToggle ->
                _uiState.update { it.copy(activateOnCreate = action.activate) }

            CreateRoutineUiAction.OnSubmit -> submitRoutine()

            // ── Feedback ──
            CreateRoutineUiAction.OnErrorDismissed ->
                _uiState.update { it.copy(errorMessage = null) }

            CreateRoutineUiAction.OnSuccessAnimationFinished ->
                navigationController.navigateUp()
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Wizard Navigation
    // ═══════════════════════════════════════════════════════════════════

    private fun goToNextStep() {
        val current = _uiState.value.currentStep
        val next = when (current) {
            RoutineWizardStep.ROUTINE_INFO -> RoutineWizardStep.WORKOUTS
            RoutineWizardStep.WORKOUTS -> RoutineWizardStep.SUMMARY
            RoutineWizardStep.WORKOUT_EXERCISES -> return
            RoutineWizardStep.SUMMARY -> return
        }
        _uiState.update { it.copy(currentStep = next) }
    }

    private fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        val prev = when (current) {
            RoutineWizardStep.ROUTINE_INFO -> {
                navigationController.navigateUp()
                return
            }
            RoutineWizardStep.WORKOUTS -> RoutineWizardStep.ROUTINE_INFO
            RoutineWizardStep.WORKOUT_EXERCISES -> RoutineWizardStep.WORKOUTS
            RoutineWizardStep.SUMMARY -> RoutineWizardStep.WORKOUTS
        }
        _uiState.update { it.copy(currentStep = prev, activeWorkoutIndex = -1) }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 1 helpers
    // ═══════════════════════════════════════════════════════════════════

    private fun dateSelected(field: RoutineDatePickerField, date: kotlinx.datetime.LocalDate) {
        val display = language.formats.full.format(date)
        _uiState.update { current ->
            when (field) {
                RoutineDatePickerField.START_DATE -> current.copy(
                    startDate = date,
                    startDateDisplay = display,
                    activeDatePickerField = null,
                )
                RoutineDatePickerField.END_DATE -> current.copy(
                    endDate = date,
                    endDateDisplay = display,
                    activeDatePickerField = null,
                )
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 2: Workouts
    // ═══════════════════════════════════════════════════════════════════

    private fun addWorkout(name: String) {
        if (name.isBlank()) return
        _uiState.update {
            it.copy(
                workouts = it.workouts + WorkoutDraft(name = name),
                showAddWorkoutDialog = false,
            )
        }
    }

    private fun editWorkout(index: Int) {
        _uiState.update {
            it.copy(
                activeWorkoutIndex = index,
                currentStep = RoutineWizardStep.WORKOUT_EXERCISES,
                isLoadingCatalog = true,
            )
        }
        loadMuscleGroupsIfNeeded()
        loadExercises(page = 0, resetList = true)
    }

    private fun deleteWorkout(index: Int) {
        _uiState.update {
            it.copy(workouts = it.workouts.toMutableList().apply { removeAt(index) })
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 3: Ejercicios + Catálogo
    // ═══════════════════════════════════════════════════════════════════

    private fun loadMuscleGroupsIfNeeded() {
        if (_uiState.value.muscleGroups.isNotEmpty()) return
        viewModelScope.launch {
            when (val result = getMuscleGroupsUseCase()) {
                is GetMuscleGroupsResult.Success ->
                    _uiState.update { it.copy(muscleGroups = result.muscleGroups) }
                is GetMuscleGroupsResult.ServerError -> { /* silently fail, catalog still works */ }
                GetMuscleGroupsResult.NetworkError -> { /* silently fail */ }
            }
        }
    }

    private fun loadExercises(page: Int = 0, resetList: Boolean = false) {
        viewModelScope.launch {
            val snapshot = _uiState.value
            _uiState.update { it.copy(isLoadingCatalog = true) }

            val result = searchExercisesUseCase(
                query = snapshot.exerciseSearchQuery.takeIf { it.isNotBlank() },
                muscleGroupId = snapshot.selectedMuscleGroupId,
                page = page,
                pageSize = 20,
            )

            when (result) {
                is SearchExercisesResult.Success -> _uiState.update { current ->
                    val base = if (resetList) emptyList() else current.exerciseCatalog
                    current.copy(
                        exerciseCatalog = (base + result.page.items).distinctBy { it.id },
                        catalogPage = page,
                        catalogHasMore = !result.page.isLastPage,
                        isLoadingCatalog = false,
                    )
                }
                is SearchExercisesResult.ServerError ->
                    _uiState.update { it.copy(isLoadingCatalog = false, errorMessage = result.message) }
                SearchExercisesResult.NetworkError ->
                    _uiState.update { it.copy(isLoadingCatalog = false, errorMessage = language.errorTexts.connectionError) }
            }
        }
    }

    private fun loadMoreExercises() {
        val state = _uiState.value
        if (state.isLoadingCatalog || !state.catalogHasMore) return
        loadExercises(page = state.catalogPage + 1)
    }

    private fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(exerciseSearchQuery = query) }
        loadExercises(page = 0, resetList = true)
    }

    private fun onMuscleGroupFilter(muscleGroupId: Long?) {
        _uiState.update { it.copy(selectedMuscleGroupId = muscleGroupId) }
        loadExercises(page = 0, resetList = true)
    }

    private fun addExerciseToWorkout(exercise: ExerciseMaster) {
        _uiState.update { state ->
            val idx = state.activeWorkoutIndex
            if (idx < 0) return@update state
            val workout = state.workouts[idx]
            val alreadyAdded = workout.exercises.any { it.exerciseId == exercise.id }
            if (alreadyAdded) return@update state

            val newExercise = ExerciseDraft(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                imageUrl = exercise.imageUrl,
                muscleGroupName = exercise.muscleGroupName,
                orderIndex = workout.exercises.size + 1,
            )
            val updatedWorkout = workout.copy(exercises = workout.exercises + newExercise)
            state.copy(workouts = state.workouts.toMutableList().apply { set(idx, updatedWorkout) })
        }
    }

    private fun removeExerciseFromWorkout(orderIndex: Int) {
        _uiState.update { state ->
            val idx = state.activeWorkoutIndex
            if (idx < 0) return@update state
            val workout = state.workouts[idx]
            val filteredExercises = workout.exercises
                .filter { it.orderIndex != orderIndex }
                .mapIndexed { i, ex -> ex.copy(orderIndex = i + 1) }
            val updatedWorkout = workout.copy(exercises = filteredExercises)
            state.copy(workouts = state.workouts.toMutableList().apply { set(idx, updatedWorkout) })
        }
    }

    private fun saveExerciseSets(orderIndex: Int, sets: List<SetDraft>, notes: String?) {
        _uiState.update { state ->
            val idx = state.activeWorkoutIndex
            if (idx < 0) return@update state
            val workout = state.workouts[idx]
            val updatedExercises = workout.exercises.map { ex ->
                if (ex.orderIndex == orderIndex) ex.copy(sets = sets, notes = notes)
                else ex
            }
            val updatedWorkout = workout.copy(exercises = updatedExercises)
            state.copy(
                workouts = state.workouts.toMutableList().apply { set(idx, updatedWorkout) },
                showSetEditorForExerciseIndex = null,
            )
        }
    }

    private fun saveWorkoutAndGoBack() {
        _uiState.update {
            it.copy(
                currentStep = RoutineWizardStep.WORKOUTS,
                activeWorkoutIndex = -1,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 4: Submit
    // ═══════════════════════════════════════════════════════════════════

    private fun submitRoutine() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = createRoutineUseCase(
                name = state.name,
                description = state.description.takeIf { it.isNotBlank() },
                difficultyLevel = state.difficultyLevel,
                goalType = state.goalType,
                scheduledTime = if (state.hasTime) state.scheduledTime else null,
                daysOfWeek = state.selectedDays,
                startDate = state.startDate,
                endDate = if (state.hasEndDate) state.endDate else null,
                workouts = state.workouts,
            )

            when (result) {
                is CreateRoutineResult.Success -> {
                    if (state.activateOnCreate) {
                        // Activar la rutina recién creada
                        val activateResult = activateRoutineUseCase(result.routineId)
                        when (activateResult) {
                            is ActivateRoutineResult.Success -> {
                                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                            }
                            is ActivateRoutineResult.ServerError -> {
                                _uiState.update { it.copy(isLoading = false, errorMessage = activateResult.message) }
                            }
                            ActivateRoutineResult.NetworkError -> {
                                _uiState.update {
                                    it.copy(isLoading = false, errorMessage = language.errorTexts.connectionError)
                                }
                            }
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                }
                is CreateRoutineResult.ValidationError -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is CreateRoutineResult.ServerError -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                CreateRoutineResult.NetworkError -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = language.errorTexts.connectionError)
                    }
                }
            }
        }
    }
}
