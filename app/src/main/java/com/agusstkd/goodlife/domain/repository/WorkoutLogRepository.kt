package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft

/**
 * Puerto de persistencia del log de un entrenamiento terminado.
 *
 * No conoce HTTP ni Room. La implementación vive en data.
 */
interface WorkoutLogRepository {

    /**
     * Registra la sesión y devuelve el id del log creado.
     *
     * @param draft Sesión realizada (ejercicios y series reales).
     * @return [Result.Success] con el `logId`, o [Result.Error] de transporte/servidor.
     */
    suspend fun finishWorkout(draft: FinishWorkoutDraft): Result<Long>
}
