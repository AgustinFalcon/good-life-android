package com.agusstkd.goodlife.presentation.screen.add.habit

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateHabitTexts
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.CategorySelectorComponent
import com.agusstkd.goodlife.presentation.components.common.CategorySelectorParams
import com.agusstkd.goodlife.presentation.components.common.DateSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.DateSelectorParams
import com.agusstkd.goodlife.presentation.components.common.DayChipComponent
import com.agusstkd.goodlife.presentation.components.common.DayChipParams
import com.agusstkd.goodlife.presentation.components.common.NumericGoalComponent
import com.agusstkd.goodlife.presentation.components.common.NumericGoalParams
import com.agusstkd.goodlife.presentation.components.common.SwitchComponent
import com.agusstkd.goodlife.presentation.components.common.SwitchParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorParams
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiAction
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiState
import com.agusstkd.goodlife.presentation.screen.add.habit.model.HabitDatePickerField
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.HabitAccent
import com.agusstkd.goodlife.presentation.theme.HabitBackground
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import kotlinx.datetime.DayOfWeek

/**
 * UI pura de la pantalla de creación de hábito.
 *
 * No inyecta nada, no tiene lógica de negocio. Solo renderiza
 * el estado y emite acciones via [onAction].
 *
 * @param uiState Estado actual del formulario.
 * @param onAction Callback para acciones del usuario.
 * @param createHabitTexts Textos específicos de la pantalla.
 * @param sharedTexts Labels compartidos (título, descripción, días, fechas).
 * @param categoryEntries Categorías con nombres localizados para el selector.
 * @param dayNames Nombres cortos de los días de la semana.
 * @param closeContentDescription Content description del botón cerrar.
 */
@Composable
fun CreateHabitScreen(
    uiState: CreateHabitUiState,
    onAction: (CreateHabitUiAction) -> Unit,
    createHabitTexts: CreateHabitTexts,
    sharedTexts: CreateItemSharedTexts,
    categoryEntries: List<Pair<HabitCategory, String>>,
    dayNames: List<String>,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // ── Header ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = createHabitTexts.screenTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
            )
            IconButton(onClick = { onAction(CreateHabitUiAction.OnDismiss) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = closeContentDescription,
                )
            }
        }

        Badge(
            containerColor = HabitBackground,
            contentColor = HabitAccent,
        ) {
            Text(
                text = createHabitTexts.typeBadge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Nombre ───────────────────────────────────────────────
        SectionLabel(text = sharedTexts.fieldTitleLabel)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.name,
                placeholder = sharedTexts.fieldTitlePlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateHabitUiAction.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Descripción ──────────────────────────────────────────
        SectionLabel(text = sharedTexts.fieldDescriptionLabel)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.description,
                placeholder = sharedTexts.fieldDescriptionPlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateHabitUiAction.OnDescriptionChange(it)) },
        )

        SectionDivider()

        // ── Categoría ────────────────────────────────────────────
        SectionLabel(text = createHabitTexts.categorySectionTitle)
        Spacer(modifier = Modifier.height(8.dp))
        CategorySelectorComponent(
            params = CategorySelectorParams(
                categories = categoryEntries,
                selectedCategory = uiState.selectedCategory,
                placeholder = createHabitTexts.categoryPlaceholder,
            ),
            onCategorySelected = { onAction(CreateHabitUiAction.OnCategorySelected(it)) },
            modifier = Modifier.fillMaxWidth(),
        )

        SectionDivider()

        // ── Meta numérica ────────────────────────────────────────
        SectionLabel(text = createHabitTexts.goalSectionTitle)
        Spacer(modifier = Modifier.height(8.dp))
        NumericGoalComponent(
            params = NumericGoalParams(
                targetValue = uiState.targetValue,
                unit = uiState.unit,
                targetValuePlaceholder = createHabitTexts.targetValuePlaceholder,
                unitPlaceholder = createHabitTexts.unitPlaceholder,
            ),
            onTargetValueChange = { onAction(CreateHabitUiAction.OnTargetValueChange(it)) },
            onUnitChange = { onAction(CreateHabitUiAction.OnUnitChange(it)) },
        )

        SectionDivider()

        // ── Días de la semana ────────────────────────────────────
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
                    onClick = { onAction(CreateHabitUiAction.OnDayToggled(day)) },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Fecha de inicio ──────────────────────────────────────
        DateSelectorComponent(
            params = DateSelectorParams(
                displayText = uiState.startDateDisplay,
                placeholderText = sharedTexts.fromDateLabel,
            ),
            onClick = { onAction(CreateHabitUiAction.OnDatePickerOpen(HabitDatePickerField.START_DATE)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Toggle + fecha de fin ────────────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = if (uiState.hasEndDate) sharedTexts.hasEndDate else sharedTexts.noEndDate,
                checked = uiState.hasEndDate,
            ),
            onCheckedChange = { onAction(CreateHabitUiAction.OnHasEndDateToggle) },
        )

        if (uiState.hasEndDate) {
            DateSelectorComponent(
                params = DateSelectorParams(
                    displayText = uiState.endDateDisplay,
                    placeholderText = sharedTexts.toDateLabel,
                ),
                onClick = { onAction(CreateHabitUiAction.OnDatePickerOpen(HabitDatePickerField.END_DATE)) },
            )
        }

        SectionDivider()

        // ── Toggle + hora ────────────────────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = if (uiState.hasTime) sharedTexts.timeLabel else sharedTexts.noSpecificTime,
                checked = uiState.hasTime,
            ),
            onCheckedChange = { onAction(CreateHabitUiAction.OnHasTimeToggle) },
        )

        if (uiState.hasTime) {
            TimeSelectorComponent(
                params = TimeSelectorParams(
                    displayText = uiState.scheduledTime?.toString() ?: "",
                    placeholderText = sharedTexts.timeLabel,
                ),
                onClick = { onAction(CreateHabitUiAction.OnTimePickerOpen) },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botón guardar ────────────────────────────────────────
        ButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            params = ButtonParams(
                text = createHabitTexts.saveButton,
                enabled = uiState.name.isNotBlank()
                        && uiState.selectedCategory != null
                        && uiState.selectedDays.isNotEmpty()
                        && uiState.targetValue.isNotBlank()
                        && uiState.unit.isNotBlank()
                        && uiState.startDate != null
                        && !uiState.isLoading,
                isLoading = uiState.isLoading,
                variant = ButtonVariant.PRIMARY,
                colorScheme = ButtonColorScheme.Habit,
            ),
            onClick = { onAction(CreateHabitUiAction.OnSubmit) },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SectionDivider() {
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = DividerColor)
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateHabitScreenPreview() {
    val previewSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Nombre",
        fieldTitlePlaceholder = "Ej: Tomar agua",
        fieldDescriptionLabel = "Descripción",
        fieldDescriptionPlaceholder = "Opcional",
        whenSectionTitle = "¿Cuándo?",
        modeOnce = "Una vez",
        modeRepeats = "Se repite",
        daysRowTitle = "Días de la semana",
        fromDateLabel = "Desde",
        toDateLabel = "Hasta",
        noEndDate = "Sin fecha de fin",
        hasEndDate = "Fecha de fin",
        noSpecificTime = "Sin hora específica",
        timeLabel = "Hora",
        reminderLabel = "Recordatorio",
        cancelButton = "Cancelar",
        confirmLabel = "Confirmar",
        cancelLabel = "Cancelar",
        retryLabel = "Reintentar",
        loadingMessage = "",
        errorTitle = "",
    )
    val previewHabitTexts = CreateHabitTexts(
        screenTitle = "Nuevo hábito",
        typeBadge = "HÁBITO",
        saveButton = "Guardar hábito",
        successTitle = "Hábito creado con éxito",
        categorySectionTitle = "Categoría",
        categoryPlaceholder = "Seleccionar categoría",
        goalSectionTitle = "Meta diaria",
        targetValuePlaceholder = "Ej: 8",
        unitPlaceholder = "Ej: vasos",
        errorNameEmpty = "",
        errorNoDays = "",
        errorNoCategory = "",
        errorInvalidGoal = "",
        errorUnitEmpty = "",
        errorEndBeforeStart = "",
        errorServer = "",
        errorNetwork = "",
        loadingTitle = "",
    )
    val previewCategories = listOf(
        HabitCategory.HYDRATION to "Hidratación",
        HabitCategory.MEDITATION to "Meditación",
        HabitCategory.READING to "Lectura",
        HabitCategory.EXERCISE to "Ejercicio",
        HabitCategory.SLEEP to "Sueño",
    )

    GoodLifeTheme {
        CreateHabitScreen(
            uiState = CreateHabitUiState(
                name = "Tomar agua",
                selectedCategory = HabitCategory.HYDRATION,
                targetValue = "8",
                unit = "vasos",
                selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                startDateDisplay = "10 de marzo, 2026",
            ),
            onAction = {},
            createHabitTexts = previewHabitTexts,
            sharedTexts = previewSharedTexts,
            categoryEntries = previewCategories,
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            closeContentDescription = "Cerrar",
        )
    }
}
