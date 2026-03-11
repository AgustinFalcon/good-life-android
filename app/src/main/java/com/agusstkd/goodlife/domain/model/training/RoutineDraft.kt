package com.agusstkd.goodlife.domain.model.training

/**
 * Modelos "draft" que representan los datos de una rutina ANTES de ser
 * enviada al backend (sin IDs, sin timestamps).
 *
 * Se usan como parámetros de [RoutineRepository.createRoutine] y en el
 * UiState del wizard de creación.
 *
 * La jerarquía: [WorkoutDraft] → [ExerciseDraft] → [SetDraft]
 */

data class WorkoutDraft(
    val name: String,
    val exercises: List<ExerciseDraft> = emptyList(),
)

data class ExerciseDraft(
    val exerciseId: Long,
    val exerciseName: String,
    val imageUrl: String?,
    val muscleGroupName: String,
    val orderIndex: Int,
    val notes: String? = null,
    val sets: List<SetDraft> = listOf(SetDraft()),
)

data class SetDraft(
    val setNumber: Int = 1,
    val targetReps: Int = 10,
    val targetWeight: Double? = null,
)
