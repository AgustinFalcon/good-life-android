package com.agusstkd.goodlife.presentation.screen.add.mealplan.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.common.DateSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.DateSelectorParams
import com.agusstkd.goodlife.presentation.components.common.DayChipComponent
import com.agusstkd.goodlife.presentation.components.common.DayChipParams
import com.agusstkd.goodlife.presentation.components.common.SwitchComponent
import com.agusstkd.goodlife.presentation.components.common.SwitchParams
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorParams
import com.agusstkd.goodlife.presentation.components.nutrition.MealHeaderCard
import com.agusstkd.goodlife.presentation.components.nutrition.MealHeaderCardParams
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealDatePickerField
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.MealAccent
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import kotlinx.datetime.DayOfWeek

/**
 * Paso 3 del wizard de creación de plan de comida.
 *
 * Muestra un resumen compacto de la meal seleccionada seguido de los controles
 * de planificación:
 * - Header con nombre, calorías y macros de [CreateMealPlanUiState.selectedMeal].
 * - Selector de tipo de comida ([MealType]) con [FilterChip] en [FlowRow].
 * - Selector de días de la semana con [DayChipComponent].
 * - Fecha de inicio con [DateSelectorComponent] (siempre visible).
 * - Toggle "Sin fecha de fin" con [SwitchComponent] + [DateSelectorComponent] opcional.
 * - Toggle "Sin hora específica" con [SwitchComponent] + [TimeSelectorComponent] opcional.
 *
 * El botón "Crear Plan" está en el [WizardBottomBarComponent] del Screen — no en este step.
 *
 * @param uiState Estado actual del wizard.
 * @param onAction Callback para todas las acciones del usuario.
 * @param texts Textos localizados del wizard (títulos de sección, etc.).
 * @param sharedTexts Labels compartidos de scheduling (fechas, días, hora).
 * @param mealTypeEntries Tipos de comida con sus nombres localizados.
 * @param dayNames Abreviaciones de los días de la semana (ej: ["L","M","X","J","V","S","D"]).
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleStep(
    uiState: CreateMealPlanUiState,
    onAction: (CreateMealPlanUiAction) -> Unit,
    texts: CreateMealPlanTexts,
    sharedTexts: CreateItemSharedTexts,
    mealTypeEntries: List<Pair<MealType, String>>,
    dayNames: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        // ── Header: resumen de la meal seleccionada ─────────────────────────
        uiState.selectedMeal?.let { meal ->
            MealHeaderCard(
                params = MealHeaderCardParams(
                    name = meal.name,
                    imageUrl = meal.imageUrl,
                    calories = meal.calories,
                    protein = meal.protein,
                    carbs = meal.carbs,
                ),
                onClick = { /* TODO: abrir sheet de detalle de meal */ },
            )
        }

        // ── Tipo de comida ──────────────────────────────────────────────────
        SectionLabel(text = texts.mealTypeSectionTitle)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            mealTypeEntries.forEach { (mealType, label) ->
                FilterChip(
                    selected = uiState.mealType == mealType,
                    onClick = { onAction(CreateMealPlanUiAction.OnMealTypeSelected(mealType = mealType)) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MealAccent.copy(alpha = 0.15f),
                        selectedLabelColor = MealAccent,
                    ),
                )
            }
        }

        // ── Días de la semana ───────────────────────────────────────────────
        SectionLabel(text = sharedTexts.daysRowTitle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            DayOfWeek.entries.forEachIndexed { index, day ->
                DayChipComponent(
                    params = DayChipParams(
                        label = dayNames[index],
                        selected = day in uiState.selectedDays,
                        selectedColor = MealAccent,
                    ),
                    onClick = { onAction(CreateMealPlanUiAction.OnDayToggled(day = day)) },
                )
            }
        }

        // ── Fecha inicio ────────────────────────────────────────────────────
        SectionLabel(text = sharedTexts.fromDateLabel)
        DateSelectorComponent(
            params = DateSelectorParams(
                displayText = uiState.startDateDisplay,
                placeholderText = sharedTexts.fromDateLabel,
            ),
            onClick = { onAction(CreateMealPlanUiAction.OnDatePickerOpen(field = MealDatePickerField.START_DATE)) },
        )

        // ── Toggle fecha fin + selector condicional ─────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = sharedTexts.noEndDate,
                checked = !uiState.hasEndDate,
                checkedTrackColor = MealAccent,
            ),
            onCheckedChange = { onAction(CreateMealPlanUiAction.OnHasEndDateToggle) },
        )

        AnimatedVisibility(visible = uiState.hasEndDate) {
            DateSelectorComponent(
                params = DateSelectorParams(
                    displayText = uiState.endDateDisplay,
                    placeholderText = sharedTexts.toDateLabel,
                ),
                onClick = { onAction(CreateMealPlanUiAction.OnDatePickerOpen(field = MealDatePickerField.END_DATE)) },
            )
        }

        // ── Toggle hora + selector condicional ──────────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = sharedTexts.noSpecificTime,
                checked = !uiState.hasTime,
                checkedTrackColor = MealAccent,
            ),
            onCheckedChange = { onAction(CreateMealPlanUiAction.OnHasTimeToggle) },
        )

        AnimatedVisibility(visible = uiState.hasTime) {
            TimeSelectorComponent(
                params = TimeSelectorParams(
                    displayText = uiState.scheduledTime?.toString() ?: "",
                    placeholderText = sharedTexts.timeLabel,
                ),
                onClick = { onAction(CreateMealPlanUiAction.OnTimePickerOpen) },
            )
        }
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

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "ScheduleStep — vacío")
@Composable
private fun ScheduleStepEmptyPreview() {
    GoodLifeTheme {
        ScheduleStep(
            uiState = previewMealPlanUiState(),
            onAction = {},
            texts = previewMealPlanTexts(),
            sharedTexts = previewSharedTexts(),
            mealTypeEntries = previewMealTypeEntries(),
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
        )
    }
}

@Preview(showBackground = true, name = "ScheduleStep — con datos")
@Composable
private fun ScheduleStepWithDataPreview() {
    GoodLifeTheme {
        ScheduleStep(
            uiState = previewMealPlanUiState().copy(
                selectedMeal = previewMealCatalog().first(),
                mealType = MealType.LUNCH,
                startDateDisplay = "Hoy, 7 de Abril",
            ),
            onAction = {},
            texts = previewMealPlanTexts(),
            sharedTexts = previewSharedTexts(),
            mealTypeEntries = previewMealTypeEntries(),
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
        )
    }
}
