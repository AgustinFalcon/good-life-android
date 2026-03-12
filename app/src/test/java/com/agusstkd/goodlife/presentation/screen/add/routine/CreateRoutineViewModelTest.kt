package com.agusstkd.goodlife.presentation.screen.add.routine

import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.usecase.routine.ActivateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.CreateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.GetMuscleGroupsUseCase
import com.agusstkd.goodlife.domain.usecase.routine.SearchExercisesUseCase
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import com.agusstkd.goodlife.fake.FakeTrainingCatalogRepository
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineDatePickerField
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineWizardStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRoutineViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var routineRepository: FakeRoutineRepository
    private lateinit var catalogRepository: FakeTrainingCatalogRepository
    private lateinit var navigationController: FakeNavigationController
    private lateinit var viewModel: CreateRoutineViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        routineRepository = FakeRoutineRepository()
        catalogRepository = FakeTrainingCatalogRepository()
        navigationController = FakeNavigationController()

        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        viewModel = CreateRoutineViewModel(
            navigationController = navigationController,
            language = Spanish,
            createRoutineUseCase = CreateRoutineUseCase(routineRepository, dispatcherProvider),
            activateRoutineUseCase = ActivateRoutineUseCase(routineRepository, dispatcherProvider),
            getMuscleGroupsUseCase = GetMuscleGroupsUseCase(catalogRepository, dispatcherProvider),
            searchExercisesUseCase = SearchExercisesUseCase(catalogRepository, dispatcherProvider),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ═══════════════════════════════════════════════════════════════════
    // Estado inicial
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `initial state starts at ROUTINE_INFO step with empty fields`() {
        val state = viewModel.uiState.value
        assertEquals(RoutineWizardStep.ROUTINE_INFO, state.currentStep)
        assertEquals("", state.name)
        assertEquals("", state.description)
        assertNull(state.difficultyLevel)
        assertNull(state.goalType)
        assertTrue(state.selectedDays.isEmpty())
        assertTrue(state.workouts.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.errorMessage)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 1: acciones de campo
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnNameChange updates name`() {
        viewModel.onAction(CreateRoutineUiAction.OnNameChange("Push Pull Legs"))
        assertEquals("Push Pull Legs", viewModel.uiState.value.name)
    }

    @Test
    fun `OnDescriptionChange updates description`() {
        viewModel.onAction(CreateRoutineUiAction.OnDescriptionChange("Hipertrofia"))
        assertEquals("Hipertrofia", viewModel.uiState.value.description)
    }

    @Test
    fun `OnDifficultySelected updates difficultyLevel`() {
        viewModel.onAction(CreateRoutineUiAction.OnDifficultySelected(DifficultyLevel.ADVANCED))
        assertEquals(DifficultyLevel.ADVANCED, viewModel.uiState.value.difficultyLevel)
    }

    @Test
    fun `OnGoalTypeSelected updates goalType`() {
        viewModel.onAction(CreateRoutineUiAction.OnGoalTypeSelected(GoalType.STRENGTH))
        assertEquals(GoalType.STRENGTH, viewModel.uiState.value.goalType)
    }

    @Test
    fun `OnDayToggled adds and removes day`() {
        viewModel.onAction(CreateRoutineUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertTrue(DayOfWeek.MONDAY in viewModel.uiState.value.selectedDays)

        viewModel.onAction(CreateRoutineUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertFalse(DayOfWeek.MONDAY in viewModel.uiState.value.selectedDays)
    }

    @Test
    fun `OnHasTimeToggle toggles hasTime`() {
        assertFalse(viewModel.uiState.value.hasTime)
        viewModel.onAction(CreateRoutineUiAction.OnHasTimeToggle)
        assertTrue(viewModel.uiState.value.hasTime)
    }

    @Test
    fun `OnHasEndDateToggle toggles hasEndDate`() {
        assertFalse(viewModel.uiState.value.hasEndDate)
        viewModel.onAction(CreateRoutineUiAction.OnHasEndDateToggle)
        assertTrue(viewModel.uiState.value.hasEndDate)
    }

    @Test
    fun `OnTimeSelected updates scheduledTime and hides picker`() {
        viewModel.onAction(CreateRoutineUiAction.OnTimePickerOpen)
        assertTrue(viewModel.uiState.value.showTimePicker)

        viewModel.onAction(CreateRoutineUiAction.OnTimeSelected(LocalTime(8, 0)))
        assertEquals(LocalTime(8, 0), viewModel.uiState.value.scheduledTime)
        assertFalse(viewModel.uiState.value.showTimePicker)
    }

    @Test
    fun `OnDateSelected updates start date and display`() {
        viewModel.onAction(
            CreateRoutineUiAction.OnDateSelected(
                RoutineDatePickerField.START_DATE,
                LocalDate(2026, 3, 10),
            )
        )
        assertEquals(LocalDate(2026, 3, 10), viewModel.uiState.value.startDate)
        assertTrue(viewModel.uiState.value.startDateDisplay.isNotEmpty())
        assertNull(viewModel.uiState.value.activeDatePickerField)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Wizard navigation
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnNextStep advances from ROUTINE_INFO to WORKOUTS when valid`() {
        fillStep1()
        viewModel.onAction(CreateRoutineUiAction.OnNextStep)
        assertEquals(RoutineWizardStep.WORKOUTS, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `OnPreviousStep from WORKOUTS goes back to ROUTINE_INFO`() {
        fillStep1()
        viewModel.onAction(CreateRoutineUiAction.OnNextStep)
        viewModel.onAction(CreateRoutineUiAction.OnPreviousStep)
        assertEquals(RoutineWizardStep.ROUTINE_INFO, viewModel.uiState.value.currentStep)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 2: Workouts
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnAddWorkout adds workout to list`() {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        assertEquals(1, viewModel.uiState.value.workouts.size)
        assertEquals("Push Day", viewModel.uiState.value.workouts[0].name)
    }

    @Test
    fun `OnAddWorkout with blank name does nothing`() {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("   "))
        assertTrue(viewModel.uiState.value.workouts.isEmpty())
    }

    @Test
    fun `OnDeleteWorkout removes workout from list`() {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Pull Day"))
        assertEquals(2, viewModel.uiState.value.workouts.size)

        viewModel.onAction(CreateRoutineUiAction.OnDeleteWorkout(0))
        assertEquals(1, viewModel.uiState.value.workouts.size)
        assertEquals("Pull Day", viewModel.uiState.value.workouts[0].name)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 3: Ejercicios
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnEditWorkout sets activeWorkoutIndex and moves to WORKOUT_EXERCISES`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.activeWorkoutIndex)
        assertEquals(RoutineWizardStep.WORKOUT_EXERCISES, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `OnAddExercise adds exercise to active workout`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        val exercise = ExerciseMaster(1, "Bench Press", null, null, 1, "Pecho", 8)
        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(exercise))

        val workout = viewModel.uiState.value.workouts[0]
        assertEquals(1, workout.exercises.size)
        assertEquals("Bench Press", workout.exercises[0].exerciseName)
    }

    @Test
    fun `OnAddExercise does not add duplicate`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        val exercise = ExerciseMaster(1, "Bench Press", null, null, 1, "Pecho", 8)
        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(exercise))
        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(exercise))

        val workout = viewModel.uiState.value.workouts[0]
        assertEquals(1, workout.exercises.size)
    }

    @Test
    fun `OnRemoveExercise removes exercise and reindexes`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(
            ExerciseMaster(1, "Bench Press", null, null, 1, "Pecho", 8)
        ))
        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(
            ExerciseMaster(2, "Overhead Press", null, null, 3, "Hombro", 7)
        ))

        viewModel.onAction(CreateRoutineUiAction.OnRemoveExercise(1))

        val workout = viewModel.uiState.value.workouts[0]
        assertEquals(1, workout.exercises.size)
        assertEquals(1, workout.exercises[0].orderIndex)
    }

    @Test
    fun `OnSaveExerciseSets updates sets and notes for exercise`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(
            ExerciseMaster(1, "Bench Press", null, null, 1, "Pecho", 8)
        ))

        val newSets = listOf(
            SetDraft(1, 12, 60.0),
            SetDraft(2, 10, 65.0),
            SetDraft(3, 8, 70.0),
        )
        viewModel.onAction(CreateRoutineUiAction.OnSaveExerciseSets(1, newSets, "Agarre medio"))

        val exercise = viewModel.uiState.value.workouts[0].exercises[0]
        assertEquals(3, exercise.sets.size)
        assertEquals("Agarre medio", exercise.notes)
        assertEquals(70.0, exercise.sets[2].targetWeight)
    }

    @Test
    fun `OnSaveWorkout returns to WORKOUTS step`() = runTest(testDispatcher) {
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        advanceUntilIdle()

        viewModel.onAction(CreateRoutineUiAction.OnSaveWorkout)
        assertEquals(RoutineWizardStep.WORKOUTS, viewModel.uiState.value.currentStep)
        assertEquals(-1, viewModel.uiState.value.activeWorkoutIndex)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Paso 4: Submit
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `submit with valid data sets isSuccess`() = runTest(testDispatcher) {
        fillCompleteForm()

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, routineRepository.createRoutineCallCount)
    }

    @Test
    fun `submit with server error sets errorMessage`() = runTest(testDispatcher) {
        routineRepository.createRoutineResult = Result.Error(
            ApiException.ServerException("Error del servidor")
        )
        fillCompleteForm()

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `submit with network error shows connection error`() = runTest(testDispatcher) {
        routineRepository.createRoutineResult = Result.Error(Exception("timeout"))
        fillCompleteForm()

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals(Spanish.errorTexts.connectionError, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `submit with activateOnCreate true calls activateRoutineUseCase`() = runTest(testDispatcher) {
        fillCompleteForm()
        viewModel.onAction(CreateRoutineUiAction.OnActivateToggle(true))

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, routineRepository.createRoutineCallCount)
        assertEquals(1, routineRepository.activateRoutineCallCount)
    }

    @Test
    fun `submit with activateOnCreate true but activate fails shows error`() = runTest(testDispatcher) {
        routineRepository.activateRoutineResult = Result.Error(
            ApiException.ServerException("No se pudo activar la rutina")
        )
        fillCompleteForm()
        viewModel.onAction(CreateRoutineUiAction.OnActivateToggle(true))

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("No se pudo activar la rutina", viewModel.uiState.value.errorMessage)
        assertEquals(1, routineRepository.createRoutineCallCount)
        assertEquals(1, routineRepository.activateRoutineCallCount)
    }

    @Test
    fun `submit with activateOnCreate false does not call activateRoutineUseCase`() = runTest(testDispatcher) {
        fillCompleteForm()
        // activateOnCreate defaults to false, no need to set it

        viewModel.onAction(CreateRoutineUiAction.OnSubmit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals(1, routineRepository.createRoutineCallCount)
        assertEquals(0, routineRepository.activateRoutineCallCount)  // Should not be called
    }

    // ═══════════════════════════════════════════════════════════════════
    // Navegación
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnDismiss triggers navigateUp`() {
        viewModel.onAction(CreateRoutineUiAction.OnDismiss)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }

    @Test
    fun `OnSuccessAnimationFinished triggers navigateUp`() {
        viewModel.onAction(CreateRoutineUiAction.OnSuccessAnimationFinished)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }

    @Test
    fun `OnErrorDismissed clears errorMessage`() {
        viewModel.onAction(CreateRoutineUiAction.OnErrorDismissed)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Textos localizados
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `routineTexts returns Spanish texts`() {
        assertEquals("Nueva rutina", viewModel.routineTexts.screenTitle)
    }

    @Test
    fun `difficultyEntries has 3 entries`() {
        assertEquals(3, viewModel.difficultyEntries.size)
    }

    @Test
    fun `goalEntries has 5 entries`() {
        assertEquals(5, viewModel.goalEntries.size)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════════

    private fun fillStep1() {
        viewModel.onAction(CreateRoutineUiAction.OnNameChange("Push Pull Legs"))
        viewModel.onAction(CreateRoutineUiAction.OnDifficultySelected(DifficultyLevel.INTERMEDIATE))
        viewModel.onAction(CreateRoutineUiAction.OnGoalTypeSelected(GoalType.MUSCLE_GAIN))
        viewModel.onAction(CreateRoutineUiAction.OnDayToggled(DayOfWeek.MONDAY))
        viewModel.onAction(CreateRoutineUiAction.OnDayToggled(DayOfWeek.WEDNESDAY))
        viewModel.onAction(CreateRoutineUiAction.OnDayToggled(DayOfWeek.FRIDAY))
    }

    private fun fillCompleteForm() {
        fillStep1()
        viewModel.onAction(CreateRoutineUiAction.OnAddWorkout("Push Day"))
        viewModel.onAction(CreateRoutineUiAction.OnEditWorkout(0))
        viewModel.onAction(CreateRoutineUiAction.OnAddExercise(
            ExerciseMaster(1, "Bench Press", null, null, 1, "Pecho", 8)
        ))
        viewModel.onAction(CreateRoutineUiAction.OnSaveWorkout)
    }
}
