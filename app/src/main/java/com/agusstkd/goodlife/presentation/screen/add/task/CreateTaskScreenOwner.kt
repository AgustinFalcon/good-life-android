package com.agusstkd.goodlife.presentation.screen.add.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.dialog.CreationItemType
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultComponent
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultParams
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultState
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
 * - Muestra el [PopupResultComponent] cuando [CreateTaskUiState.isLoading], [CreateTaskUiState.isSuccess]
 *   o [CreateTaskUiState.errorMessage] están activos, cubriendo los tres estados del flujo de creación.
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

        // ── 4. Popup de resultado (loading / success / error) ────────────
        val showPopup = uiState.isLoading || uiState.isSuccess || uiState.errorMessage != null
        if (showPopup) {
            PopupResultComponent(
                params = PopupResultParams(
                    state = when {
                        uiState.isLoading -> PopupResultState.LOADING
                        uiState.isSuccess -> PopupResultState.SUCCESS
                        else -> PopupResultState.ERROR
                    },
                    itemType = CreationItemType.TASK,
                    title = when {
                        uiState.isLoading -> viewModel.createTaskTexts.loadingTitle
                        uiState.isSuccess -> viewModel.createTaskTexts.successTitle
                        else -> viewModel.sharedTexts.errorTitle
                    },
                    message = when {
                        uiState.isLoading -> viewModel.sharedTexts.loadingMessage
                        uiState.isSuccess -> uiState.title // title task
                        else -> uiState.errorMessage ?: ""
                    },
                    detail = if (uiState.isSuccess) {
                        uiState.scheduledDateDisplay.ifEmpty { uiState.startDateDisplay }.ifEmpty { null }
                    } else null,
                    loadingProgress = null,
                    retryLabel = viewModel.sharedTexts.retryLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onSuccess = { viewModel.onAction(CreateTaskUiAction.OnSuccessAnimationFinished) },
                onRetry = { viewModel.onAction(CreateTaskUiAction.OnSubmit) },
                onCancel = { viewModel.onAction(CreateTaskUiAction.OnErrorDismissed) },
            )
        }
    }
}
