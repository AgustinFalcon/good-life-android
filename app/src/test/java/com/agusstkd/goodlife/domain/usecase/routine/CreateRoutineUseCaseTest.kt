package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseDraft
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.usecase.routine.result.CreateRoutineResult
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateRoutineUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRoutineRepository
    private lateinit var useCase: CreateRoutineUseCase

    @Before
    fun setUp() {
        repository = FakeRoutineRepository()
        useCase = CreateRoutineUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
        )
    }

    private fun validWorkouts() = listOf(
        WorkoutDraft(
            name = "Push Day",
            exercises = listOf(
                ExerciseDraft(
                    exerciseId = 1,
                    exerciseName = "Bench Press",
                    imageUrl = null,
                    muscleGroupName = "Pecho",
                    orderIndex = 1,
                    sets = listOf(SetDraft(1, 12, 60.0)),
                )
            ),
        )
    )

    // ═══════════════════════════════════════════════════════════════════
    // Validaciones locales
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `blank name returns ValidationError without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            name = "   ",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `null difficultyLevel returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = null,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `null goalType returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = null,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `empty daysOfWeek returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = emptySet(),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `empty workouts returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = emptyList(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `workout without exercises returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = listOf(WorkoutDraft(name = "Push Day")),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `exercise without sets returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = listOf(
                WorkoutDraft(
                    name = "Push Day",
                    exercises = listOf(
                        ExerciseDraft(1, "Bench Press", null, "Pecho", 1, sets = emptyList())
                    ),
                )
            ),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    @Test
    fun `endDate before startDate returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = LocalDate(2026, 3, 15),
            endDate = LocalDate(2026, 3, 10),
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals(0, repository.createRoutineCallCount)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Caso exitoso
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `valid input calls repository and returns Success`() = runTest(testDispatcher) {
        val result = useCase(
            name = "Push Pull Legs",
            description = "Rutina de hipertrofia",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startDate = LocalDate(2026, 3, 10),
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.Success)
        assertEquals(1, repository.createRoutineCallCount)
        assertEquals("Push Pull Legs", repository.lastName)
    }

    // ═══════════════════════════════════════════════════════════════════
    // Errores del repositorio
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun `BadRequestException maps to ValidationError`() = runTest(testDispatcher) {
        repository.createRoutineResult = Result.Error(
            ApiException.BadRequestException("Invalid data")
        )

        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ValidationError)
        assertEquals("Invalid data", (result as CreateRoutineResult.ValidationError).message)
    }

    @Test
    fun `ServerException maps to ServerError`() = runTest(testDispatcher) {
        repository.createRoutineResult = Result.Error(
            ApiException.ServerException("Internal server error")
        )

        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.ServerError)
        assertEquals("Internal server error", (result as CreateRoutineResult.ServerError).message)
    }

    @Test
    fun `generic Exception maps to NetworkError`() = runTest(testDispatcher) {
        repository.createRoutineResult = Result.Error(
            Exception("Connection timeout")
        )

        val result = useCase(
            name = "PPL",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startDate = null,
            endDate = null,
            workouts = validWorkouts(),
        )

        assertTrue(result is CreateRoutineResult.NetworkError)
    }
}
