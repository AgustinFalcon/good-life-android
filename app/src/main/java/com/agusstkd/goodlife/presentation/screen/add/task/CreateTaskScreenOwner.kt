package com.agusstkd.goodlife.presentation.screen.add.task

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
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiAction
import com.agusstkd.goodlife.presentation.screen.add.task.model.DatePickerField
import org.koin.androidx.compose.koinViewModel

/**
 * Owner composable que orquesta la pantalla de creación de tarea.
 *
 * Responsabilidades:
 * - Inyecta el [CreateTaskViewModel] via Koin.
 * - Renderiza [CreateTaskScreen] (formulario puro).
 * - Muestra el [DatePickerBottomSheetComponent] cuando [CreateTaskUiState.activeDatePickerField] != null.
 * - Muestra el [TimePickerDialogComponent] cuando [CreateTaskUiState.showTimePicker] es true.
 * - Muestra el [SuccessDialog] con Lottie cuando [CreateTaskUiState.isSuccess] es true.
 * - Muestra el [SnackbarComponent] de error cuando [CreateTaskUiState.errorMessage] != null.
 */
@Composable
fun CreateTaskScreenOwner(
    viewModel: CreateTaskViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Formulario ────────────────────────────────────────────────
        CreateTaskScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            createTaskTexts = viewModel.createTaskTexts,
            sharedTexts = viewModel.sharedTexts,
            dayNames = viewModel.dayNames,
            closeContentDescription = viewModel.accessibilityTexts.close,
        )

        // ── 2. DatePicker bottom sheet ───────────────────────────────────
        uiState.activeDatePickerField?.let { field ->
            DatePickerBottomSheetComponent(
                texts = viewModel.datePickerTexts,
                initialDate = when (field) {
                    DatePickerField.SCHEDULED_DATE -> uiState.scheduledDate
                    DatePickerField.START_DATE -> uiState.startDate
                    DatePickerField.END_DATE -> uiState.endDate
                },
                onDateSelected = { date ->
                    viewModel.onAction(CreateTaskUiAction.OnDateSelected(field, date))
                },
                onDismiss = {
                    viewModel.onAction(CreateTaskUiAction.OnDatePickerDismiss)
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
                    viewModel.onAction(CreateTaskUiAction.OnTimeSelected(time))
                },
                onDismiss = {
                    viewModel.onAction(CreateTaskUiAction.OnTimePickerDismiss)
                },
            )
        }

        // ── 4. Success dialog (Lottie) ───────────────────────────────────
        if (uiState.isSuccess) {
            SuccessDialog(
                params = SuccessDialogParams(
                    title = viewModel.createTaskTexts.successTitle,
                    subtitle = uiState.title,
                    detail = uiState.scheduledDateDisplay.ifEmpty {
                        uiState.startDateDisplay
                    }.ifEmpty { null },
                ),
                onAnimationFinished = {
                    viewModel.onAction(CreateTaskUiAction.OnSuccessAnimationFinished)
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
                    viewModel.onAction(CreateTaskUiAction.OnErrorDismissed)
                },
                onActionClick = {
                    viewModel.onAction(CreateTaskUiAction.OnSubmit)
                },
            )
        }
    }
}
