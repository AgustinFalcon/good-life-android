package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import com.agusstkd.goodlife.domain.repository.WorkoutLogRepository

class FakeWorkoutLogRepository : WorkoutLogRepository {

    var finishWorkoutResult: Result<Long> = Result.Success(1L)
    var finishWorkoutCallCount = 0
        private set
    var lastDraft: FinishWorkoutDraft? = null
        private set
    var finishWorkoutException: Throwable? = null

    override suspend fun finishWorkout(draft: FinishWorkoutDraft): Result<Long> {
        finishWorkoutCallCount++
        lastDraft = draft
        finishWorkoutException?.let { throw it }
        return finishWorkoutResult
    }
}
