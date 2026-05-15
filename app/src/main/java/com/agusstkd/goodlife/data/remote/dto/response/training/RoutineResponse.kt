package com.agusstkd.goodlife.data.remote.dto.response.training

import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.Workout
import com.agusstkd.goodlife.domain.model.training.WorkoutExercise
import com.agusstkd.goodlife.domain.model.training.WorkoutSet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RoutineResponse(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    val difficultyLevel: String? = null,
    val goalType: String? = null,
    val isActive: Boolean = false,
    val scheduledTime: String? = null,
    val daysOfWeek: List<Int>? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val workouts: List<WorkoutResponse>? = null,
)

@Serializable
data class WorkoutResponse(
    val id: Long,
    val name: String? = null,
    val dayOfWeek: Int? = null,
    val exercises: List<WorkoutExerciseResponse>? = null,
)

@Serializable
data class WorkoutExerciseResponse(
    val id: Long,
    val exerciseId: Long? = null,
    val exerciseName: String? = null,
    val imageUrl: String? = null,
    val orderIndex: Int? = null,
    val notes: String? = null,
    val sets: List<WorkoutSetResponse>? = null,
)

@Serializable
data class WorkoutSetResponse(
    val id: Long,
    val setNumber: Int? = null,
    val targetReps: Int? = null,
    val targetWeight: Double? = null,
)

// ── Mappers DTO → Domain ──

fun RoutineResponse.toDomain(): Routine = Routine(
    id = id,
    name = name.orEmpty(),
    description = description,
    difficultyLevel = runCatching { DifficultyLevel.valueOf(difficultyLevel.orEmpty()) }
        .getOrDefault(DifficultyLevel.BEGINNER),
    goalType = runCatching { GoalType.valueOf(goalType.orEmpty()) }
        .getOrDefault(GoalType.STRENGTH),
    isActive = isActive,
    scheduledTime = scheduledTime?.let { runCatching { LocalTime.parse(it) }.getOrNull() },
    daysOfWeek = daysOfWeek?.map { DayOfWeek(it) }?.toSet() ?: emptySet(),
    startDate = startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    endDate = endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    workouts = workouts?.map { it.toDomain() } ?: emptyList(),
)

fun WorkoutResponse.toDomain(): Workout = Workout(
    id = id,
    name = name.orEmpty(),
    dayOfWeek = dayOfWeek,
    exercises = exercises?.map { it.toDomain() } ?: emptyList(),
)

fun WorkoutExerciseResponse.toDomain(): WorkoutExercise = WorkoutExercise(
    id = id,
    exerciseId = exerciseId ?: 0L,
    exerciseName = exerciseName.orEmpty(),
    imageUrl = imageUrl,
    orderIndex = orderIndex ?: 0,
    notes = notes,
    sets = sets?.map { it.toDomain() } ?: emptyList(),
)

fun WorkoutSetResponse.toDomain(): WorkoutSet = WorkoutSet(
    id = id,
    setNumber = setNumber ?: 0,
    targetReps = targetReps ?: 0,
    targetWeight = targetWeight,
)
