package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.task.Task
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Contrato de acceso a datos para la gestión de tareas del usuario.
 *
 * Define las operaciones disponibles sobre tareas sin acoplar al dominio
 * a ningún detalle de implementación (Retrofit, Room, etc.).
 * La implementación concreta vive en [com.agusstkd.goodlife.data.repository.TaskRepositoryImpl].
 *
 * @see Task
 * @see com.agusstkd.goodlife.domain.usecase.task.CreateTaskUseCase
 */
interface TaskRepository {

    /**
     * Crea una nueva tarea para el usuario autenticado.
     *
     * Los parámetros [scheduledDate] y [recurrentDays] son mutuamente excluyentes:
     * - Si [scheduledDate] tiene valor → modo ONCE, [recurrentDays] debe estar vacío.
     * - Si [recurrentDays] no está vacío → modo RECURRENT, [scheduledDate] debe ser nulo
     *   y [startDate] es obligatorio.
     *
     * La validación de esta regla es responsabilidad de [com.agusstkd.goodlife.domain.usecase.task.CreateTaskUseCase].
     *
     * @param title Título de la tarea. No puede estar vacío.
     * @param description Descripción opcional de la tarea.
     * @param scheduledDate Fecha de ejecución única. Solo para modo ONCE.
     * @param scheduledTime Hora de ejecución opcional. Aplica a ambos modos.
     * @param recurrentDays Días de la semana en que se repite. Vacío para modo ONCE.
     * @param startDate Fecha de inicio de la recurrencia. Requerido si [recurrentDays] no está vacío.
     * @param endDate Fecha de fin de la recurrencia. Opcional.
     * @return [Result.Success] con la [Task] creada y su ID asignado por el backend,
     *         o [Result.Error] con la excepción correspondiente.
     */
    suspend fun createTask(
        title: String,
        description: String?,
        scheduledDate: LocalDate?,
        scheduledTime: LocalTime?,
        recurrentDays: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Result<Task>
}
