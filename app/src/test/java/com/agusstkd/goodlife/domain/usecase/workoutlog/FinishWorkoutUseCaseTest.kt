package com.agusstkd.goodlife.domain.usecase.workoutlog

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.FinishExerciseDraft
import com.agusstkd.goodlife.domain.model.training.FinishSetDraft
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import com.agusstkd.goodlife.domain.usecase.workoutlog.result.FinishWorkoutResult
import com.agusstkd.goodlife.fake.FakeWorkoutLogRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FinishWorkoutUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeWorkoutLogRepository
    private lateinit var useCase: FinishWorkoutUseCase

    @Before
    fun setUp() {
        repository = FakeWorkoutLogRepository()
        useCase = FinishWorkoutUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
        )
    }

    @Test
    fun `empty exercises returns EmptyExercises without calling repository`() = runTest(testDispatcher) {
        val result = useCase(validDraft().copy(exercises = emptyList()))

        assertEquals(FinishWorkoutResult.EmptyExercises, result)
        assertEquals(0, repository.finishWorkoutCallCount)
    }

    @Test
    fun `exercises with empty sets return EmptyExercises without calling repository`() = runTest(testDispatcher) {
        val result = useCase(
            validDraft().copy(
                exercises = listOf(
                    FinishExerciseDraft(exerciseMasterId = 1L, notes = null, sets = emptyList()),
                ),
            ),
        )

        assertEquals(FinishWorkoutResult.EmptyExercises, result)
        assertEquals(0, repository.finishWorkoutCallCount)
    }

    @Test
    fun `valid draft calls repository and returns Success`() = runTest(testDispatcher) {
        val draft = validDraft()

        val result = useCase(draft)

        assertTrue(result is FinishWorkoutResult.Success)
        assertEquals(1L, (result as FinishWorkoutResult.Success).logId)
        assertEquals(1, repository.finishWorkoutCallCount)
        assertEquals(draft, repository.lastDraft)
    }

    @Test
    fun `NotFoundException maps to NotFound`() = runTest(testDispatcher) {
        repository.finishWorkoutResult = Result.Error(ApiException.NotFoundException("missing"))

        val result = useCase(validDraft())

        assertEquals(FinishWorkoutResult.NotFound, result)
    }

    @Test
    fun `ServerException maps to ServerError`() = runTest(testDispatcher) {
        repository.finishWorkoutResult = Result.Error(ApiException.ServerException("db down"))

        val result = useCase(validDraft())

        assertTrue(result is FinishWorkoutResult.ServerError)
        assertEquals("db down", (result as FinishWorkoutResult.ServerError).message)
    }

    @Test
    fun `BadRequestException maps to ServerError`() = runTest(testDispatcher) {
        repository.finishWorkoutResult = Result.Error(ApiException.BadRequestException("invalid sets"))

        val result = useCase(validDraft())

        assertTrue(result is FinishWorkoutResult.ServerError)
        assertEquals("invalid sets", (result as FinishWorkoutResult.ServerError).message)
    }

    @Test
    fun `UnknownApiException maps to ServerError`() = runTest(testDispatcher) {
        repository.finishWorkoutResult = Result.Error(ApiException.UnknownApiException("teapot", 418))

        val result = useCase(validDraft())

        assertTrue(result is FinishWorkoutResult.ServerError)
        assertEquals("teapot", (result as FinishWorkoutResult.ServerError).message)
    }

    @Test
    fun `generic Exception maps to NetworkError`() = runTest(testDispatcher) {
        repository.finishWorkoutResult = Result.Error(Exception("timeout"))

        val result = useCase(validDraft())

        assertEquals(FinishWorkoutResult.NetworkError, result)
    }

    private fun validDraft() = FinishWorkoutDraft(
        routineId = 10L,
        workoutId = 20L,
        name = "Push Day",
        durationMinutes = 60,
        startTime = LocalDateTime(2026, 10, 29, 18, 0),
        endTime = LocalDateTime(2026, 10, 29, 19, 0),
        notes = "Me sentí fuerte",
        exercises = listOf(
            FinishExerciseDraft(
                exerciseMasterId = 1L,
                notes = null,
                sets = listOf(FinishSetDraft(setNumber = 1, weight = 60.0, reps = 10, rpe = null)),
            )
        ),
    )
}
