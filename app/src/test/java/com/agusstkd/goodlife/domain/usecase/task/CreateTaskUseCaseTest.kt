package com.agusstkd.goodlife.domain.usecase.task

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.task.result.CreateTaskResult
import com.agusstkd.goodlife.fake.FakeTaskRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateTaskUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var useCase: CreateTaskUseCase

    @Before
    fun setUp() {
        repository = FakeTaskRepository()
        useCase = CreateTaskUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher)
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // Validaciones locales
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `blank title returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            title = "   ",
            description = null,
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.ValidationError)
        assertEquals(0, repository.createTaskCallCount)
    }

    @Test
    fun `scheduledDate AND recurrentDays returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            title = "Valid title",
            description = null,
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 1),
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.ValidationError)
        assertEquals(0, repository.createTaskCallCount)
    }

    @Test
    fun `recurrentDays without startDate returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            title = "Valid title",
            description = null,
            scheduleDate = null,
            recurrentDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            startDate = null,
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.ValidationError)
        assertEquals(0, repository.createTaskCallCount)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // Casos exitosos
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `valid once task returns Success`() = runTest(testDispatcher) {
        val result = useCase(
            title = "Comprar leche",
            description = "Del supermercado",
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            scheduledTime = LocalTime(10, 0)
        )

        assertTrue(result is CreateTaskResult.Success)
        assertEquals(1, repository.createTaskCallCount)
        assertEquals("Comprar leche", repository.lastTitle)
    }

    @Test
    fun `valid recurrent task returns Success`() = runTest(testDispatcher) {
        val result = useCase(
            title = "Ir al gimnasio",
            description = null,
            scheduleDate = null,
            recurrentDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startDate = LocalDate(2026, 3, 1),
            endDate = LocalDate(2026, 6, 30),
            scheduledTime = LocalTime(7, 0)
        )

        assertTrue(result is CreateTaskResult.Success)
        assertEquals(1, repository.createTaskCallCount)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // Errores del repositorio
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `repository BadRequest maps to ValidationError`() = runTest(testDispatcher) {
        repository.createTaskResult = Result.Error(
            ApiException.BadRequestException("title already exists")
        )

        val result = useCase(
            title = "Duplicada",
            description = null,
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.ValidationError)
        assertEquals("title already exists", (result as CreateTaskResult.ValidationError).message)
    }

    @Test
    fun `repository ServerError maps to ServerError`() = runTest(testDispatcher) {
        repository.createTaskResult = Result.Error(
            ApiException.ServerException("Internal server error")
        )

        val result = useCase(
            title = "Task",
            description = null,
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.ServerError)
    }

    @Test
    fun `repository network error maps to NetworkError`() = runTest(testDispatcher) {
        repository.createTaskResult = Result.Error(java.io.IOException("timeout"))

        val result = useCase(
            title = "Task",
            description = null,
            scheduleDate = LocalDate(2026, 3, 10),
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            scheduledTime = null
        )

        assertTrue(result is CreateTaskResult.NetworkError)
    }
}
