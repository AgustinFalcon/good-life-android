package com.agusstkd.goodlife.presentation.screen.add.routine

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.presentation.components.wizard.WizardHeaderComponent
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiAction
import com.agusstkd.goodlife.presentation.screen.add.routine.model.CreateRoutineUiState
import com.agusstkd.goodlife.presentation.screen.add.routine.model.RoutineWizardStep
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.RoutineInfoStep
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.RoutineSummaryStep
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.WorkoutExercisesStep
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.WorkoutsStep
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.previewRoutineTexts
import com.agusstkd.goodlife.presentation.screen.add.routine.steps.previewSharedTexts
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutBackground

@Composable
fun CreateRoutineScreen(
    uiState: CreateRoutineUiState,
    onAction: (CreateRoutineUiAction) -> Unit,
    routineTexts: CreateRoutineTexts,
    sharedTexts: CreateItemSharedTexts,
    difficultyEntries: List<Pair<DifficultyLevel, String>>,
    goalEntries: List<Pair<GoalType, String>>,
    dayNames: List<String>,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // ── Header ──
        WizardHeaderComponent(
            title = routineTexts.screenTitle,
            badgeText = routineTexts.typeBadge,
            badgeContainerColor = WorkoutBackground,
            badgeContentColor = WorkoutAccent,
            stepLabel = String.format(routineTexts.stepOf, uiState.stepNumber, uiState.totalSteps),
            progress = uiState.stepNumber.toFloat() / uiState.totalSteps,
            accentColor = WorkoutAccent,
            onBack = if (uiState.currentStep != RoutineWizardStep.ROUTINE_INFO) {
                { onAction(CreateRoutineUiAction.OnPreviousStep) }
            } else null,
            onClose = { onAction(CreateRoutineUiAction.OnDismiss) },
            closeContentDescription = closeContentDescription,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Steps content ──
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
            label = "wizard_step",
            modifier = Modifier.weight(1f),
        ) { step ->
            when (step) {
                RoutineWizardStep.ROUTINE_INFO -> RoutineInfoStep(
                    uiState = uiState,
                    onAction = onAction,
                    routineTexts = routineTexts,
                    sharedTexts = sharedTexts,
                    difficultyEntries = difficultyEntries,
                    goalEntries = goalEntries,
                    dayNames = dayNames,
                )

                RoutineWizardStep.WORKOUTS -> WorkoutsStep(
                    uiState = uiState,
                    onAction = onAction,
                    routineTexts = routineTexts,
                )

                RoutineWizardStep.WORKOUT_EXERCISES -> WorkoutExercisesStep(
                    uiState = uiState,
                    onAction = onAction,
                    routineTexts = routineTexts,
                )

                RoutineWizardStep.SUMMARY -> {
                    val difficultyLabel = difficultyEntries
                        .firstOrNull { it.first == uiState.difficultyLevel }?.second ?: ""
                    val goalLabel = goalEntries
                        .firstOrNull { it.first == uiState.goalType }?.second ?: ""

                    RoutineSummaryStep(
                        uiState = uiState,
                        onAction = onAction,
                        routineTexts = routineTexts,
                        difficultyLabel = difficultyLabel,
                        goalLabel = goalLabel,
                        dayNames = dayNames,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateRoutineScreenStep1Preview() {
    GoodLifeTheme {
        CreateRoutineScreen(
            uiState = CreateRoutineUiState(
                name = "Push Pull Legs",
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                goalType = GoalType.MUSCLE_GAIN,
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
            closeContentDescription = "Cerrar",
        )
    }
}
