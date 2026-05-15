package com.agusstkd.goodlife.presentation.screen.add.routine.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.agusstkd.goodlife.presentation.components.common.SearchBarComponent
import com.agusstkd.goodlife.presentation.components.common.SearchBarParams
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.ExerciseDraft
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.MuscleGroup
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutBackground

@Composable
fun WorkoutExercisesStep(
    uiState: CreateRoutineUiState,
    onAction: (CreateRoutineUiAction) -> Unit,
    routineTexts: CreateRoutineTexts,
    modifier: Modifier = Modifier,
) {
    val workout = uiState.activeWorkout ?: return
    val listState = rememberLazyListState()
    val addedExerciseIds = remember(workout.exercises) {
        workout.exercises.map { it.exerciseId }.toSet()
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisible >= totalItems - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onAction(CreateRoutineUiAction.OnLoadMoreExercises) }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // ── Ejercicios agregados ──
        if (workout.exercises.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = routineTexts.yourExercises,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = "${workout.exercises.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = WorkoutAccent,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Added exercises section
            items(workout.exercises, key = { it.orderIndex }) { exercise ->
                AddedExerciseCard(
                    exercise = exercise,
                    routineTexts = routineTexts,
                    onEdit = { onAction(CreateRoutineUiAction.OnOpenSetEditor(exercise.orderIndex)) },
                    onRemove = { onAction(CreateRoutineUiAction.OnRemoveExercise(exercise.orderIndex)) },
                )
            }

            if (workout.exercises.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DividerColor)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Catalog header
            item {
                Text(
                    text = routineTexts.catalogTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Search bar
                SearchBarComponent(
                    params = SearchBarParams(
                        query = uiState.exerciseSearchQuery,
                        placeholder = routineTexts.searchPlaceholder,
                    ),
                    onQueryChange = { onAction(CreateRoutineUiAction.OnSearchQueryChange(it)) },
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Muscle group filters
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = uiState.selectedMuscleGroupId == null,
                            onClick = { onAction(CreateRoutineUiAction.OnMuscleGroupFilter(null)) },
                            label = { Text(routineTexts.filterAll) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WorkoutAccent.copy(alpha = 0.15f),
                                selectedLabelColor = WorkoutAccent,
                            ),
                        )
                    }
                    items(uiState.muscleGroups) { group ->
                        FilterChip(
                            selected = uiState.selectedMuscleGroupId == group.id,
                            onClick = { onAction(CreateRoutineUiAction.OnMuscleGroupFilter(group.id)) },
                            label = { Text(group.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WorkoutAccent.copy(alpha = 0.15f),
                                selectedLabelColor = WorkoutAccent,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Catalog items
            items(uiState.exerciseCatalog, key = { it.id }) { exercise ->
                CatalogExerciseCard(
                    exercise = exercise,
                    isAdded = exercise.id in addedExerciseIds,
                    routineTexts = routineTexts,
                    onAdd = { onAction(CreateRoutineUiAction.OnAddExercise(exercise)) },
                )
            }

            // Loading indicator
            if (uiState.isLoadingCatalog) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WorkoutAccent,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Guardar workout ──
        ButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            params = ButtonParams(
                text = routineTexts.saveWorkoutButton,
                enabled = workout.exercises.isNotEmpty(),
                variant = ButtonVariant.PRIMARY,
                colorScheme = ButtonColorScheme.Workout,
            ),
            onClick = { onAction(CreateRoutineUiAction.OnSaveWorkout) },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AddedExerciseCard(
    exercise: ExerciseDraft,
    routineTexts: CreateRoutineTexts,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val setsText = exercise.sets.joinToString(" / ") { "${it.targetReps}r" }
    val weightText = exercise.sets
        .mapNotNull { it.targetWeight }
        .takeIf { it.isNotEmpty() }
        ?.let { weights -> "${weights.min()}-${weights.max()} kg" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WorkoutBackground),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.exerciseName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = buildString {
                            append("${exercise.sets.size} ${routineTexts.setsLabel.lowercase()}")
                            append(" • $setsText")
                            weightText?.let { append(" • $it") }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Row {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(routineTexts.editSetsButton, style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogExerciseCard(
    exercise: ExerciseMaster,
    isAdded: Boolean,
    routineTexts: CreateRoutineTexts,
    onAdd: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                )
                Text(
                    text = exercise.muscleGroupName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            if (!isAdded) {
                OutlinedButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(routineTexts.addExerciseButton)
                }
            } else {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleMedium,
                    color = WorkoutAccent,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutExercisesStepPreview() {
    GoodLifeTheme {
        WorkoutExercisesStep(
            uiState = CreateRoutineUiState(
                activeWorkoutIndex = 0,
                workouts = listOf(
                    WorkoutDraft(
                        name = "Push Day",
                        exercises = listOf(
                            ExerciseDraft(1, "Bench Press", null, "Pecho", 1, sets = listOf(SetDraft(1, 12, 60.0), SetDraft(2, 10, 65.0))),
                            ExerciseDraft(2, "Overhead Press", null, "Hombro", 2),
                        ),
                    ),
                ),
                exerciseCatalog = listOf(
                    ExerciseMaster(3, "Dumbbell Flyes", "Aislamiento de pecho", null, 1, "Pecho", 7),
                    ExerciseMaster(4, "Lateral Raises", "Hombro lateral", null, 3, "Hombro", 6),
                ),
                muscleGroups = listOf(
                    MuscleGroup(1, "CHEST", "Pecho", null),
                    MuscleGroup(2, "BACK", "Espalda", null),
                    MuscleGroup(3, "SHOULDERS", "Hombro", null),
                ),
            ),
            onAction = {},
            routineTexts = previewRoutineTexts(),
            modifier = Modifier.padding(16.dp),
        )
    }
}
