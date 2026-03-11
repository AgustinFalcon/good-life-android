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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseDraft
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.DayChipComponent
import com.agusstkd.goodlife.presentation.components.common.DayChipParams
import com.agusstkd.goodlife.presentation.components.common.SwitchComponent
import com.agusstkd.goodlife.presentation.components.common.SwitchParams
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutBackground
import kotlinx.datetime.DayOfWeek

@Composable
fun RoutineSummaryStep(
    uiState: CreateRoutineUiState,
    onAction: (CreateRoutineUiAction) -> Unit,
    routineTexts: CreateRoutineTexts,
    difficultyLabel: String,
    goalLabel: String,
    dayNames: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Resumen card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WorkoutBackground),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = uiState.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryChip(text = difficultyLabel)
                    SummaryChip(text = goalLabel)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Días
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    DayOfWeek.entries.forEachIndexed { index, day ->
                        DayChipComponent(
                            params = DayChipParams(
                                label = dayNames[index],
                                selected = day in uiState.selectedDays,
                            ),
                            onClick = {},
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha/hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    if (uiState.hasTime && uiState.scheduledTime != null) {
                        Text(
                            text = "🕐 ${uiState.scheduledTime}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                    if (uiState.startDateDisplay.isNotEmpty()) {
                        val dateRange = buildString {
                            append("📅 ${uiState.startDateDisplay}")
                            if (uiState.hasEndDate && uiState.endDateDisplay.isNotEmpty()) {
                                append(" - ${uiState.endDateDisplay}")
                            }
                        }
                        Text(
                            text = dateRange,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Workouts resumen ──
        SectionLabel(text = routineTexts.workoutsTitle)
        Spacer(modifier = Modifier.height(8.dp))

        uiState.workouts.forEach { workout ->
            val totalExercises = workout.exercises.size
            val totalSets = workout.exercises.sumOf { it.sets.size }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = workout.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                        )
                        Text(
                            text = "${String.format(routineTexts.exercisesCount, totalExercises)} • ${String.format(routineTexts.setsCount, totalSets)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        HorizontalDivider(color = DividerColor)
        Spacer(modifier = Modifier.height(12.dp))

        // ── Toggle activar ──
        SwitchComponent(
            params = SwitchParams(
                label = routineTexts.activateToggle,
                checked = uiState.activateOnCreate,
            ),
            onCheckedChange = { onAction(CreateRoutineUiAction.OnActivateToggle(it)) },
        )

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
                ),
                onClick = { onAction(CreateRoutineUiAction.OnPreviousStep) },
            )
            ButtonComponent(
                modifier = Modifier.weight(2f),
                params = ButtonParams(
                    text = routineTexts.createButton,
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    variant = ButtonVariant.PRIMARY,
                ),
                onClick = { onAction(CreateRoutineUiAction.OnSubmit) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SummaryChip(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WorkoutAccent.copy(alpha = 0.12f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = WorkoutAccent,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineSummaryStepPreview() {
    GoodLifeTheme {
        RoutineSummaryStep(
            uiState = CreateRoutineUiState(
                name = "Push Pull Legs",
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                goalType = GoalType.MUSCLE_GAIN,
                selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                hasTime = true,
                startDateDisplay = "10 de marzo, 2026",
                workouts = listOf(
                    WorkoutDraft(
                        name = "Push Day",
                        exercises = listOf(
                            ExerciseDraft(1, "Bench Press", null, "Pecho", 1, sets = listOf(SetDraft(1, 12, 60.0), SetDraft(2, 10, 65.0), SetDraft(3, 8, 70.0))),
                            ExerciseDraft(2, "Overhead Press", null, "Hombro", 2),
                        ),
                    ),
                    WorkoutDraft(
                        name = "Pull Day",
                        exercises = listOf(
                            ExerciseDraft(3, "Deadlift", null, "Espalda", 1, sets = listOf(SetDraft(1, 5, 100.0))),
                        ),
                    ),
                ),
            ),
            onAction = {},
            routineTexts = previewRoutineTexts(),
            difficultyLabel = "Intermedio",
            goalLabel = "Ganar músculo",
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier.padding(16.dp),
        )
    }
}
