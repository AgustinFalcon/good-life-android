package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface RoutineRepository {

    suspend fun createRoutine(
        name: String,
        description: String?,
        difficultyLevel: DifficultyLevel,
        goalType: GoalType,
        scheduledTime: LocalTime?,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate?,
        endDate: LocalDate?,
        workouts: List<WorkoutDraft>,
    ): Result<Routine>

    suspend fun activateRoutine(routineId: Long): Result<Unit>

    /**
     * Obtiene la rutina activa del usuario autenticado.
     *
     * @return [Result.Success] con la [Routine] activa o `null` cuando no existe una activa.
     *         Los fallos de transporte y servidor se representan con [Result.Error].
     */
    suspend fun getActiveRoutine(): Result<Routine?>
}
