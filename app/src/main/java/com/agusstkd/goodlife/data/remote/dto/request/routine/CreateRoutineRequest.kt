package com.agusstkd.goodlife.data.remote.dto.request.routine

import kotlinx.serialization.Serializable

/**
 * DTO para `POST /api/v1/routines`.
 *
 * Estructura anidada: Routine → Workouts → Exercises → Sets.
 * Todo se envía en una sola llamada HTTP.
 *
 * Los campos de fecha/hora son strings ISO porque el backend los parsea así.
 * Los enums se envían como su `.name` (ej: "INTERMEDIATE", "MUSCLE_GAIN").
 */
@Serializable
data class CreateRoutineRequest(
    val name: String,
    val description: String?,
    val difficultyLevel: String,
    val goalType: String,
    val scheduledTime: String?,
    val daysOfWeek: List<Int>,
    val startDate: String?,
    val endDate: String?,
    val workouts: List<CreateWorkoutRequest>,
)

@Serializable
data class CreateWorkoutRequest(
    val name: String,
    val exercises: List<CreateWorkoutExerciseRequest>,
)

@Serializable
data class CreateWorkoutExerciseRequest(
    val exerciseId: Long,
    val orderIndex: Int,
    val notes: String?,
    val sets: List<CreateWorkoutSetRequest>,
)

@Serializable
data class CreateWorkoutSetRequest(
    val setNumber: Int,
    val targetReps: Int,
    val targetWeight: Double?,
)
