package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.wizard.WizardBottomBarComponent
import com.agusstkd.goodlife.presentation.components.wizard.WizardBottomBarParams
import com.agusstkd.goodlife.presentation.components.wizard.WizardHeaderComponent
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealPlanWizardStep
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.MealIngredientsStep
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.ScheduleStep
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.SelectMealStep
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.previewMealPlanTexts
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.previewSharedTexts
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.previewMealTypeEntries
import com.agusstkd.goodlife.presentation.screen.add.mealplan.steps.previewMealPlanUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionAccent

/**
 * UI pura del wizard de creación de plan de comida.
 *
 * Estructura:
 * - [topBar]: [WizardHeaderComponent] con título, badge nutrición, indicador de paso y progreso.
 * - [bottomBar]: botón principal configurable ("Siguiente" / "Crear plan").
 * - Contenido: [AnimatedContent] que anima la transición entre los 3 pasos del wizard.
 *
 * Los 3 pasos del wizard:
 * - [MealPlanWizardStep.SELECT_MEAL]: catálogo de meals con búsqueda y filtros.
 * - [MealPlanWizardStep.MEAL_INGREDIENTS]: resumen de macros + catálogo de ingredientes.
 * - [MealPlanWizardStep.SCHEDULE]: tipo de comida, días, fechas y hora.
 *
 * @param uiState Estado actual del wizard.
 * @param onAction Callback para todas las acciones del usuario.
 * @param texts Textos específicos del wizard (títulos, botones, placeholders).
 * @param sharedTexts Labels compartidos entre wizards (fechas, días, tiempo, etc.).
 * @param mealTypeEntries Tipos de comida con sus nombres localizados.
 * @param dayNames Nombres cortos de los días de la semana (L, M, X...).
 * @param closeContentDescription Content description del botón cerrar para accesibilidad.
 */
@Composable
fun CreateMealPlanScreen(
    uiState: CreateMealPlanUiState,
    onAction: (CreateMealPlanUiAction) -> Unit,
    texts: CreateMealPlanTexts,
    sharedTexts: CreateItemSharedTexts,
    mealTypeEntries: List<Pair<MealType, String>>,
    dayNames: List<String>,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val totalSteps = MealPlanWizardStep.entries.size
    val stepNumber = uiState.currentStep.ordinal + 1

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                WizardHeaderComponent(
                    title = texts.screenTitle,
                    badgeText = texts.typeBadge,
                    badgeContainerColor = NutritionAccent.copy(alpha = 0.15f),
                    badgeContentColor = NutritionAccent,
                    stepLabel = String.format(texts.stepOf, stepNumber, totalSteps),
                    progress = stepNumber.toFloat() / totalSteps,
                    accentColor = NutritionAccent,
                    onBack = if (uiState.currentStep != MealPlanWizardStep.SELECT_MEAL) {
                        { onAction(CreateMealPlanUiAction.OnPreviousStep) }
                    } else null,
                    onClose = { onAction(CreateMealPlanUiAction.OnDismiss) },
                    closeContentDescription = closeContentDescription,
                )
            }
        },
        bottomBar = {
            WizardBottomBarComponent(
                params = WizardBottomBarParams(
                    primaryText = if (uiState.currentStep == MealPlanWizardStep.SCHEDULE) {
                        texts.createPlanButton
                    } else {
                        texts.nextButton
                    },
                    primaryEnabled = true,
                    isLoading = false,
                ),
                onPrimary = { onAction(CreateMealPlanUiAction.OnNextStep) },
            )
        },
    ) { paddingValues ->

        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
            label = "meal_wizard_step",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) { step ->
            when (step) {
                MealPlanWizardStep.SELECT_MEAL -> SelectMealStep(
                    uiState = uiState,
                    onAction = onAction,
                    texts = texts,
                    mealTypeEntries = mealTypeEntries,
                )
                MealPlanWizardStep.MEAL_INGREDIENTS -> MealIngredientsStep(
                    uiState = uiState,
                    onAction = onAction,
                    texts = texts,
                )
                MealPlanWizardStep.SCHEDULE -> ScheduleStep(
                    uiState = uiState,
                    onAction = onAction,
                    texts = texts,
                    sharedTexts = sharedTexts,
                    mealTypeEntries = mealTypeEntries,
                    dayNames = dayNames,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "CreateMealPlanScreen — Paso 1")
@Composable
private fun CreateMealPlanScreenPreview() {
    GoodLifeTheme {
        CreateMealPlanScreen(
            uiState = previewMealPlanUiState(),
            onAction = {},
            texts = previewMealPlanTexts(),
            sharedTexts = previewSharedTexts(),
            mealTypeEntries = previewMealTypeEntries(),
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            closeContentDescription = "Cerrar",
        )
    }
}
