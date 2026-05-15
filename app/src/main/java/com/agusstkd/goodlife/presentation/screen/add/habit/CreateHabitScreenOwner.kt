package com.agusstkd.goodlife.presentation.screen.add.habit

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
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiAction
import com.agusstkd.goodlife.presentation.screen.add.habit.model.HabitDatePickerField
import org.koin.androidx.compose.koinViewModel

/**
 * Owner composable que orquesta la pantalla de creación de hábito.
 *
 * Responsabilidades:
 * - Inyecta el [CreateHabitViewModel] via Koin.
 * - Renderiza [CreateHabitScreen] (formulario puro).
 * - Muestra [DatePickerBottomSheetComponent] cuando [CreateHabitUiState.activeDatePickerField] != null.
 * - Muestra [TimePickerDialogComponent] cuando [CreateHabitUiState.showTimePicker] es true.
 * - Muestra el [PopupResultComponent] cuando [CreateHabitUiState.isLoading], [CreateHabitUiState.isSuccess]
 *   o [CreateHabitUiState.errorMessage] están activos, cubriendo los tres estados del flujo de creación.
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

        // ── 4. Popup de resultado (loading / success / error) ────────────
        val showPopup = uiState.isLoading || uiState.isSuccess || uiState.errorMessage != null
        if (showPopup) {
            PopupResultComponent(
                params = PopupResultParams(
                    state = when {
                        uiState.isLoading -> PopupResultState.LOADING
                        uiState.isSuccess -> PopupResultState.SUCCESS
                        else              -> PopupResultState.ERROR
                    },
                    itemType = CreationItemType.HABIT,
                    title = when {
                        uiState.isLoading -> viewModel.createHabitTexts.loadingTitle
                        uiState.isSuccess -> viewModel.createHabitTexts.successTitle
                        else              -> viewModel.sharedTexts.errorTitle
                    },
                    message = when {
                        uiState.isLoading -> viewModel.sharedTexts.loadingMessage
                        uiState.isSuccess -> uiState.name
                        else              -> uiState.errorMessage ?: ""
                    },
                    detail = if (uiState.isSuccess) {
                        uiState.startDateDisplay.ifEmpty { null }
                    } else null,
                    loadingProgress = null,
                    retryLabel = viewModel.sharedTexts.retryLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onSuccess = { viewModel.onAction(CreateHabitUiAction.OnSuccessAnimationFinished) },
                onRetry   = { viewModel.onAction(CreateHabitUiAction.OnSubmit) },
                onCancel  = { viewModel.onAction(CreateHabitUiAction.OnErrorDismissed) },
            )
        }
    }
}
