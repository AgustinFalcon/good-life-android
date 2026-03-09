package com.agusstkd.goodlife.domain.usecase.habit

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.repository.HabitRepository
import com.agusstkd.goodlife.domain.usecase.habit.result.CreateHabitResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Caso de uso para crear un nuevo hábito recurrente.
 *
 * ## Responsabilidades
 * 1. **Validar** coherencia de datos antes de ir a red:
 *    - Nombre no vacío.
 *    - Al menos un día de la semana seleccionado.
 *    - Meta numérica > 0.
 *    - Unidad no vacía.
 *    - endDate no anterior a startDate (si se provee).
 * 2. Cambiar al dispatcher IO (ownership del UseCase).
 * 3. Delegar al [HabitRepository].
 * 4. Traducir [Result] genérico a [CreateHabitResult] semántico.
 *
 * ## Mapping de errores
 * | Result.Error causa                 | CreateHabitResult           |
 * |------------------------------------|-----------------------------|
 * | ApiException.BadRequestException   | ValidationError             |
 * | ApiException.ServerException       | ServerError                 |
 * | IOException / otro                 | NetworkError                |
 *
 * @property repository Repositorio de hábitos.
 * @property dispatcher Proveedor de dispatchers para IO.
 */
class CreateHabitUseCase(
    private val repository: HabitRepository,
    private val dispatcher: DispatcherProvider,
) {

    /**
     * Crea un nuevo hábito tras validar los datos de entrada.
     *
     * @param name Nombre del hábito.
     * @param description Descripción opcional.
     * @param category Categoría de bienestar.
     * @param targetValue Meta numérica diaria (debe ser > 0).
     * @param unit Unidad de la meta (no vacía).
     * @param daysOfWeek Días activos (al menos 1).
     * @param startDate Fecha de inicio del hábito.
     * @param endDate Fecha de fin opcional.
     * @param scheduledTime Hora de ejecución opcional.
     * @return [CreateHabitResult] con el subtipo correspondiente.
     */
    suspend operator fun invoke(
        name: String,
        description: String?,
        category: HabitCategory,
        targetValue: Int,
        unit: String,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        scheduledTime: LocalTime?,
    ): CreateHabitResult {

        if (name.isBlank()) {
            return CreateHabitResult.ValidationError("El nombre no puede estar vacío")
        }

        if (daysOfWeek.isEmpty()) {
            return CreateHabitResult.ValidationError("Debes seleccionar al menos un día")
        }

        if (targetValue <= 0) {
            return CreateHabitResult.ValidationError("La meta debe ser mayor a 0")
        }

        if (unit.isBlank()) {
            return CreateHabitResult.ValidationError("La unidad no puede estar vacía")
        }

        if (endDate != null && endDate < startDate) {
            return CreateHabitResult.ValidationError("La fecha de fin no puede ser anterior al inicio")
        }

        return withContext(dispatcher.io) {
            val result = repository.createHabit(
                name = name,
                description = description,
                category = category,
                targetValue = targetValue,
                unit = unit,
                daysOfWeek = daysOfWeek,
                startDate = startDate,
                endDate = endDate,
                scheduledTime = scheduledTime,
            )

            when (result) {
                is Result.Success -> CreateHabitResult.Success
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.BadRequestException -> CreateHabitResult.ValidationError(ex.message)
                    is ApiException.ServerException -> CreateHabitResult.ServerError(ex.message)
                    else -> CreateHabitResult.NetworkError
                }
            }
        }
    }
}
