package com.agusstkd.goodlife.presentation.screen.add.task.model

import androidx.compose.runtime.Stable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Estado de UI para la pantalla de creación de tarea.
 *
 * Es un [data class] simple (no sealed) porque el formulario siempre
 * está en modo editable — [isLoading] e [isSuccess] son flags dentro
 * del mismo estado, no variantes estructuralmente distintas.
 *
 * ## Modos de scheduling:
 * - [SchedulingMode.ONCE]: tarea puntual para [scheduledDate]
 * - [SchedulingMode.RECURRENT]: tarea repetida en [recurrentDays] desde
 *   [startDate] hasta [endDate] (opcional si [hasEndDate] es false)
 *
 * ## Control del DatePicker:
 * [activeDatePickerField] determina qué bottom sheet de calendario está
 * abierto. `null` = cerrado. Cada valor mapea a un campo de fecha distinto.
 *
 * @see CreateTaskUiAction
 * @see DatePickerField
 * @see SchedulingMode
 */
@Stable
data class CreateTaskUiState(
    // Formulario básico
    val title: String = "",
    val description: String = "",

    // Modo de scheduling
    val schedulingMode: SchedulingMode = SchedulingMode.ONCE,

    // Modo ONCE — una fecha puntual
    val scheduledDate: LocalDate? = null,
    val scheduledDateDisplay: String = "",

    // Modo RECURRENT — días + rango
    val recurrentDays: Set<DayOfWeek> = emptySet(),
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",

    // Hora (aplica a ambos modos)
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,
    val showTimePicker: Boolean = false,

    // Recordatorio
    val reminderEnabled: Boolean = false,

    // Control del DatePicker — cuál campo está abierto
    val activeDatePickerField: DatePickerField? = null,

    // Estado de la operación
    val hasAttemptedSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

enum class SchedulingMode { ONCE, RECURRENT }

enum class DatePickerField {
    SCHEDULED_DATE,
    START_DATE,
    END_DATE,
}
