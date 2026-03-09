package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.task.Task
import com.agusstkd.goodlife.domain.repository.TaskRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class FakeTaskRepository : TaskRepository {

    var createTaskResult: Result<Task> = Result.Success(DEFAULT_TASK)

    var createTaskCallCount = 0
        private set
    var lastTitle: String? = null
        private set

    override suspend fun createTask(
        title: String,
        description: String?,
        scheduledDate: LocalDate?,
        scheduledTime: LocalTime?,
        recurrentDays: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Result<Task> {
        createTaskCallCount++
        lastTitle = title
        return createTaskResult
    }

    companion object {
        val DEFAULT_TASK = Task(
            id = 1L,
            title = "Test Task",
            description = null,
            scheduledDate = LocalDate(2026, 3, 9),
            scheduledTime = null,
            recurrentDays = emptySet(),
            startDate = null,
            endDate = null,
            reminderEnabled = false
        )
    }
}
