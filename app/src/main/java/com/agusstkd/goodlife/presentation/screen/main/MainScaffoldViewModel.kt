package com.agusstkd.goodlife.presentation.screen.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.lifecycle.ViewModel
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.bottom.model.getDefaultBottomNavItems
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiAction
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiState
import com.agusstkd.goodlife.presentation.theme.EmeraldGreen
import com.agusstkd.goodlife.presentation.theme.InfoBlue
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import com.agusstkd.goodlife.presentation.theme.WarningOrange
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.format

/**
 * ViewModel del MainScaffold.
 *
 * Gestiona el estado del bottom navigation, modal de acciones rápidas
 * y la navegación a las pantallas de creación.
 *
 * @param navigationController Controlador de navegación para flujos de creación.
 * @param dateProvider Proveedor de fechas para el header del modal.
 * @param language Textos localizados de la aplicación.
 */
class MainScaffoldViewModel(
    private val navigationController: ComposeNavigationController,
    private val dateProvider: DateProvider,
    private val language: AppLanguage,
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
                navigateToQuickAction(action.action)
            }
            is MainScaffoldUiAction.OnMealOptionClick -> {
                _uiState.value = current.copy(isModalOpen = false)
            }
        }
    }

    /**
     * Navega a la pantalla de creación según el tipo de acción rápida seleccionada.
     */
    private fun navigateToQuickAction(type: QuickActionType) {
        when (type) {
            QuickActionType.TASK -> navigationController.navigateTo(AppRoute.CreateTask)
            QuickActionType.HABIT -> navigationController.navigateTo(AppRoute.CreateHabit)
            QuickActionType.WORKOUT -> navigationController.navigateTo(AppRoute.CreateRoutine)
            QuickActionType.MEAL -> { /* TODO: NavigateTo CreateMeal */ }
            QuickActionType.OTHER -> { /* TODO: NavigateTo Other */ }
        }
    }

    private fun buildQuickActions(): ImmutableList<QuickActionItem> {
        val t = language.mainScaffoldTexts
        return listOf(
            QuickActionItem(QuickActionType.TASK, t.task, Icons.Default.CheckBox, SuccessGreen),
            QuickActionItem(QuickActionType.HABIT, t.habit, Icons.Default.Repeat, InfoBlue),
            QuickActionItem(QuickActionType.WORKOUT, t.workout, Icons.Default.FitnessCenter, EmeraldGreen),
            QuickActionItem(QuickActionType.MEAL, t.meal, Icons.Default.Restaurant, WarningOrange),
            QuickActionItem(QuickActionType.OTHER, t.others, Icons.Default.MoreHoriz, WarningOrange),
        ).toImmutableList()
    }

    private fun buildMealOptions(): ImmutableList<MealOptionItem> {
        val t = language.mainScaffoldTexts
        return listOf(
            MealOptionItem(null, t.dailySummary, Icons.Default.WbSunny),
            MealOptionItem(MealType.BREAKFAST, t.breakfast, Icons.Default.LocalDining),
            MealOptionItem(MealType.LUNCH, t.lunch, Icons.Default.Dining),
            MealOptionItem(MealType.DINNER, t.dinner, Icons.Default.Dining),
            MealOptionItem(MealType.SNACK, t.snack, Icons.Default.LocalDining),
            MealOptionItem(MealType.PRE_WORKOUT, t.preWorkout, Icons.Default.FitnessCenter),
            MealOptionItem(MealType.POST_WORKOUT, t.postWorkout, Icons.Default.FitnessCenter)
        ).toImmutableList()
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