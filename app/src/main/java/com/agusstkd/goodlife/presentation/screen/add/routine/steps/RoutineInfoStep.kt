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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.DateSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.DateSelectorParams
import com.agusstkd.goodlife.presentation.components.common.DayChipComponent
import com.agusstkd.goodlife.presentation.components.common.DayChipParams
import com.agusstkd.goodlife.presentation.components.common.SwitchComponent
import com.agusstkd.goodlife.presentation.components.common.SwitchParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorParams
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineDatePickerField
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import kotlinx.datetime.DayOfWeek

@Composable
fun RoutineInfoStep(
    uiState: CreateRoutineUiState,
    onAction: (CreateRoutineUiAction) -> Unit,
    routineTexts: CreateRoutineTexts,
    sharedTexts: CreateItemSharedTexts,
    difficultyEntries: List<Pair<DifficultyLevel, String>>,
    goalEntries: List<Pair<GoalType, String>>,
    dayNames: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Nombre ──
        SectionLabel(text = sharedTexts.fieldTitleLabel)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.name,
                placeholder = sharedTexts.fieldTitlePlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateRoutineUiAction.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Descripción ──
        SectionLabel(text = sharedTexts.fieldDescriptionLabel)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.description,
                placeholder = sharedTexts.fieldDescriptionPlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateRoutineUiAction.OnDescriptionChange(it)) },
        )

        StepDivider()

        // ── Dificultad ──
        SectionLabel(text = routineTexts.difficultySectionTitle)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            difficultyEntries.forEach { (level, label) ->
                FilterChip(
                    selected = uiState.difficultyLevel == level,
                    onClick = { onAction(CreateRoutineUiAction.OnDifficultySelected(level)) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WorkoutAccent.copy(alpha = 0.15f),
                        selectedLabelColor = WorkoutAccent,
                    ),
                )
            }
        }

        StepDivider()

        // ── Objetivo ──
        SectionLabel(text = routineTexts.goalSectionTitle)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            goalEntries.forEach { (goal, label) ->
                FilterChip(
                    selected = uiState.goalType == goal,
                    onClick = { onAction(CreateRoutineUiAction.OnGoalTypeSelected(goal)) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WorkoutAccent.copy(alpha = 0.15f),
                        selectedLabelColor = WorkoutAccent,
                    ),
                )
            }
        }

        StepDivider()

        // ── Días ──
        SectionLabel(text = sharedTexts.daysRowTitle)
        Spacer(modifier = Modifier.height(8.dp))
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
                    onClick = { onAction(CreateRoutineUiAction.OnDayToggled(day)) },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Fecha inicio ──
        DateSelectorComponent(
            params = DateSelectorParams(
                displayText = uiState.startDateDisplay,
                placeholderText = sharedTexts.fromDateLabel,
            ),
            onClick = { onAction(CreateRoutineUiAction.OnDatePickerOpen(RoutineDatePickerField.START_DATE)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Toggle + fecha fin ──
        SwitchComponent(
            params = SwitchParams(
                label = if (uiState.hasEndDate) sharedTexts.hasEndDate else sharedTexts.noEndDate,
                checked = uiState.hasEndDate,
            ),
            onCheckedChange = { onAction(CreateRoutineUiAction.OnHasEndDateToggle) },
        )

        if (uiState.hasEndDate) {
            DateSelectorComponent(
                params = DateSelectorParams(
                    displayText = uiState.endDateDisplay,
                    placeholderText = sharedTexts.toDateLabel,
                ),
                onClick = { onAction(CreateRoutineUiAction.OnDatePickerOpen(RoutineDatePickerField.END_DATE)) },
            )
        }

        StepDivider()

        // ── Toggle + hora ──
        SwitchComponent(
            params = SwitchParams(
                label = if (uiState.hasTime) sharedTexts.timeLabel else sharedTexts.noSpecificTime,
                checked = uiState.hasTime,
            ),
            onCheckedChange = { onAction(CreateRoutineUiAction.OnHasTimeToggle) },
        )

        if (uiState.hasTime) {
            TimeSelectorComponent(
                params = TimeSelectorParams(
                    displayText = uiState.scheduledTime?.toString() ?: "",
                    placeholderText = sharedTexts.timeLabel,
                ),
                onClick = { onAction(CreateRoutineUiAction.OnTimePickerOpen) },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Siguiente ──
        ButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            params = ButtonParams(
                text = routineTexts.nextButton,
                enabled = uiState.canGoNext,
                variant = ButtonVariant.PRIMARY,
            ),
            onClick = { onAction(CreateRoutineUiAction.OnNextStep) },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
internal fun StepDivider() {
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = DividerColor)
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
private fun RoutineInfoStepPreview() {
    GoodLifeTheme {
        RoutineInfoStep(
            uiState = CreateRoutineUiState(
                name = "Push Pull Legs",
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                goalType = GoalType.MUSCLE_GAIN,
                selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
            onAction = {},
            routineTexts = previewRoutineTexts(),
            sharedTexts = previewSharedTexts(),
            difficultyEntries = listOf(
                DifficultyLevel.BEGINNER to "Principiante",
                DifficultyLevel.INTERMEDIATE to "Intermedio",
                DifficultyLevel.ADVANCED to "Avanzado",
            ),
            goalEntries = listOf(
                GoalType.MUSCLE_GAIN to "Ganar músculo",
                GoalType.WEIGHT_LOSS to "Perder peso",
                GoalType.STRENGTH to "Fuerza",
                GoalType.ENDURANCE to "Resistencia",
                GoalType.FLEXIBILITY to "Flexibilidad",
            ),
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier.padding(16.dp),
        )
    }
}
