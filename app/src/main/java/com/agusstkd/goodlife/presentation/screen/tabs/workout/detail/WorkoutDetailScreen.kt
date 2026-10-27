package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.datetime.language.WorkoutTexts
import com.agusstkd.goodlife.core.extensions.formatArgs
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutExerciseUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetUiModel
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

@Composable
fun WorkoutDetailScreen(
    state: WorkoutDetailUiState.Content,
    texts: WorkoutTexts,
    onNavigateUp: () -> Unit,
    onAction: (WorkoutDetailUiAction) -> Unit,
) {
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
                WorkoutExerciseCard(exercise, texts)
            }
        }
        item {
            Button(
                onClick = { onAction(WorkoutDetailUiAction.OnFinishWorkout) },
                enabled = state.exercises.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(texts.finishWorkout)
            }
        }
    }
}

@Composable
private fun WorkoutExerciseCard(exercise: WorkoutExerciseUiModel, texts: WorkoutTexts) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(set.label, fontWeight = FontWeight.Medium)
                        Text(set.summary, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "WorkoutDetailScreen - con ejercicios")
@Composable
private fun WorkoutDetailScreenPreview() {
    val texts = Spanish.workoutTexts
    GoodLifeTheme {
        WorkoutDetailScreen(
            state = WorkoutDetailUiState.Content(
                workoutName = "Piernas",
                dayLabel = texts.dayFormat.formatArgs(5),
                exercises = listOf(
                    WorkoutExerciseUiModel(
                        id = 1L,
                        name = "Sentadilla",
                        notes = "Mantener espalda recta",
                        sets = listOf(
                            WorkoutSetUiModel(
                                label = texts.setFormat.formatArgs(1),
                                summary = texts.repsAndWeightFormat.formatArgs(8, "80"),
                            ),
                            WorkoutSetUiModel(
                                label = texts.setFormat.formatArgs(2),
                                summary = texts.repsAndWeightFormat.formatArgs(8, "80"),
                            ),
                            WorkoutSetUiModel(
                                label = texts.setFormat.formatArgs(3),
                                summary = texts.repsAndWeightFormat.formatArgs(6, "85"),
                            ),
                        ),
                    ),
                    WorkoutExerciseUiModel(
                        id = 2L,
                        name = "Prensa",
                        notes = null,
                        sets = listOf(
                            WorkoutSetUiModel(
                                label = texts.setFormat.formatArgs(1),
                                summary = texts.repsFormat.formatArgs(12),
                            ),
                        ),
                    ),
                    WorkoutExerciseUiModel(
                        id = 3L,
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
