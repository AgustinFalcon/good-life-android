package com.agusstkd.goodlife.presentation.screen.tabs.workout.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import kotlinx.datetime.DayOfWeek

/**
 * Estado de UI para la pantalla Workouts (tab de entrenamiento).
 *
 * Define los diferentes estados posibles:
 * - [Loading]:  Cargando datos del backend (muestra skeleton)
 * - [NoRoutine]: El usuario no tiene ninguna rutina activa
 * - [Success]:  Rutina activa encontrada con sus workouts
 * - [Error]:    Error técnico al cargar datos
 */
@Stable
sealed interface WorkoutsUiState {

    /**
     * Estado de carga inicial.
     * Muestra skeleton/shimmer mientras se consulta el backend.
     */
    @Immutable
    data object Loading : WorkoutsUiState

    /**
     * El usuario no tiene una rutina activa.
     *
     * No es un error — simplemente no hay rutina activada todavía.
     * La UI debe mostrar un CTA para crear o activar una rutina.
     */
    @Immutable
    data object NoRoutine : WorkoutsUiState

    /**
     * Estado principal con la rutina activa cargada.
     *
     * @property routineName Nombre de la rutina activa
     * @property routineDescription Descripción opcional de la rutina
     * @property difficultyLabel Nivel de dificultad formateado (ej: "Intermedio")
     * @property goalLabel Objetivo formateado (ej: "Ganancia muscular")
     * @property daysOfWeek Días de la semana programados
     * @property workouts Lista de workouts de la rutina
     */
    @Immutable
    data class Success(
        val routineName: String,
        val routineDescription: String?,
        val difficultyLabel: String,
        val goalLabel: String,
        val daysOfWeek: Set<DayOfWeek>,
        val workouts: List<WorkoutUiModel>,
    ) : WorkoutsUiState

    /**
     * Error técnico (red o servidor).
     *
     * El mensaje ya viene formateado desde el ViewModel usando [AppLanguage],
     * listo para mostrar al usuario.
     *
     * @property message Mensaje de error para mostrar al usuario.
     */
    @Immutable
    data class Error(val message: String) : WorkoutsUiState
}

/**
 * Modelo UI de un workout dentro de la rutina activa.
 *
 * @property id ID del workout
 * @property name Nombre del workout (ej: "Piernas", "Push")
 * @property dayOfWeek Día de la semana asignado (1=Lun … 7=Dom), o null si no tiene día fijo
 * @property exerciseCount Cantidad de ejercicios en el workout
 * @property totalSets Cantidad total de series del workout
 */
@Immutable
data class WorkoutUiModel(
    val id: Long,
    val name: String,
    val dayOfWeek: Int?,
    val exerciseCount: Int,
    val totalSets: Int,
)
