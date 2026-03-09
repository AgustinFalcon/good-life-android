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
import com.agusstkd.goodlife.presentation.theme.TaskStyle
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import kotlinx.datetime.DayOfWeek


@Composable
fun CreateTaskScreen(
    modifier: Modifier = Modifier,
    onAction: (CreateTaskUiAction) -> Unit,
    uiState: CreateTaskUiState,
    createTaskTexts: CreateTaskTexts,
    sharedTexts: CreateItemSharedTexts,
    dayNames: List<String>,
) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

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
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        // Badge centrado — usá los colores de TaskStyle que ya existen
        Badge(
            containerColor = TaskStyle.badgeBackground,
            contentColor = TaskStyle.badgeText,
        ) {
            Text(
                text = createTaskTexts.typeBadge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // INPUTS
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = sharedTexts.fieldTitleLabel, style = MaterialTheme.typography.titleMedium)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.title,
                placeholder = sharedTexts.fieldTitlePlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateTaskUiAction.OnTitleChange(it)) },
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = sharedTexts.fieldDescriptionLabel, style = MaterialTheme.typography.titleMedium)
        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.description,
                placeholder = sharedTexts.fieldDescriptionPlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateTaskUiAction.OnDescriptionChange(it)) },
        )


        // DATES
        Text(text = sharedTexts.whenSectionTitle)

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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
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

                // From
                DateSelectorComponent(
                    params = DateSelectorParams(
                        displayText = uiState.startDateDisplay,
                        placeholderText = sharedTexts.fromDateLabel,
                    ),
                    onClick = { onAction(CreateTaskUiAction.OnDatePickerOpen(DatePickerField.START_DATE)) },
                )

                // Toggle end date
                SwitchComponent(
                    params = SwitchParams(
                        label = if (uiState.hasEndDate) sharedTexts.hasEndDate else sharedTexts.noEndDate,
                        checked = uiState.hasEndDate,
                    ),
                    onCheckedChange = { onAction(CreateTaskUiAction.OnHasEndDateToggle) },
                )

                // To
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

        // Toggle time
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

        // Active notification
        SwitchComponent(
            params = SwitchParams(
                label = sharedTexts.reminderLabel,
                checked = uiState.reminderEnabled,
            ),
            onCheckedChange = { onAction(CreateTaskUiAction.OnReminderChange(it)) },
        )

        Spacer(modifier = Modifier.height(24.dp))
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

    }
}
