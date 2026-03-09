package com.agusstkd.goodlife.presentation.screen.add.task

import com.agusstkd.goodlife.core.datetime.FakeDateProvider
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.domain.usecase.task.CreateTaskUseCase
import com.agusstkd.goodlife.domain.usecase.task.result.CreateTaskResult
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.fake.FakeTaskRepository
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiAction
import com.agusstkd.goodlife.presentation.screen.add.task.model.DatePickerField
import com.agusstkd.goodlife.presentation.screen.add.task.model.SchedulingMode
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
class CreateTaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var navigationController: FakeNavigationController
    private lateinit var viewModel: CreateTaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        navigationController = FakeNavigationController()
        viewModel = CreateTaskViewModel(
            navigationController = navigationController,
            language = Spanish,
            createTaskUseCase = CreateTaskUseCase(
                repository = repository,
                dispatcher = TestDispatcherProvider(testDispatcher)
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // State updates
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertEquals(SchedulingMode.ONCE, state.schedulingMode)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.errorMessage)
    }

    @Test
    fun `OnTitleChange updates title`() {
        viewModel.onAction(CreateTaskUiAction.OnTitleChange("Mi tarea"))
        assertEquals("Mi tarea", viewModel.uiState.value.title)
    }

    @Test
    fun `OnDescriptionChange updates description`() {
        viewModel.onAction(CreateTaskUiAction.OnDescriptionChange("Detalle"))
        assertEquals("Detalle", viewModel.uiState.value.description)
    }

    @Test
    fun `OnSchedulingModeChange toggles mode`() {
        viewModel.onAction(CreateTaskUiAction.OnSchedulingModeChange(SchedulingMode.RECURRENT))
        assertEquals(SchedulingMode.RECURRENT, viewModel.uiState.value.schedulingMode)
    }

    @Test
    fun `OnDayToggled adds and removes days`() {
        viewModel.onAction(CreateTaskUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertTrue(DayOfWeek.MONDAY in viewModel.uiState.value.recurrentDays)

        viewModel.onAction(CreateTaskUiAction.OnDayToggled(DayOfWeek.MONDAY))
        assertFalse(DayOfWeek.MONDAY in viewModel.uiState.value.recurrentDays)
    }

    @Test
    fun `OnHasEndDateToggle toggles`() {
        assertFalse(viewModel.uiState.value.hasEndDate)
        viewModel.onAction(CreateTaskUiAction.OnHasEndDateToggle)
        assertTrue(viewModel.uiState.value.hasEndDate)
    }

    @Test
    fun `OnHasTimeToggle toggles`() {
        assertFalse(viewModel.uiState.value.hasTime)
        viewModel.onAction(CreateTaskUiAction.OnHasTimeToggle)
        assertTrue(viewModel.uiState.value.hasTime)
    }

    @Test
    fun `OnDatePickerOpen and dismiss`() {
        viewModel.onAction(CreateTaskUiAction.OnDatePickerOpen(DatePickerField.SCHEDULED_DATE))
        assertEquals(DatePickerField.SCHEDULED_DATE, viewModel.uiState.value.activeDatePickerField)

        viewModel.onAction(CreateTaskUiAction.OnDatePickerDismiss)
        assertNull(viewModel.uiState.value.activeDatePickerField)
    }

    @Test
    fun `OnDateSelected updates scheduled date`() {
        val date = LocalDate(2026, 4, 15)
        viewModel.onAction(CreateTaskUiAction.OnDateSelected(DatePickerField.SCHEDULED_DATE, date))

        assertEquals(date, viewModel.uiState.value.scheduledDate)
        assertNull(viewModel.uiState.value.activeDatePickerField)
    }

    @Test
    fun `OnTimeSelected updates time and dismisses picker`() {
        viewModel.onAction(CreateTaskUiAction.OnTimePickerOpen)
        assertTrue(viewModel.uiState.value.showTimePicker)

        viewModel.onAction(CreateTaskUiAction.OnTimeSelected(LocalTime(14, 30)))
        assertEquals(LocalTime(14, 30), viewModel.uiState.value.scheduledTime)
        assertFalse(viewModel.uiState.value.showTimePicker)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // Submit
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `OnSubmit with valid data sets isSuccess true`() = runTest(testDispatcher) {
        viewModel.onAction(CreateTaskUiAction.OnTitleChange("Test task"))
        viewModel.onAction(CreateTaskUiAction.OnDateSelected(
            DatePickerField.SCHEDULED_DATE, LocalDate(2026, 3, 10)
        ))

        viewModel.onAction(CreateTaskUiAction.OnSubmit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `OnSubmit with server error sets errorMessage`() = runTest(testDispatcher) {
        repository.createTaskResult = com.agusstkd.goodlife.core.result.Result.Error(
            com.agusstkd.goodlife.core.network.ApiException.ServerException("DB down")
        )
        viewModel.onAction(CreateTaskUiAction.OnTitleChange("Test task"))
        viewModel.onAction(CreateTaskUiAction.OnDateSelected(
            DatePickerField.SCHEDULED_DATE, LocalDate(2026, 3, 10)
        ))

        viewModel.onAction(CreateTaskUiAction.OnSubmit)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `OnDismiss navigates up`() {
        viewModel.onAction(CreateTaskUiAction.OnDismiss)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }

    @Test
    fun `OnSuccessAnimationFinished navigates up`() {
        viewModel.onAction(CreateTaskUiAction.OnSuccessAnimationFinished)
        assertTrue(navigationController.navigatedActions.any { it is NavigationAction.NavigateUp })
    }
}
