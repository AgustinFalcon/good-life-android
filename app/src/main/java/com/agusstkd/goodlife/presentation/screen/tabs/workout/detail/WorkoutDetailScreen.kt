package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.datetime.language.WorkoutTexts
import com.agusstkd.goodlife.core.extensions.formatArgs
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialog
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialogParams
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutExerciseUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetField
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetUiModel
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

@Composable
fun WorkoutDetailScreen(
    state: WorkoutDetailUiState.Content,
    texts: WorkoutTexts,
    onNavigateUp: () -> Unit,
    onAction: (WorkoutDetailUiAction) -> Unit,
) {
    val fieldsEnabled = !state.finished && !state.isFinishing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.back)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.workoutName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    state.dayLabel?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        item {
            Text(
                text = texts.exercisesTitle,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        if (state.exercises.isEmpty()) {
            item {
                Text(
                    text = texts.noExercises,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(state.exercises, key = { it.id }) { exercise ->
                WorkoutExerciseCard(
                    exercise = exercise,
                    texts = texts,
                    enabled = fieldsEnabled,
                    onAction = onAction,
                )
            }
        }
        item {
            Text(
                text = texts.notesLabel,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        item {
            TextFieldComponent(
                params = TextFieldParams(
                    value = state.notes,
                    placeholder = texts.notesPlaceholder,
                    type = TextFieldType.MULTILINE,
                    minLines = 2,
                    maxLines = 4,
                    enabled = fieldsEnabled,
                ),
                onValueChange = { onAction(WorkoutDetailUiAction.OnNotesChange(it)) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
        state.finishError?.let { error ->
            item {
                Text(
                    text = error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            Button(
                onClick = { onAction(WorkoutDetailUiAction.OnFinishWorkout) },
                enabled = state.canFinish && fieldsEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(texts.finishWorkout)
            }
        }
    }

    if (state.finished) {
        SuccessDialog(
            params = SuccessDialogParams(
                title = texts.workoutFinishedTitle,
                subtitle = state.workoutName,
            ),
            onAnimationFinished = onNavigateUp,
        )
    }
}

@Composable
private fun WorkoutExerciseCard(
    exercise: WorkoutExerciseUiModel,
    texts: WorkoutTexts,
    enabled: Boolean,
    onAction: (WorkoutDetailUiAction) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            exercise.notes?.let { Text("${texts.notesLabel}: $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (exercise.sets.isEmpty()) {
                Text(texts.noSets, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                exercise.sets.forEach { set ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(set.label, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            SetSessionField(
                                modifier = Modifier.weight(1f),
                                value = set.weightInput,
                                field = WorkoutSetField.Weight,
                                texts = texts,
                                enabled = enabled,
                                keyboardType = KeyboardType.Decimal,
                                onValueChange = {
                                    onAction(
                                        WorkoutDetailUiAction.OnSetFieldChange(
                                            exerciseId = exercise.id,
                                            setId = set.id,
                                            field = WorkoutSetField.Weight,
                                            value = it,
                                        )
                                    )
                                },
                            )
                            SetSessionField(
                                modifier = Modifier.weight(1f),
                                value = set.repsInput,
                                field = WorkoutSetField.Reps,
                                texts = texts,
                                enabled = enabled,
                                keyboardType = KeyboardType.Number,
                                onValueChange = {
                                    onAction(
                                        WorkoutDetailUiAction.OnSetFieldChange(
                                            exerciseId = exercise.id,
                                            setId = set.id,
                                            field = WorkoutSetField.Reps,
                                            value = it,
                                        )
                                    )
                                },
                            )
                            SetSessionField(
                                modifier = Modifier.weight(1f),
                                value = set.rpeInput,
                                field = WorkoutSetField.Rpe,
                                texts = texts,
                                enabled = enabled,
                                keyboardType = KeyboardType.Number,
                                onValueChange = {
                                    onAction(
                                        WorkoutDetailUiAction.OnSetFieldChange(
                                            exerciseId = exercise.id,
                                            setId = set.id,
                                            field = WorkoutSetField.Rpe,
                                            value = it,
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Input de un campo de sesión. El label sale de [WorkoutSetField.label], no de un literal.
 */
@Composable
private fun SetSessionField(
    modifier: Modifier,
    value: String,
    field: WorkoutSetField,
    texts: WorkoutTexts,
    enabled: Boolean,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        label = { Text(field.label(texts)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

private fun previewSet(
    id: Long,
    number: Int,
    texts: WorkoutTexts,
    weight: String,
    reps: String,
    rpe: String = "",
) = WorkoutSetUiModel(
    id = id,
    setNumber = number,
    label = texts.setFormat.formatArgs(number),
    weightInput = weight,
    repsInput = reps,
    rpeInput = rpe,
)

@Preview(showBackground = true, name = "WorkoutDetailScreen - con ejercicios")
@Composable
private fun WorkoutDetailScreenPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailScreen(
            state = WorkoutDetailUiState.Content(
                workoutName = "Piernas",
                dayLabel = texts.dayFormat.formatArgs(5),
                notes = "Me sentí fuerte en sentadilla",
                exercises = listOf(
                    WorkoutExerciseUiModel(
                        id = 1L,
                        exerciseMasterId = 10L,
                        name = "Sentadilla",
                        notes = "Mantener espalda recta",
                        sets = listOf(
                            previewSet(1L, 1, texts, "80", "8"),
                            previewSet(2L, 2, texts, "80", "8"),
                            previewSet(3L, 3, texts, "85", "6"),
                        ),
                    ),
                    WorkoutExerciseUiModel(
                        id = 2L,
                        exerciseMasterId = 11L,
                        name = "Prensa",
                        notes = null,
                        sets = listOf(previewSet(4L, 1, texts, "", "12")),
                    ),
                    WorkoutExerciseUiModel(
                        id = 3L,
                        exerciseMasterId = 12L,
                        name = "Gemelos de pie",
                        notes = null,
                        sets = emptyList(),
                    ),
                ),
            ),
            texts = texts,
            onNavigateUp = {},
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - sin ejercicios")
@Composable
private fun WorkoutDetailScreenEmptyPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailScreen(
            state = WorkoutDetailUiState.Content(
                workoutName = "Pecho + Tríceps",
                dayLabel = texts.dayFormat.formatArgs(1),
                exercises = emptyList(),
                notes = "",
            ),
            texts = texts,
            onNavigateUp = {},
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - terminado")
@Composable
private fun WorkoutDetailScreenFinishedPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailScreen(
            state = WorkoutDetailUiState.Content(
                workoutName = "Piernas",
                dayLabel = texts.dayFormat.formatArgs(5),
                exercises = emptyList(),
                notes = "Me sentí fuerte",
                finished = true,
            ),
            texts = texts,
            onNavigateUp = {},
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - finishError")
@Composable
private fun WorkoutDetailScreenFinishErrorPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailScreen(
            state = WorkoutDetailUiState.Content(
                workoutName = "Piernas",
                dayLabel = texts.dayFormat.formatArgs(5),
                exercises = listOf(
                    WorkoutExerciseUiModel(
                        id = 1L,
                        exerciseMasterId = 10L,
                        name = "Sentadilla",
                        notes = null,
                        sets = listOf(previewSet(1L, 1, texts, "80", "8")),
                    ),
                ),
                notes = "Me sentí fuerte",
                finishError = texts.finishFailed,
            ),
            texts = texts,
            onNavigateUp = {},
            onAction = {},
        )
    }
}

@Composable
internal fun WorkoutDetailMessage(
    message: String,
    backLabel: String,
    onNavigateUp: () -> Unit,
    actionLabel: String? = null,
    onAction: ((WorkoutDetailUiAction) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backLabel)
        }
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = { onAction(WorkoutDetailUiAction.OnRetry) },
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - Loading")
@Composable
private fun WorkoutDetailLoadingPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailMessage(message = texts.loading, backLabel = texts.back, onNavigateUp = {})
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - NotFound")
@Composable
private fun WorkoutDetailNotFoundPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailMessage(message = texts.workoutNotFound, backLabel = texts.back, onNavigateUp = {})
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - Error")
@Composable
private fun WorkoutDetailErrorPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailMessage(
            message = Spanish.errorTexts.connectionError,
            backLabel = texts.back,
            onNavigateUp = {},
            actionLabel = texts.retry,
            onAction = {},
        )
    }
}
