package com.agusstkd.goodlife.presentation.screen.add.task

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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateTaskTexts
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.DayChipComponent
import com.agusstkd.goodlife.presentation.components.common.DayChipParams
import com.agusstkd.goodlife.presentation.components.common.DateSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.DateSelectorParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.common.SwitchComponent
import com.agusstkd.goodlife.presentation.components.common.SwitchParams
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorComponent
import com.agusstkd.goodlife.presentation.components.common.TimeSelectorParams
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiAction
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiState
import com.agusstkd.goodlife.presentation.screen.add.task.model.DatePickerField
import com.agusstkd.goodlife.presentation.screen.add.task.model.SchedulingMode
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TaskStyle
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import kotlinx.datetime.DayOfWeek

@Composable
fun CreateTaskScreen(
    uiState: CreateTaskUiState,
    onAction: (CreateTaskUiAction) -> Unit,
    createTaskTexts: CreateTaskTexts,
    sharedTexts: CreateItemSharedTexts,
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
                text = createTaskTexts.screenTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
            )
            IconButton(onClick = { onAction(CreateTaskUiAction.OnDismiss) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = closeContentDescription,
                )
            }
        }

        Badge(
            containerColor = TaskStyle.badgeBackground,
            contentColor = TaskStyle.badgeText,
        ) {
            Text(
                text = createTaskTexts.typeBadge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Nombre ───────────────────────────────────────────────
        SectionLabel(text = sharedTexts.fieldTitleLabel)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.title,
                placeholder = sharedTexts.fieldTitlePlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateTaskUiAction.OnTitleChange(it)) },
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
            onValueChange = { onAction(CreateTaskUiAction.OnDescriptionChange(it)) },
        )

        SectionDivider()

        // ── Modo de scheduling ───────────────────────────────────
        SectionLabel(text = sharedTexts.whenSectionTitle)
        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.schedulingMode == SchedulingMode.ONCE,
                onClick = { onAction(CreateTaskUiAction.OnSchedulingModeChange(SchedulingMode.ONCE)) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(sharedTexts.modeOnce)
            }
            SegmentedButton(
                selected = uiState.schedulingMode == SchedulingMode.RECURRENT,
                onClick = { onAction(CreateTaskUiAction.OnSchedulingModeChange(SchedulingMode.RECURRENT)) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(sharedTexts.modeRepeats)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (uiState.schedulingMode) {
            SchedulingMode.ONCE -> {
                DateSelectorComponent(
                    params = DateSelectorParams(
                        displayText = uiState.scheduledDateDisplay,
                        placeholderText = sharedTexts.fromDateLabel,
                    ),
                    onClick = { onAction(CreateTaskUiAction.OnDatePickerOpen(DatePickerField.SCHEDULED_DATE)) },
                )
            }

            SchedulingMode.RECURRENT -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    DayOfWeek.entries.forEachIndexed { index, day ->
                        DayChipComponent(
                            params = DayChipParams(
                                label = dayNames[index],
                                selected = day in uiState.recurrentDays,
                            ),
                            onClick = { onAction(CreateTaskUiAction.OnDayToggled(day)) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                DateSelectorComponent(
                    params = DateSelectorParams(
                        displayText = uiState.startDateDisplay,
                        placeholderText = sharedTexts.fromDateLabel,
                    ),
                    onClick = { onAction(CreateTaskUiAction.OnDatePickerOpen(DatePickerField.START_DATE)) },
                )

                Spacer(modifier = Modifier.height(8.dp))

                SwitchComponent(
                    params = SwitchParams(
                        label = if (uiState.hasEndDate) sharedTexts.hasEndDate else sharedTexts.noEndDate,
                        checked = uiState.hasEndDate,
                    ),
                    onCheckedChange = { onAction(CreateTaskUiAction.OnHasEndDateToggle) },
                )

                if (uiState.hasEndDate) {
                    DateSelectorComponent(
                        params = DateSelectorParams(
                            displayText = uiState.endDateDisplay,
                            placeholderText = sharedTexts.toDateLabel,
                        ),
                        onClick = { onAction(CreateTaskUiAction.OnDatePickerOpen(DatePickerField.END_DATE)) },
                    )
                }
            }
        }

        SectionDivider()

        // ── Toggle + hora ────────────────────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = if (uiState.hasTime) sharedTexts.timeLabel else sharedTexts.noSpecificTime,
                checked = uiState.hasTime,
            ),
            onCheckedChange = { onAction(CreateTaskUiAction.OnHasTimeToggle) },
        )

        if (uiState.hasTime) {
            TimeSelectorComponent(
                params = TimeSelectorParams(
                    displayText = uiState.scheduledTime?.toString() ?: "",
                    placeholderText = sharedTexts.timeLabel,
                ),
                onClick = { onAction(CreateTaskUiAction.OnTimePickerOpen) },
            )
        }

        // ── Recordatorio ─────────────────────────────────────────
        SwitchComponent(
            params = SwitchParams(
                label = sharedTexts.reminderLabel,
                checked = uiState.reminderEnabled,
            ),
            onCheckedChange = { onAction(CreateTaskUiAction.OnReminderChange(it)) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botón guardar ────────────────────────────────────────
        ButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            params = ButtonParams(
                text = createTaskTexts.saveButton,
                enabled = uiState.title.isNotBlank() && !uiState.isLoading,
                isLoading = uiState.isLoading,
                variant = ButtonVariant.PRIMARY,
            ),
            onClick = { onAction(CreateTaskUiAction.OnSubmit) },
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
private fun CreateTaskScreenOncePreview() {
    val previewSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Nombre",
        fieldTitlePlaceholder = "Ej: Ir al gimnasio",
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
    )
    val previewTaskTexts = CreateTaskTexts(
        screenTitle = "Nueva tarea",
        typeBadge = "TAREA",
        saveButton = "Guardar tarea",
        successTitle = "Tarea creada con éxito",
        errorTitleEmpty = "",
        errorNoDate = "",
        errorNoDays = "",
        errorEndBeforeStart = "",
        errorServer = "",
        errorNetwork = "",
    )
    GoodLifeTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(
                title = "Ir al gimnasio",
                schedulingMode = SchedulingMode.ONCE,
                scheduledDateDisplay = "10 de marzo, 2026",
            ),
            onAction = {},
            createTaskTexts = previewTaskTexts,
            sharedTexts = previewSharedTexts,
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            closeContentDescription = "Cerrar",
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateTaskScreenRecurrentPreview() {
    val previewSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Nombre",
        fieldTitlePlaceholder = "Ej: Ir al gimnasio",
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
    )
    val previewTaskTexts = CreateTaskTexts(
        screenTitle = "Nueva tarea",
        typeBadge = "TAREA",
        saveButton = "Guardar tarea",
        successTitle = "Tarea creada con éxito",
        errorTitleEmpty = "",
        errorNoDate = "",
        errorNoDays = "",
        errorEndBeforeStart = "",
        errorServer = "",
        errorNetwork = "",
    )
    GoodLifeTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(
                title = "Estudiar Kotlin",
                schedulingMode = SchedulingMode.RECURRENT,
                recurrentDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                startDateDisplay = "10 de marzo, 2026",
                hasEndDate = true,
                endDateDisplay = "30 de junio, 2026",
                hasTime = true,
            ),
            onAction = {},
            createTaskTexts = previewTaskTexts,
            sharedTexts = previewSharedTexts,
            dayNames = listOf("L", "M", "X", "J", "V", "S", "D"),
            closeContentDescription = "Cerrar",
        )
    }
}
