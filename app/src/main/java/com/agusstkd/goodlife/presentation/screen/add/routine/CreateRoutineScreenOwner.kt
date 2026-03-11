package com.agusstkd.goodlife.presentation.screen.add.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.common.SnackbarComponent
import com.agusstkd.goodlife.presentation.components.common.SnackbarParams
import com.agusstkd.goodlife.presentation.components.common.SnackbarVariant
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialog
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialogParams
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogComponent
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogParams
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineDatePickerField
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

        // ── 6. Success dialog (Lottie) ──
        if (uiState.isSuccess) {
            SuccessDialog(
                params = SuccessDialogParams(
                    title = viewModel.routineTexts.successTitle,
                    subtitle = uiState.name,
                    detail = uiState.startDateDisplay.ifEmpty { null },
                ),
                onAnimationFinished = {
                    viewModel.onAction(CreateRoutineUiAction.OnSuccessAnimationFinished)
                },
            )
        }

        // ── 7. Snackbar de error ──
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
                    viewModel.onAction(CreateRoutineUiAction.OnErrorDismissed)
                },
                onActionClick = {
                    viewModel.onAction(CreateRoutineUiAction.OnSubmit)
                },
            )
        }
    }
}

@Composable
private fun AddWorkoutDialog(
    placeholder: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelLabel)
            }
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetEditorBottomSheet(
    exerciseName: String,
    muscleGroupName: String,
    initialSets: List<SetDraft>,
    initialNotes: String?,
    routineTexts: com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts,
    sharedTexts: com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts,
    onConfirm: (List<SetDraft>, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sets = remember { mutableStateListOf(*initialSets.toTypedArray()) }
    var notes by remember { mutableStateOf(initialNotes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // Header
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            Text(
                text = muscleGroupName,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sets table header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("#", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelMedium)
                Text(routineTexts.repsLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text(routineTexts.weightLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets rows
            sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.width(32.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedTextField(
                        value = set.targetReps.toString(),
                        onValueChange = { value ->
                            val reps = value.toIntOrNull() ?: 0
                            sets[index] = set.copy(targetReps = reps)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = set.targetWeight?.toString() ?: "",
                        onValueChange = { value ->
                            val weight = value.toDoubleOrNull()
                            sets[index] = set.copy(targetWeight = weight)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                    if (sets.size > 1) {
                        IconButton(onClick = { sets.removeAt(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = ErrorRed,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add set
            OutlinedButton(
                onClick = {
                    val nextNumber = sets.size + 1
                    sets.add(SetDraft(setNumber = nextNumber))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(routineTexts.addSetButton)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(routineTexts.notesPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(sharedTexts.cancelLabel)
                }
                TextButton(
                    onClick = {
                        val numberedSets = sets.mapIndexed { i, s ->
                            s.copy(setNumber = i + 1)
                        }
                        onConfirm(numberedSets, notes.takeIf { it.isNotBlank() })
                    },
                    modifier = Modifier.weight(1f),
                    enabled = sets.isNotEmpty() && sets.all { it.targetReps > 0 },
                ) {
                    Text(sharedTexts.confirmLabel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
