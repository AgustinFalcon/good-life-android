package com.agusstkd.goodlife.domain.model.training

import kotlinx.datetime.LocalDateTime

/**
 * Sesión de entrenamiento realizada, lista para persistir como workout log.
 *
 * Vive en dominio: no es el DTO de Retrofit.
 * Jerarquía: [FinishWorkoutDraft] → [FinishExerciseDraft] → [FinishSetDraft].
 *
 * @property notes Feedback de sesión del usuario. Nunca null: string vacío si no escribió nada.
 */
data class FinishWorkoutDraft(
    val routineId: Long?,
    val workoutId: Long?,
    val name: String,
    val durationMinutes: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val notes: String = "",
    val exercises: List<FinishExerciseDraft>,
)

data class FinishExerciseDraft(
    val exerciseMasterId: Long,
    val notes: String?,
    val sets: List<FinishSetDraft>,
)

data class FinishSetDraft(
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rpe: Int?,
)
