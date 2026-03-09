package com.agusstkd.goodlife.domain.usecase.task

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.domain.repository.TaskRepository
import com.agusstkd.goodlife.domain.usecase.task.result.CreateTaskResult
import com.agusstkd.goodlife.core.result.Result
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Caso de uso para crear una nueva tarea del usuario.
 *
 * ## Responsabilidades
 * 1. **Validar** la coherencia de los datos antes de ir a red:
 *    - Título no vacío.
 *    - [scheduledDate] y [recurrentDays] son mutuamente excluyentes (XOR).
 *    - Si [recurrentDays] no está vacío, [startDate] es obligatorio.
 * 2. Cambiar al dispatcher IO para no bloquear el hilo principal.
 * 3. Delegar la creación al [TaskRepository].
 * 4. Traducir el [Result] genérico del repositorio a [CreateTaskResult],
 *    convirtiendo cada [ApiException] en un subtipo semántico de dominio.
 *
 * ## Mapping de errores
 * | Result.Error causa                     | CreateTaskResult              |
 * |----------------------------------------|-------------------------------|
 * | [ApiException.BadRequestException]     | [CreateTaskResult.ValidationError] |
 * | [ApiException.ServerException]         | [CreateTaskResult.ServerError]     |
 * | IOException / cualquier otro           | [CreateTaskResult.NetworkError]    |
 *
 * @see TaskRepository
 * @see CreateTaskResult
 */
class CreateTaskUseCase(
    private val repository: TaskRepository,
    private val dispatcher: DispatcherProvider
) {

    /**
     * Crea una nueva tarea tras validar los datos de entrada.
     *
     * Las validaciones locales se ejecutan **antes** de ir a red, evitando
     * requests innecesarios al backend cuando el input es inválido.
     *
     * @param title Título de la tarea. No puede estar vacío ni contener solo espacios.
     * @param description Descripción opcional de la tarea.
     * @param scheduleDate Fecha de ejecución única (modo ONCE). Mutuamente excluyente con [recurrentDays].
     * @param recurrentDays Días de la semana en que se repite (modo RECURRENT). Vacío para modo ONCE.
     * @param startDate Fecha de inicio de la recurrencia. Obligatorio si [recurrentDays] no está vacío.
     * @param endDate Fecha de fin de la recurrencia. Opcional.
     * @param scheduledTime Hora de ejecución opcional. Aplica a ambos modos.
     * @return [CreateTaskResult] con el subtipo correspondiente al resultado de la operación.
     */
    suspend operator fun invoke(
        title: String,
        description: String?,
        scheduleDate: LocalDate?,
        recurrentDays: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
        scheduledTime: LocalTime?,
    ): CreateTaskResult {

        if (title.isBlank()) {
            return CreateTaskResult.ValidationError("El título no puede estar vacío")
        }

        if (scheduleDate != null && recurrentDays.isNotEmpty()) {
            return CreateTaskResult.ValidationError("No puedes seleccionar una fecha y crear dias recurrentes")
        }

        if (recurrentDays.isNotEmpty() && startDate == null) {
            return CreateTaskResult.ValidationError("Debes seleccionar una fecha de inicio para tareas recurrentes")
        }

        return withContext(dispatcher.io) {
            val result = repository.createTask(
                title = title,
                description = description,
                scheduledDate = scheduleDate,
                recurrentDays = recurrentDays,
                startDate = startDate,
                endDate = endDate,
                scheduledTime = scheduledTime
            )

            when (result) {
                is Result.Success -> CreateTaskResult.Success
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.BadRequestException -> CreateTaskResult.ValidationError(ex.message)
                    is ApiException.ServerException -> CreateTaskResult.ServerError(ex.message)
                    else -> CreateTaskResult.NetworkError
                }
            }
        }
    }
}
