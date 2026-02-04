package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.lifecycle.ViewModel
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.plus

/**
 * ViewModel del tab Daily (tareas diarias).
 *
 * ## Responsabilidades:
 * - Mantener la fecha actual seleccionada
 * - Navegar entre días (anterior/siguiente)
 * - Formatear fechas para la UI (headerText, monthYear)
 *
 * ## Filosofía:
 * TODO el formateo de fechas se hace aquí.
 * La UI recibe strings listos para mostrar, sin lógica de fechas.
 */
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildUiState(dateProvider.today()))
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    fun onAction(action: DailyUiAction) {
        when (action) {
            DailyUiAction.OnPreviousDay -> navigateToPreviousDay()
            DailyUiAction.OnNextDay -> navigateToNextDay()
        }
    }

    private fun navigateToPreviousDay() {
        val newDate = _uiState.value.date.plus(-1, DateTimeUnit.DAY)
        _uiState.value = buildUiState(newDate)
    }

    private fun navigateToNextDay() {
        val newDate = _uiState.value.date.plus(1, DateTimeUnit.DAY)
        _uiState.value = buildUiState(newDate)
    }

    /**
     * Construye el UiState completo con todos los campos formateados.
     *
     * @param date Fecha a mostrar
     * @return UiState con headerText, dayNumber, monthYear, etc.
     */
    private fun buildUiState(date: LocalDate): DailyUiState {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()

        val isRelativeDate = date == today || date == yesterday || date == tomorrow

        val headerText = when (date) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> {
                // Formato: "Lun, 08 feb"
                date.format(language.formats.dayNameAndDate)
                    .replaceFirstChar { it.uppercase() }
            }
        }

        val monthYear = "${language.monthNames.names[date.monthNumber - 1]} ${date.year}"

        return DailyUiState(
            date = date,
            dayNumber = date.dayOfMonth,
            headerText = headerText,
            monthYear = monthYear,
            showFullDate = !isRelativeDate
        )
    }
}
