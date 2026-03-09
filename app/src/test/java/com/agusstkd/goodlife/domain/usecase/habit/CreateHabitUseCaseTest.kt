package com.agusstkd.goodlife.domain.usecase.habit

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.usecase.habit.result.CreateHabitResult
import com.agusstkd.goodlife.fake.FakeHabitRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateHabitUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeHabitRepository
    private lateinit var useCase: CreateHabitUseCase

    @Before
    fun setUp() {
        repository = FakeHabitRepository()
        useCase = CreateHabitUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // Validaciones locales
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `blank name returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "   ",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals(0, repository.createHabitCallCount)
    }

    @Test
    fun `empty days returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Beber agua",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = emptySet(),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals(0, repository.createHabitCallCount)
    }

    @Test
    fun `zero targetValue returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Meditar",
            description = null,
            category = HabitCategory.MEDITATION,
            targetValue = 0,
            unit = "minutos",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals(0, repository.createHabitCallCount)
    }

    @Test
    fun `blank unit returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Leer",
            description = null,
            category = HabitCategory.READING,
            targetValue = 30,
            unit = "  ",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals(0, repository.createHabitCallCount)
    }

    @Test
    fun `endDate before startDate returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Ejercicio",
            description = null,
            category = HabitCategory.EXERCISE,
            targetValue = 1,
            unit = "sesión",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 15),
            endDate = LocalDate(2026, 3, 10),
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals(0, repository.createHabitCallCount)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Caso exitoso
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `valid input calls repository and returns Success`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Beber agua",
            description = "Mantenerse hidratado",
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.Success)
        assertEquals(1, repository.createHabitCallCount)
        assertEquals("Beber agua", repository.lastName)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Errores del repositorio
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `BadRequestException maps to ValidationError`() = runTest(testDispatcher) {
        repository.createHabitResult = Result.Error(
            ApiException.BadRequestException("Invalid data")
        )

        val result = useCase(
            name = "Beber agua",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ValidationError)
        assertEquals("Invalid data", (result as CreateHabitResult.ValidationError).message)
    }

    @Test
    fun `ServerException maps to ServerError`() = runTest(testDispatcher) {
        repository.createHabitResult = Result.Error(
            ApiException.ServerException("Internal server error")
        )

        val result = useCase(
            name = "Beber agua",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.ServerError)
        assertEquals("Internal server error", (result as CreateHabitResult.ServerError).message)
    }

    @Test
    fun `generic Exception maps to NetworkError`() = runTest(testDispatcher) {
        repository.createHabitResult = Result.Error(
            Exception("Connection timeout")
        )

        val result = useCase(
            name = "Beber agua",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )

        assertTrue(result is CreateHabitResult.NetworkError)
    }
}
