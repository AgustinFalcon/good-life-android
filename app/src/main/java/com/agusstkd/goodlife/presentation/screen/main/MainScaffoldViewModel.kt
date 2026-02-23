package com.agusstkd.goodlife.presentation.screen.main

import androidx.lifecycle.ViewModel
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.bottom.model.getDefaultBottomNavItems
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiAction
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiState
import com.agusstkd.goodlife.presentation.theme.EmeraldGreen
import com.agusstkd.goodlife.presentation.theme.InfoBlue
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import com.agusstkd.goodlife.presentation.theme.WarningOrange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Dining
import kotlinx.datetime.format

class MainScaffoldViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainScaffoldUiState(
            bottomNavItems = getDefaultBottomNavItems(language.mainScaffoldTexts, language.accessibilityTexts),
            currentDateFormatted = formatModalHeaderDate(),
            quickActions = buildQuickActions(),
            mealOptions = buildMealOptions(),
            fabContentDescription = language.accessibilityTexts.add
        )
    )
    val uiState: StateFlow<MainScaffoldUiState> = _uiState.asStateFlow()

    fun onAction(action: MainScaffoldUiAction) {
        val current = _uiState.value

        when (action) {
            is MainScaffoldUiAction.OnTabSelected -> {
                _uiState.value = current.copy(
                    selectedTab = action.tab,
                    isModalOpen = false
                )
            }
            MainScaffoldUiAction.OnFabClick -> {
                _uiState.value = current.copy(isModalOpen = true)
            }
            MainScaffoldUiAction.OnModalDismiss -> {
                _uiState.value = current.copy(isModalOpen = false)
            }
            is MainScaffoldUiAction.OnQuickActionClick -> {
                _uiState.value = current.copy(isModalOpen = false)
            }
            is MainScaffoldUiAction.OnMealOptionClick -> {
                _uiState.value = current.copy(isModalOpen = false)
            }
        }
    }

    private fun buildQuickActions(): List<QuickActionItem> {
        val t = language.mainScaffoldTexts
        return listOf(
            QuickActionItem(QuickActionType.ROUTINE, t.routine, Icons.Default.FitnessCenter, SuccessGreen),
            QuickActionItem(QuickActionType.NUTRITION, t.nutrition, Icons.Default.LocalDining, WarningOrange),
            QuickActionItem(QuickActionType.WEIGHT, t.weight, Icons.Default.MonitorWeight, InfoBlue),
            QuickActionItem(QuickActionType.SUPPLEMENTS, t.supplements, Icons.Default.Medication, EmeraldGreen),
            QuickActionItem(QuickActionType.ACTIVITY, t.activity, Icons.Default.DirectionsRun, LightGreen),
            QuickActionItem(QuickActionType.OTHER, t.others, Icons.Default.MoreHoriz, WarningOrange)
        )
    }

    private fun buildMealOptions(): List<MealOptionItem> {
        val t = language.mainScaffoldTexts
        return listOf(
            MealOptionItem(null, t.dailySummary, Icons.Default.WbSunny),
            MealOptionItem(MealType.BREAKFAST, t.breakfast, Icons.Default.LocalDining),
            MealOptionItem(MealType.LUNCH, t.lunch, Icons.Default.Dining),
            MealOptionItem(MealType.DINNER, t.dinner, Icons.Default.Dining),
            MealOptionItem(MealType.SNACK, t.snack, Icons.Default.LocalDining),
            MealOptionItem(MealType.PRE_WORKOUT, t.preWorkout, Icons.Default.FitnessCenter),
            MealOptionItem(MealType.POST_WORKOUT, t.postWorkout, Icons.Default.FitnessCenter)
        )
    }

    /**
     * Formatea la fecha para el header del modal de acciones.
     *
     * Ejemplo: "Hoy, 3 de febrero de 2026"
     */
    private fun formatModalHeaderDate(): String {
        val today = dateProvider.today()
        val texts = language.relativeTexts

        val prefix = when (today) {
            dateProvider.today() -> "${texts.today}, "
            dateProvider.yesterday() -> "${texts.yesterday}, "
            dateProvider.tomorrow() -> "${texts.tomorrow}, "
            else -> ""
        }

        return prefix + today.format(language.formats.full)
    }
}