package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.datasource.TaskRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.task.CreateTaskRequest
import com.agusstkd.goodlife.data.remote.dto.response.task.toDomain
import com.agusstkd.goodlife.domain.model.task.Task
import com.agusstkd.goodlife.domain.repository.TaskRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber


/**
 * Implementación de [TaskRepository] que opera contra el backend remoto.
 *
 * Responsabilidades:
 * - Convertir tipos de dominio ([LocalDate], [DayOfWeek]) a formatos de red (String ISO-8601, Int 1-7).
 * - Construir el [CreateTaskRequest] DTO a partir de los parámetros de dominio.
 * - Delegar la llamada HTTP a [TaskRemoteDataSource].
 * - Convertir el [TaskResponse] de red a [Task] de dominio via [toDomain].
 *
 * FASE 1: Solo backend (fuente de verdad 100%).
 * FASE 2 (futuro): Agregar Room para cache offline.
 *
 * @see TaskRepository
 * @see TaskRemoteDataSource
 */
class TaskRepositoryImpl(
    private val taskRemoteDataSource: TaskRemoteDataSource,
) : TaskRepository {

    override suspend fun createTask(
        title: String,
        description: String?,
        scheduledDate: LocalDate?,
        scheduledTime: LocalTime?,
        recurrentDays: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Result<Task> {
        val taskRequest = CreateTaskRequest(
            title = title,
            description = description,
            scheduledDate = scheduledDate?.toString(),
            scheduledTime = scheduledTime?.toString(),
            daysOfWeek = recurrentDays.takeIf { it.isNotEmpty() }?.map { it.isoDayNumber },
            startDate = startDate?.toString(),
            endDate = endDate?.toString()
        )
        return when(val result = taskRemoteDataSource.createTask(taskRequest)) {
            is Result.Success -> {
                Result.Success(result.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }
}
