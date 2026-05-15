package com.agusstkd.goodlife.presentation.screen.add.habit

import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.usecase.habit.CreateHabitUseCase
import com.agusstkd.goodlife.fake.FakeHabitRepository
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiAction
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
class CreateHabitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeHabitRepository
    private lateinit var navigationController: FakeNavigationController
    private lateinit var viewModel: CreateHabitViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHabitRepository()
        navigationController = FakeNavigationController()

        val useCase = CreateHabitUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
        )

        viewModel = CreateHabitViewModel(
            navigationController = navigationController,
            language = Spanish,
            createHabitUseCase = useCase,
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
    fun `initial state has empty fields`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.description)
        assertNull(state.selectedCategory)
        assertEquals("", state.targetValue)
        assertEquals("", state.unit)
        assertTrue(state.selectedDays.isEmpty())
        assertNull(state.startDate)
        assertFalse(state.hasEndDate)
        assertFalse(state.hasTime)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.errorMessage)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Acciones de UI (state updates)
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnNameChange updates name`() {
        viewModel.onAction(CreateHabitUiAction.OnNameChange("Beber agua"))
        assertEquals("Beber agua", viewModel.uiState.value.name)
    }

    @Test
    fun `OnDescriptionChange updates description`() {
        viewModel.onAction(CreateHabitUiAction.OnDescriptionChange("Mantenerse hidratado"))
        assertEquals("Mantenerse hidratado", viewModel.uiState.value.description)
    }

    @Test
    fun `OnCategorySelected updates selectedCategory`() {
        viewModel.onAction(CreateHabitUiAction.OnCategorySelected(HabitCategory.HYDRATION))
        assertEquals(HabitCategory.HYDRATION, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `OnTargetValueChange updates targetValue`() {
        viewModel.onAction(CreateHabitUiAction.OnTargetValueChange("8"))
        assertEquals("8", viewModel.uiState.value.targetValue)
    }

    @Test
    fun `OnUnitChange updates unit`() {
        viewModel.onAction(CreateHabitUiAction.OnUnitChange("vasos"))
        assertEquals("vasos", viewModel.uiState.value.unit)
    }

    @Test
    fun `OnDayToggled adds and removes day`() {
        viewModel.onAction(CreateHabitUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertTrue(DayOfWeek.MONDAY in viewModel.uiState.value.selectedDays)

        viewModel.onAction(CreateHabitUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertFalse(DayOfWeek.MONDAY in viewModel.uiState.value.selectedDays)
    }

    @Test
    fun `OnHasEndDateToggle toggles hasEndDate`() {
        assertFalse(viewModel.uiState.value.hasEndDate)
        viewModel.onAction(CreateHabitUiAction.OnHasEndDateToggle)
        assertTrue(viewModel.uiState.value.hasEndDate)
    }

    @Test
    fun `OnHasTimeToggle toggles hasTime`() {
        assertFalse(viewModel.uiState.value.hasTime)
        viewModel.onAction(CreateHabitUiAction.OnHasTimeToggle)
        assertTrue(viewModel.uiState.value.hasTime)
    }

    @Test
    fun `OnTimeSelected updates scheduledTime and hides picker`() {
        viewModel.onAction(CreateHabitUiAction.OnTimePickerOpen)
        assertTrue(viewModel.uiState.value.showTimePicker)

        viewModel.onAction(CreateHabitUiAction.OnTimeSelected(LocalTime(9, 30)))
        assertEquals(LocalTime(9, 30), viewModel.uiState.value.scheduledTime)
        assertFalse(viewModel.uiState.value.showTimePicker)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Submit
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `submit with valid data sets isSuccess`() = runTest(testDispatcher) {
        fillValidForm()

        viewModel.onAction(CreateHabitUiAction.OnSubmit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, repository.createHabitCallCount)
    }

    @Test
    fun `submit with server error sets errorMessage`() = runTest(testDispatcher) {
        repository.createHabitResult = Result.Error(
            ApiException.ServerException("Error del servidor")
        )
        fillValidForm()

        viewModel.onAction(CreateHabitUiAction.OnSubmit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `submit with network error sets connection error message`() = runTest(testDispatcher) {
        repository.createHabitResult = Result.Error(Exception("timeout"))
        fillValidForm()

        viewModel.onAction(CreateHabitUiAction.OnSubmit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals(Spanish.errorTexts.connectionError, viewModel.uiState.value.errorMessage)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Navegación
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `OnDismiss triggers navigateUp`() {
        viewModel.onAction(CreateHabitUiAction.OnDismiss)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }

    @Test
    fun `OnSuccessAnimationFinished triggers navigateUp`() {
        viewModel.onAction(CreateHabitUiAction.OnSuccessAnimationFinished)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }

    @Test
    fun `OnErrorDismissed clears errorMessage`() {
        viewModel.onAction(CreateHabitUiAction.OnNameChange("Test"))
        viewModel.onAction(CreateHabitUiAction.OnErrorDismissed)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Textos localizados
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `categoryEntries has 13 entries matching HabitCategory values`() {
        assertEquals(HabitCategory.entries.size, viewModel.categoryEntries.size)
    }

    @Test
    fun `createHabitTexts returns Spanish texts`() {
        assertEquals("Nuevo hábito", viewModel.createHabitTexts.screenTitle)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════════

    private fun fillValidForm() {
        viewModel.onAction(CreateHabitUiAction.OnNameChange("Beber agua"))
        viewModel.onAction(CreateHabitUiAction.OnCategorySelected(HabitCategory.HYDRATION))
        viewModel.onAction(CreateHabitUiAction.OnTargetValueChange("8"))
        viewModel.onAction(CreateHabitUiAction.OnUnitChange("vasos"))
        viewModel.onAction(CreateHabitUiAction.OnDayToggled(DayOfWeek.MONDAY))
        viewModel.onAction(CreateHabitUiAction.OnDayToggled(DayOfWeek.WEDNESDAY))
        viewModel.onAction(CreateHabitUiAction.OnDayToggled(DayOfWeek.FRIDAY))
        viewModel.onAction(
            CreateHabitUiAction.OnDateSelected(
                com.agusstkd.goodlife.presentation.screen.add.habit.model.HabitDatePickerField.START_DATE,
                LocalDate(2026, 3, 9),
            )
        )
    }
}
