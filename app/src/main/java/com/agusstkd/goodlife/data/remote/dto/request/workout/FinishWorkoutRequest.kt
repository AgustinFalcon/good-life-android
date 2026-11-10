package com.agusstkd.goodlife.data.remote.dto.request.workout

import com.agusstkd.goodlife.domain.model.training.FinishExerciseDraft
import com.agusstkd.goodlife.domain.model.training.FinishSetDraft
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import kotlinx.serialization.Serializable

/**
 * Body de `POST /api/v1/workouts/log/finish`.
 *
 * Fechas como string ISO porque el backend parsea `LocalDateTime`.
 * No es el draft de dominio: el borde traduce con [FinishWorkoutDraft.toRequest].
 */
@Serializable
data class FinishWorkoutRequest(
    val routineId: Long?,
    val workoutId: Long?,
    val name: String,
    val durationMinutes: Int,
    val startTime: String,
    val endTime: String,
    val notes: String,
    val exercises: List<FinishExerciseRequest>,
)

@Serializable
data class FinishExerciseRequest(
    val exerciseMasterId: Long,
    val notes: String?,
    val sets: List<FinishSetRequest>,
)

@Serializable
data class FinishSetRequest(
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rpe: Int?,
)

fun FinishWorkoutDraft.toRequest(): FinishWorkoutRequest = FinishWorkoutRequest(
    routineId = routineId,
    workoutId = workoutId,
    name = name,
    durationMinutes = durationMinutes,
    startTime = startTime.toString(),
    endTime = endTime.toString(),
    notes = notes,
    exercises = exercises.map { it.toRequest() },
)

fun FinishExerciseDraft.toRequest(): FinishExerciseRequest = FinishExerciseRequest(
    exerciseMasterId = exerciseMasterId,
    notes = notes,
    sets = sets.map { it.toRequest() },
)

fun FinishSetDraft.toRequest(): FinishSetRequest = FinishSetRequest(
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rpe = rpe,
)
