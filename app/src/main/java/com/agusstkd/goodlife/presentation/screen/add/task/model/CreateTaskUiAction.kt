package com.agusstkd.goodlife.presentation.screen.add.task.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Acciones posibles en la pantalla de creación de tarea.
 *
 * Cada subclase representa un evento del usuario. El ViewModel recibe
 * estas acciones via [CreateTaskViewModel.onAction] y actualiza
 * [CreateTaskUiState] en consecuencia.
 *
 * ## Flujo del DatePicker:
 * 1. Usuario toca un campo de fecha → dispara [OnDatePickerOpen]
 * 2. ViewModel setea [CreateTaskUiState.activeDatePickerField] != null
 * 3. La Screen detecta el campo activo y muestra [DatePickerBottomSheetComponent]
 * 4a. Usuario confirma fecha → dispara [OnDateSelected]
 * 4b. Usuario cancela/cierra → dispara [OnDatePickerDismiss]
 * 5. ViewModel resetea [CreateTaskUiState.activeDatePickerField] a null
 *
 * ## Flujo de envío:
 * [OnSubmit] → ViewModel valida → llama UseCase → actualiza [CreateTaskUiState.isSuccess]
 * → Owner detecta isSuccess y cierra el formulario via [OnDismiss].
 *
 * @see CreateTaskUiState
 * @see CreateTaskViewModel
 */
sealed interface CreateTaskUiAction {

    // Campos de texto
    data class OnTitleChange(val title: String) : CreateTaskUiAction
    data class OnDescriptionChange(val description: String) : CreateTaskUiAction

    // Modo de scheduling (Una vez / Se repite)
    data class OnSchedulingModeChange(val mode: SchedulingMode) : CreateTaskUiAction

    // Días de la semana — toggle individual (toca Lun → activa/desactiva Lun)
    data class OnDayToggled(val day: DayOfWeek) : CreateTaskUiAction

    // Switches
    data object OnHasEndDateToggle : CreateTaskUiAction
    data object OnHasTimeToggle : CreateTaskUiAction
    data class OnReminderChange(val enabled: Boolean) : CreateTaskUiAction

    // Control del DatePicker
    data class OnDatePickerOpen(val field: DatePickerField) : CreateTaskUiAction
    data object OnDatePickerDismiss : CreateTaskUiAction

    // Fecha confirmada desde el DatePicker (field indica cuál de los 3)
    data class OnDateSelected(val field: DatePickerField, val date: LocalDate) : CreateTaskUiAction

    // Control del TimePicker
    data object OnTimePickerOpen : CreateTaskUiAction
    data object OnTimePickerDismiss : CreateTaskUiAction

    // Hora confirmada desde el TimePicker
    data class OnTimeSelected(val time: LocalTime) : CreateTaskUiAction

    // Envío del formulario
    data object OnSubmit : CreateTaskUiAction

    // El snackbar de error se cerró (auto o manual)
    data object OnErrorDismissed : CreateTaskUiAction

    // La animación Lottie de éxito terminó → navegar de vuelta
    data object OnSuccessAnimationFinished : CreateTaskUiAction

    // Cierre del formulario completo
    data object OnDismiss : CreateTaskUiAction
}
