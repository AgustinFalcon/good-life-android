package com.agusstkd.goodlife.presentation.screen.add.habit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.common.SnackbarComponent
import com.agusstkd.goodlife.presentation.components.common.SnackbarParams
import com.agusstkd.goodlife.presentation.components.common.SnackbarVariant
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialog
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialogParams
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogComponent
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogParams
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiAction
import com.agusstkd.goodlife.presentation.screen.add.habit.model.HabitDatePickerField
import org.koin.androidx.compose.koinViewModel

/**
 * Owner composable que orquesta la pantalla de creación de hábito.
 *
 * Responsabilidades:
 * - Inyecta el [CreateHabitViewModel] via Koin (único koinViewModel).
 * - Renderiza [CreateHabitScreen] (formulario puro).
 * - Muestra [DatePickerBottomSheetComponent] cuando [CreateHabitUiState.activeDatePickerField] != null.
 * - Muestra [TimePickerDialogComponent] cuando [CreateHabitUiState.showTimePicker] es true.
 * - Muestra [SuccessDialog] con Lottie cuando [CreateHabitUiState.isSuccess] es true.
 * - Muestra [SnackbarComponent] de error cuando [CreateHabitUiState.errorMessage] != null.
 */
@Composable
fun CreateHabitScreenOwner(
    viewModel: CreateHabitViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Formulario ────────────────────────────────────────────────
        CreateHabitScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            createHabitTexts = viewModel.createHabitTexts,
            sharedTexts = viewModel.sharedTexts,
            categoryEntries = viewModel.categoryEntries,
            dayNames = viewModel.dayNames,
            closeContentDescription = viewModel.accessibilityTexts.close,
        )

        // ── 2. DatePicker bottom sheet ───────────────────────────────────
        uiState.activeDatePickerField?.let { field ->
            DatePickerBottomSheetComponent(
                texts = viewModel.datePickerTexts,
                initialDate = when (field) {
                    HabitDatePickerField.START_DATE -> uiState.startDate
                    HabitDatePickerField.END_DATE -> uiState.endDate
                },
                onDateSelected = { date ->
                    viewModel.onAction(CreateHabitUiAction.OnDateSelected(field, date))
                },
                onDismiss = {
                    viewModel.onAction(CreateHabitUiAction.OnDatePickerDismiss)
                },
            )
        }

        // ── 3. TimePicker dialog ─────────────────────────────────────────
        if (uiState.showTimePicker) {
            TimePickerDialogComponent(
                params = TimePickerDialogParams(
                    initialHour = uiState.scheduledTime?.hour ?: 9,
                    initialMinute = uiState.scheduledTime?.minute ?: 0,
                    confirmLabel = viewModel.sharedTexts.confirmLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onTimeSelected = { time ->
                    viewModel.onAction(CreateHabitUiAction.OnTimeSelected(time))
                },
                onDismiss = {
                    viewModel.onAction(CreateHabitUiAction.OnTimePickerDismiss)
                },
            )
        }

        // ── 4. Success dialog (Lottie) ───────────────────────────────────
        if (uiState.isSuccess) {
            SuccessDialog(
                params = SuccessDialogParams(
                    title = viewModel.createHabitTexts.successTitle,
                    subtitle = uiState.name,
                    detail = uiState.startDateDisplay.ifEmpty { null },
                ),
                onAnimationFinished = {
                    viewModel.onAction(CreateHabitUiAction.OnSuccessAnimationFinished)
                },
            )
        }

        // ── 5. Snackbar de error ─────────────────────────────────────────
        uiState.errorMessage?.let { error ->
            SnackbarComponent(
                params = SnackbarParams(
                    message = error,
                    variant = SnackbarVariant.ERROR,
                    actionLabel = viewModel.sharedTexts.retryLabel,
                    autoDismissMs = 6_000L,
                ),
                isVisible = true,
                onDismiss = {
                    viewModel.onAction(CreateHabitUiAction.OnErrorDismissed)
                },
                onActionClick = {
                    viewModel.onAction(CreateHabitUiAction.OnSubmit)
                },
            )
        }
    }
}
