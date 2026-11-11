package com.agusstkd.goodlife.presentation.screen.add.routine

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
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineDatePickerField
import org.koin.androidx.compose.koinViewModel

/**
 * Owner composable que orquesta el wizard de creación de rutina.
 *
 * Responsabilidades:
 * - Inyecta el [CreateRoutineViewModel] via Koin.
 * - Renderiza [CreateRoutineScreen] (wizard de pasos puro).
 * - Muestra [DatePickerBottomSheetComponent] cuando [CreateRoutineUiState.activeDatePickerField] != null.
 * - Muestra [TimePickerDialogComponent] cuando [CreateRoutineUiState.showTimePicker] es true.
 * - Muestra [AddWorkoutDialog] cuando [CreateRoutineUiState.showAddWorkoutDialog] es true.
 * - Muestra [SetEditorBottomSheet] cuando [CreateRoutineUiState.showSetEditorForExerciseIndex] != null.
 * - Muestra el [PopupResultComponent] cuando [CreateRoutineUiState.isLoading], [CreateRoutineUiState.isSuccess]
 *   o [CreateRoutineUiState.errorMessage] están activos, cubriendo los tres estados del flujo de creación.
 */
@Composable
fun CreateRoutineScreenOwner(
    viewModel: CreateRoutineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Pantalla principal del wizard ──
        CreateRoutineScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            routineTexts = viewModel.routineTexts,
            sharedTexts = viewModel.sharedTexts,
            difficultyEntries = viewModel.difficultyEntries,
            goalEntries = viewModel.goalEntries,
            dayNames = viewModel.dayNames,
            closeContentDescription = viewModel.accessibilityTexts.close,
        )

        // ── 2. DatePicker bottom sheet ──
        uiState.activeDatePickerField?.let { field ->
            DatePickerBottomSheetComponent(
                texts = viewModel.datePickerTexts,
                initialDate = when (field) {
                    RoutineDatePickerField.START_DATE -> uiState.startDate
                    RoutineDatePickerField.END_DATE -> uiState.endDate
                },
                onDateSelected = { date ->
                    viewModel.onAction(CreateRoutineUiAction.OnDateSelected(field, date))
                },
                onDismiss = {
                    viewModel.onAction(CreateRoutineUiAction.OnDatePickerDismiss)
                },
            )
        }

        // ── 3. TimePicker dialog ──
        if (uiState.showTimePicker) {
            TimePickerDialogComponent(
                params = TimePickerDialogParams(
                    initialHour = uiState.scheduledTime?.hour ?: 9,
                    initialMinute = uiState.scheduledTime?.minute ?: 0,
                    confirmLabel = viewModel.sharedTexts.confirmLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onTimeSelected = { time ->
                    viewModel.onAction(CreateRoutineUiAction.OnTimeSelected(time))
                },
                onDismiss = {
                    viewModel.onAction(CreateRoutineUiAction.OnTimePickerDismiss)
                },
            )
        }

        // ── 4. Add Workout Dialog ──
        if (uiState.showAddWorkoutDialog) {
            AddWorkoutDialog(
                placeholder = viewModel.routineTexts.workoutNamePlaceholder,
                confirmLabel = viewModel.sharedTexts.confirmLabel,
                cancelLabel = viewModel.sharedTexts.cancelLabel,
                onConfirm = { name ->
                    viewModel.onAction(CreateRoutineUiAction.OnAddWorkout(name))
                },
                onDismiss = {
                    viewModel.onAction(CreateRoutineUiAction.OnDismissAddWorkoutDialog)
                },
            )
        }

        // ── 5. Set Editor Bottom Sheet ──
        uiState.showSetEditorForExerciseIndex?.let { orderIndex ->
            val workout = uiState.activeWorkout
            val exercise = workout?.exercises?.firstOrNull { it.orderIndex == orderIndex }
            if (exercise != null) {
                SetEditorBottomSheet(
                    exerciseName = exercise.exerciseName,
                    muscleGroupName = exercise.muscleGroupName,
                    initialSets = exercise.sets,
                    initialNotes = exercise.notes,
                    routineTexts = viewModel.routineTexts,
                    sharedTexts = viewModel.sharedTexts,
                    onConfirm = { sets, notes ->
                        viewModel.onAction(
                            CreateRoutineUiAction.OnSaveExerciseSets(orderIndex, sets, notes)
                        )
                    },
                    onDismiss = {
                        viewModel.onAction(CreateRoutineUiAction.OnDismissSetEditor)
                    },
                )
            }
        }

        // ── 6. Popup de resultado (loading / success / error) ────────────
        val showPopup = uiState.isLoading || uiState.isSuccess || uiState.errorMessage != null
        if (showPopup) {
            PopupResultComponent(
                params = PopupResultParams(
                    state = when {
                        uiState.isLoading -> PopupResultState.LOADING
                        uiState.isSuccess -> PopupResultState.SUCCESS
                        else              -> PopupResultState.ERROR
                    },
                    itemType = CreationItemType.WORKOUT,
                    title = when {
                        uiState.isLoading -> viewModel.routineTexts.loadingTitle
                        uiState.isSuccess -> viewModel.routineTexts.successTitle
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
                onSuccess = { viewModel.onAction(CreateRoutineUiAction.OnSuccessAnimationFinished) },
                onRetry   = { viewModel.onAction(CreateRoutineUiAction.OnSubmit) },
                onCancel  = { viewModel.onAction(CreateRoutineUiAction.OnErrorDismissed) },
            )
        }
    }
}
