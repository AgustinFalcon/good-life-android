package com.agusstkd.goodlife.presentation.screen.add.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.CreateHabitTexts
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.HabitCategoryTexts
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.usecase.habit.CreateHabitUseCase
import com.agusstkd.goodlife.domain.usecase.habit.result.CreateHabitResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiAction
import com.agusstkd.goodlife.presentation.screen.add.habit.model.CreateHabitUiState
import com.agusstkd.goodlife.presentation.screen.add.habit.model.HabitDatePickerField
import com.agusstkd.goodlife.core.extensions.toggleDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * ViewModel para la pantalla de creación de hábito.
 *
 * Mantiene el estado del formulario, procesa las acciones del usuario,
 * y delega la creación al [CreateHabitUseCase].
 *
 * ## Textos localizados
 * Expone propiedades de texto que el Owner pasa al Screen:
 * - [createHabitTexts]: textos específicos de la pantalla.
 * - [sharedTexts]: labels compartidos con Create Task.
 * - [dayNames]: nombres cortos de días de la semana.
 * - [categoryEntries]: lista de (categoría, nombre localizado) para el selector.
 *
 * @property navigationController Controlador de navegación.
 * @property language Sistema de localización.
 * @property createHabitUseCase Caso de uso para crear hábito.
 */
class CreateHabitViewModel(
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
    private val createHabitUseCase: CreateHabitUseCase,
) : ViewModel() {

    val createHabitTexts: CreateHabitTexts get() = language.createHabitTexts
    val sharedTexts: CreateItemSharedTexts get() = language.createItemSharedTexts
    val accessibilityTexts get() = language.accessibilityTexts
    val datePickerTexts get() = language.datePickerTexts
    val dayNames: List<String> get() = language.dayNamesShort.names

    /** Lista de categorías con nombre localizado para el selector. */
    val categoryEntries: List<Pair<HabitCategory, String>> by lazy {
        buildCategoryEntries(language.habitCategoryTexts)
    }

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState: StateFlow<CreateHabitUiState> = _uiState.asStateFlow()

    /**
     * Punto de entrada para todas las acciones del usuario.
     */
    fun onAction(action: CreateHabitUiAction) {
        when (action) {
            is CreateHabitUiAction.OnNameChange ->
                _uiState.update { it.copy(name = action.name) }

            is CreateHabitUiAction.OnDescriptionChange ->
                _uiState.update { it.copy(description = action.description) }

            is CreateHabitUiAction.OnCategorySelected ->
                _uiState.update { it.copy(selectedCategory = action.category) }

            is CreateHabitUiAction.OnTargetValueChange ->
                _uiState.update { it.copy(targetValue = action.value) }

            is CreateHabitUiAction.OnUnitChange ->
                _uiState.update { it.copy(unit = action.unit) }

            is CreateHabitUiAction.OnDayToggled -> toggleDay(action.day)

            CreateHabitUiAction.OnHasEndDateToggle ->
                _uiState.update { it.copy(hasEndDate = !it.hasEndDate) }

            CreateHabitUiAction.OnHasTimeToggle ->
                _uiState.update { it.copy(hasTime = !it.hasTime) }

            is CreateHabitUiAction.OnDatePickerOpen ->
                _uiState.update { it.copy(activeDatePickerField = action.field) }

            CreateHabitUiAction.OnDatePickerDismiss ->
                _uiState.update { it.copy(activeDatePickerField = null) }

            is CreateHabitUiAction.OnDateSelected -> dateSelected(action.field, action.date)

            CreateHabitUiAction.OnTimePickerOpen ->
                _uiState.update { it.copy(showTimePicker = true) }

            CreateHabitUiAction.OnTimePickerDismiss ->
                _uiState.update { it.copy(showTimePicker = false) }

            is CreateHabitUiAction.OnTimeSelected ->
                _uiState.update { it.copy(scheduledTime = action.time, showTimePicker = false) }

            CreateHabitUiAction.OnSubmit -> submitHabit()

            CreateHabitUiAction.OnErrorDismissed ->
                _uiState.update { it.copy(errorMessage = null) }

            CreateHabitUiAction.OnSuccessAnimationFinished ->
                navigationController.navigateUp()

            CreateHabitUiAction.OnDismiss ->
                navigationController.navigateUp()
        }
    }

    private fun toggleDay(day: DayOfWeek) {
        _uiState.update { it.copy(selectedDays = it.selectedDays.toggleDay(day)) }
    }

    private fun dateSelected(field: HabitDatePickerField, date: LocalDate) {
        val display = language.formats.full.format(date)
        _uiState.update { current ->
            when (field) {
                HabitDatePickerField.START_DATE -> current.copy(
                    startDate = date,
                    startDateDisplay = display,
                    activeDatePickerField = null,
                )

                HabitDatePickerField.END_DATE -> current.copy(
                    endDate = date,
                    endDateDisplay = display,
                    activeDatePickerField = null,
                )
            }
        }
    }

    private fun submitHabit() {
        val state = _uiState.value
        val parsedTarget = state.targetValue.toIntOrNull() ?: 0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasAttemptedSubmit = true) }

            val result = createHabitUseCase(
                name = state.name,
                description = state.description.takeIf { it.isNotBlank() },
                category = state.selectedCategory ?: HabitCategory.CUSTOM,
                targetValue = parsedTarget,
                unit = state.unit,
                daysOfWeek = state.selectedDays,
                startDate = state.startDate ?: return@launch,
                endDate = if (state.hasEndDate) state.endDate else null,
                scheduledTime = if (state.hasTime) state.scheduledTime else null,
            )

            when (result) {
                is CreateHabitResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }

                is CreateHabitResult.ValidationError -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }

                is CreateHabitResult.ServerError -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }

                is CreateHabitResult.NetworkError -> {
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

    private fun buildCategoryEntries(texts: HabitCategoryTexts): List<Pair<HabitCategory, String>> {
        return listOf(
            HabitCategory.HYDRATION to texts.hydration,
            HabitCategory.MEDITATION to texts.meditation,
            HabitCategory.READING to texts.reading,
            HabitCategory.EXERCISE to texts.exercise,
            HabitCategory.SLEEP to texts.sleep,
            HabitCategory.NUTRITION to texts.nutrition,
            HabitCategory.LEARNING to texts.learning,
            HabitCategory.MINDFULNESS to texts.mindfulness,
            HabitCategory.SOCIAL to texts.social,
            HabitCategory.CREATIVITY to texts.creativity,
            HabitCategory.PRODUCTIVITY to texts.productivity,
            HabitCategory.HEALTH to texts.health,
            HabitCategory.CUSTOM to texts.custom,
        )
    }
}
