package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class FakeRoutineRepository : RoutineRepository {

    var createRoutineResult: Result<Routine> = Result.Success(DEFAULT_ROUTINE)
    var activateRoutineResult: Result<Unit> = Result.Success(Unit)
    var getActiveRoutineResult: Result<Routine> = Result.Success(DEFAULT_ROUTINE)

    var createRoutineCallCount = 0
        private set
    var activateRoutineCallCount = 0
        private set
    var getActiveRoutineCallCount = 0
        private set
    var lastName: String? = null
        private set

    override suspend fun createRoutine(
        name: String,
        description: String?,
        difficultyLevel: DifficultyLevel,
        goalType: GoalType,
        scheduledTime: LocalTime?,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
        workouts: List<WorkoutDraft>,
    ): Result<Routine> {
        createRoutineCallCount++
        lastName = name
        return createRoutineResult
    }

    override suspend fun activateRoutine(routineId: Long): Result<Unit> {
        activateRoutineCallCount++
        return activateRoutineResult
    }

    override suspend fun getActiveRoutine(): Result<Routine> {
        getActiveRoutineCallCount++
        return getActiveRoutineResult
    }

    companion object {
        val DEFAULT_ROUTINE = Routine(
            id = 1L,
            name = "Push Pull Legs",
            description = null,
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            goalType = GoalType.MUSCLE_GAIN,
            isActive = false,
            scheduledTime = null,
            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startDate = LocalDate(2026, 3, 10),
            endDate = null,
            workouts = emptyList(),
        )
    }
}
