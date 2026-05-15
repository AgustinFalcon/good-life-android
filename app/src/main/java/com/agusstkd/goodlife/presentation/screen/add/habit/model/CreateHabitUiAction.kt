package com.agusstkd.goodlife.presentation.screen.add.habit.model

import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Acciones del usuario en la pantalla de creación de hábito.
 *
 * Cada subclase es un evento discreto que el ViewModel procesa
 * actualizando [CreateHabitUiState].
 *
 * @see CreateHabitUiState
 */
sealed interface CreateHabitUiAction {

    // Campos de texto
    data class OnNameChange(val name: String) : CreateHabitUiAction
    data class OnDescriptionChange(val description: String) : CreateHabitUiAction

    // Categoría
    data class OnCategorySelected(val category: HabitCategory) : CreateHabitUiAction

    // Meta numérica
    data class OnTargetValueChange(val value: String) : CreateHabitUiAction
    data class OnUnitChange(val unit: String) : CreateHabitUiAction

    // Días de la semana
    data class OnDayToggled(val day: DayOfWeek) : CreateHabitUiAction

    // Toggles
    data object OnHasEndDateToggle : CreateHabitUiAction
    data object OnHasTimeToggle : CreateHabitUiAction

    // DatePicker
    data class OnDatePickerOpen(val field: HabitDatePickerField) : CreateHabitUiAction
    data object OnDatePickerDismiss : CreateHabitUiAction
    data class OnDateSelected(val field: HabitDatePickerField, val date: LocalDate) : CreateHabitUiAction

    // TimePicker
    data object OnTimePickerOpen : CreateHabitUiAction
    data object OnTimePickerDismiss : CreateHabitUiAction
    data class OnTimeSelected(val time: LocalTime) : CreateHabitUiAction

    // Envío y cierre
    data object OnSubmit : CreateHabitUiAction
    data object OnErrorDismissed : CreateHabitUiAction
    data object OnSuccessAnimationFinished : CreateHabitUiAction
    data object OnDismiss : CreateHabitUiAction
}
