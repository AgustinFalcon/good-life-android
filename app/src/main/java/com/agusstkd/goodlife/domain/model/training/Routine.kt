package com.agusstkd.goodlife.domain.model.training

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Routine(
    val id: Long,
    val name: String,
    val description: String?,
    val difficultyLevel: DifficultyLevel,
    val goalType: GoalType,
    val isActive: Boolean,
    val scheduledTime: LocalTime?,
    val daysOfWeek: Set<DayOfWeek>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val workouts: List<Workout>,
)

data class Workout(
    val id: Long,
    val name: String,
    val dayOfWeek: Int?,
    val exercises: List<WorkoutExercise>,
)

data class WorkoutExercise(
    val id: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val imageUrl: String?,
    val orderIndex: Int,
    val notes: String?,
    val sets: List<WorkoutSet>,
)

data class WorkoutSet(
    val id: Long,
    val setNumber: Int,
    val targetReps: Int,
    val targetWeight: Double?,
)
