package com.agusstkd.goodlife.presentation.screen.add.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.CreateTaskTexts
import com.agusstkd.goodlife.domain.usecase.task.CreateTaskUseCase
import com.agusstkd.goodlife.domain.usecase.task.result.CreateTaskResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiAction
import com.agusstkd.goodlife.presentation.screen.add.task.model.CreateTaskUiState
import com.agusstkd.goodlife.presentation.screen.add.task.model.DatePickerField
import com.agusstkd.goodlife.presentation.screen.add.task.model.SchedulingMode
import com.agusstkd.goodlife.core.extensions.toggleDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class CreateTaskViewModel(
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
    private val createTaskUseCase: CreateTaskUseCase,
) : ViewModel() {


    val createTaskTexts: CreateTaskTexts get() = language.createTaskTexts
    val accessibilityTexts get() = language.accessibilityTexts

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    val sharedTexts get() = language.createItemSharedTexts
    val datePickerTexts get() = language.datePickerTexts
    val dayNames: List<String> get() = language.dayNamesShort.names


    fun onAction(action: CreateTaskUiAction) {
        when (action) {

            is CreateTaskUiAction.OnTitleChange -> titleChange(action.title)
            is CreateTaskUiAction.OnDescriptionChange -> descriptionChange(action.description)

            // ONCE or RECURRENT
            is CreateTaskUiAction.OnSchedulingModeChange -> schedulingModeChange(action.mode)
            is CreateTaskUiAction.OnDatePickerOpen -> openDatePicker(action.field)
            CreateTaskUiAction.OnDatePickerDismiss -> openDatePicker(null)

            // TOGGLES
            is CreateTaskUiAction.OnDayToggled -> changeDaySelected(action.day)
            CreateTaskUiAction.OnHasEndDateToggle -> hasEndDate()
            CreateTaskUiAction.OnHasTimeToggle -> hasTime()
            is CreateTaskUiAction.OnReminderChange -> reminderChange(action.enabled)


            is CreateTaskUiAction.OnDateSelected -> dateSelected(action.field, action.date)

            CreateTaskUiAction.OnErrorDismissed -> _uiState.update { it.copy(errorMessage = null) }
            CreateTaskUiAction.OnSuccessAnimationFinished -> navigationController.navigateUp()
            CreateTaskUiAction.OnDismiss -> navigationController.navigateUp()
            CreateTaskUiAction.OnSubmit -> submitTask()

            CreateTaskUiAction.OnTimePickerOpen -> _uiState.update { it.copy(showTimePicker = true) }
            CreateTaskUiAction.OnTimePickerDismiss -> _uiState.update { it.copy(showTimePicker = false) }
            is CreateTaskUiAction.OnTimeSelected -> timeSelected(action.time)
        }
    }

    private fun timeSelected(time: LocalTime) {
        _uiState.update { it.copy(scheduledTime = time, showTimePicker = false) }
    }

    private fun submitTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasAttemptedSubmit = true) }

            when (val result = createTaskUseCase(
                title = uiState.value.title,
                description = uiState.value.description.takeIf { it.isNotBlank() },
                scheduleDate = uiState.value.scheduledDate,
                recurrentDays = uiState.value.recurrentDays,
                startDate = uiState.value.startDate,
                endDate = uiState.value.endDate,
                scheduledTime = uiState.value.scheduledTime
            )) {
                is CreateTaskResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }

                is CreateTaskResult.ValidationError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isSuccess = false
                        )
                    }
                }

                is CreateTaskResult.ServerError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isSuccess = false
                        )
                    }
                }

                is CreateTaskResult.NetworkError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = language.errorTexts.connectionError,
                        )
                    }
                }
            }
        }
    }


    private fun reminderChange(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    private fun hasTime() {
        _uiState.update { it.copy(hasTime = !it.hasTime) }
    }

    private fun hasEndDate() {
        _uiState.update { it.copy(hasEndDate = !it.hasEndDate) }
    }

    private fun dateSelected(field: DatePickerField, date: LocalDate) {
        val display = language.formats.full.format(date)
        _uiState.update { current ->
            when (field) {
                DatePickerField.SCHEDULED_DATE -> current.copy(
                    scheduledDate = date,
                    scheduledDateDisplay = display,
                    activeDatePickerField = null
                )

                DatePickerField.START_DATE -> current.copy(
                    startDate = date,
                    startDateDisplay = display,
                    activeDatePickerField = null
                )

                DatePickerField.END_DATE -> current.copy(
                    endDate = date,
                    endDateDisplay = display,
                    activeDatePickerField = null
                )
            }
        }
    }

    private fun openDatePicker(field: DatePickerField?) {
        _uiState.update { it.copy(activeDatePickerField = field) }
    }

    private fun schedulingModeChange(mode: SchedulingMode) {
        _uiState.update { it.copy(schedulingMode = mode) }
    }

    private fun changeDaySelected(day: DayOfWeek) {
        _uiState.update { it.copy(recurrentDays = it.recurrentDays.toggleDay(day)) }
    }

    private fun descriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    private fun titleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }
}
