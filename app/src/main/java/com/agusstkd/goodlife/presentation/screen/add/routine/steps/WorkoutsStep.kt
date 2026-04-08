package com.agusstkd.goodlife.presentation.screen.add.routine.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.ExerciseDraft
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutBackground

@Composable
fun WorkoutsStep(
    uiState: CreateRoutineUiState,
    onAction: (CreateRoutineUiAction) -> Unit,
    routineTexts: CreateRoutineTexts,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionLabel(text = routineTexts.workoutsTitle)
        Text(
            text = routineTexts.workoutsSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Lista de workouts ──
        uiState.workouts.forEachIndexed { index, workout ->
            WorkoutCard(
                index = index,
                workout = workout,
                routineTexts = routineTexts,
                onEdit = { onAction(CreateRoutineUiAction.OnEditWorkout(index)) },
                onDelete = { onAction(CreateRoutineUiAction.OnDeleteWorkout(index)) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Botón agregar ──
        OutlinedButton(
            onClick = { onAction(CreateRoutineUiAction.OnShowAddWorkoutDialog) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(routineTexts.addWorkout)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Navegación ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ButtonComponent(
                modifier = Modifier.weight(1f),
                params = ButtonParams(
                    text = routineTexts.backButton,
                    variant = ButtonVariant.OUTLINE,
                    colorScheme = ButtonColorScheme.Workout,
                ),
                onClick = { onAction(CreateRoutineUiAction.OnPreviousStep) },
            )
            ButtonComponent(
                modifier = Modifier.weight(2f),
                params = ButtonParams(
                    text = routineTexts.nextButton,
                    enabled = uiState.canGoNext,
                    variant = ButtonVariant.PRIMARY,
                    colorScheme = ButtonColorScheme.Workout,
                ),
                onClick = { onAction(CreateRoutineUiAction.OnNextStep) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun WorkoutCard(
    index: Int,
    workout: WorkoutDraft,
    routineTexts: CreateRoutineTexts,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val exerciseCount = workout.exercises.size
    val setsCount = workout.exercises.sumOf { it.sets.size }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WorkoutBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                if (exerciseCount > 0) {
                    Text(
                        text = "${String.format(routineTexts.exercisesCount, exerciseCount)} • ${String.format(routineTexts.setsCount, setsCount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = WorkoutAccent,
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = ErrorRed,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutsStepPreview() {
    GoodLifeTheme {
        WorkoutsStep(
            uiState = CreateRoutineUiState(
                workouts = listOf(
                    WorkoutDraft(
                        name = "Push Day",
                        exercises = listOf(
                            ExerciseDraft(1, "Bench Press", null, "Pecho", 1, sets = listOf(SetDraft(), SetDraft(2), SetDraft(3))),
                            ExerciseDraft(2, "Overhead Press", null, "Hombro", 2),
                        ),
                    ),
                    WorkoutDraft(name = "Pull Day"),
                ),
            ),
            onAction = {},
            routineTexts = previewRoutineTexts(),
            modifier = Modifier.padding(16.dp),
        )
    }
}
