package com.agusstkd.goodlife.presentation.screen.add.habit.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Estado de UI para la pantalla de creación de hábito.
 *
 * A diferencia de [CreateTaskUiState], no tiene modo ONCE/RECURRENT porque
 * un hábito siempre es recurrente. Agrega [selectedCategory], [targetValue]
 * y [unit] para la meta numérica.
 *
 * ## Control de pickers:
 * - [activeDatePickerField]: qué bottom sheet de calendario está abierto.
 * - [showTimePicker]: si el dialog de hora está visible.
 *
 * @see CreateHabitUiAction
 * @see HabitDatePickerField
 */
@Stable
data class CreateHabitUiState(
    val name: String = "",
    val description: String = "",

    // Categoría
    val selectedCategory: HabitCategory? = null,

    // Meta numérica
    val targetValue: String = "",
    val unit: String = "",

    // Scheduling (siempre recurrente)
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",

    // Hora opcional
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,
    val showTimePicker: Boolean = false,

    // Control del DatePicker
    val activeDatePickerField: HabitDatePickerField? = null,

    // Estado de la operación
    val hasAttemptedSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Campos de fecha editables en el formulario de hábito.
 *
 * A diferencia de Task, no hay SCHEDULED_DATE porque
 * el hábito siempre es recurrente.
 */
enum class HabitDatePickerField {
    START_DATE,
    END_DATE,
}
