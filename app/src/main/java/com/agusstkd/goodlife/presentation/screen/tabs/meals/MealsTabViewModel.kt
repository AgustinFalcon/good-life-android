package com.agusstkd.goodlife.presentation.screen.tabs.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.AccessibilityTexts
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.result.GetMealPlansResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealPlanUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * ViewModel del tab Meals (nutrición diaria).
 *
 * ## Responsabilidades:
 * - Mantener la fecha navegada ([currentDate])
 * - Cargar planes del backend usando [GetMealPlansUseCase]
 * - Formatear fechas para UI (headerText, monthYear)
 * - Calcular totales de macros del día
 * - Mapear Domain → UiModel
 * - Navegar a CreateMealPlan y MealDetail
 *
 * ## Flujo de datos:
 * ```
 * loadMealPlans()
 *     ↓
 * GetMealPlansUseCase(date)           ← dueño del dispatcher IO
 *     ↓
 * MealPlanRepository.getMealPlansByDate(date)
 *     ↓
 * MealPlanRemoteDataSource → MealPlanApiService → Backend
 *     ↓
 * GetMealPlansResult (subtipo semántico)
 *     ↓
 * MealsUiState.Success | MealsUiState.Empty | MealsUiState.Error
 * ```
 *
 * ## Sobre el dispatcher:
 * Este ViewModel NO recibe ni usa [DispatcherProvider].
 * El dispatcher es responsabilidad exclusiva del UseCase (decisión arquitectónica #1).
 */
class MealsTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage,
    private val getMealPlansUseCase: GetMealPlansUseCase,
    private val navigationController: ComposeNavigationController,
) : ViewModel() {

    val accessibilityTexts: AccessibilityTexts get() = language.accessibilityTexts

    private var currentDate: LocalDate = dateProvider.today()
    private var latestLoadRequestId: Long = 0L

    private val _uiState = MutableStateFlow<MealsUiState>(MealsUiState.Loading)
    val uiState: StateFlow<MealsUiState> = _uiState
        .onSubscription { loadMealPlans() }
        .stateIn(viewModelScope, SharingStarted.Lazily, MealsUiState.Loading)

    fun refresh() {
        loadMealPlans()
    }

    fun onAction(action: MealsUiAction) {
        when (action) {
            MealsUiAction.OnPreviousDay -> navigateToPreviousDay()
            MealsUiAction.OnNextDay -> navigateToNextDay()
            MealsUiAction.OnRefresh -> loadMealPlans()
            MealsUiAction.OnCreateMealPlan -> navigateToCreateMealPlan()
            is MealsUiAction.OnMealPlanClick -> navigateToMealDetail(action.planId)
        }
    }

    /**
     * Carga los planes de comida del día actual desde el backend.
     *
     * Muestra [MealsUiState.Loading] mientras se hace la request.
     */
    private fun loadMealPlans() {
        val requestedDate = currentDate
        val requestId = ++latestLoadRequestId
        viewModelScope.launch {
            if (!isCurrentLoadRequest(requestId, requestedDate)) return@launch
            _uiState.value = MealsUiState.Loading

            val result = getMealPlansUseCase(requestedDate)
            if (!isCurrentLoadRequest(requestId, requestedDate)) return@launch

            when (result) {
                is GetMealPlansResult.Success -> _uiState.value = buildSuccessState(result.plans, requestedDate)
                is GetMealPlansResult.NotFound -> _uiState.value = MealsUiState.Empty
                is GetMealPlansResult.ServerError -> _uiState.value = MealsUiState.Error(language.errorTexts.dataLoadError)
                is GetMealPlansResult.NetworkError -> _uiState.value = MealsUiState.Error(language.errorTexts.connectionError)
            }
        }
    }

    private fun isCurrentLoadRequest(requestId: Long, requestedDate: LocalDate): Boolean {
        return requestId == latestLoadRequestId && requestedDate == currentDate
    }

    private fun navigateToPreviousDay() {
        currentDate = currentDate.minus(1, DateTimeUnit.DAY)
        loadMealPlans()
    }

    private fun navigateToNextDay() {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        loadMealPlans()
    }

    private fun navigateToCreateMealPlan() {
        navigationController.navigateTo(AppRoute.CreateMealPlan)
    }

    private fun navigateToMealDetail(planId: Long) {
        // TODO: Implementar navegación a detalle de plan de comida
    }

    /**
     * Construye [MealsUiState.Success] con todos los campos formateados.
     *
     * Mapea lista de [DailyMealPlanSummary] (Domain) → [MealsUiState.Success] (UI).
     */
    private fun buildSuccessState(plans: List<DailyMealPlanSummary>, date: LocalDate): MealsUiState.Success {
        val uiModels = plans.map { it.toUiModel(resolveMealTypeLabel(it.mealType)) }
        return MealsUiState.Success(
            date = date,
            dayNumber = date.day,
            headerText = formatHeaderText(date),
            monthYear = formatMonthYear(date),
            showFullDate = !isRelativeDate(date),
            mealPlans = uiModels,
            totalCalories = plans.sumOf { it.totalCalories },
            totalProtein = plans.sumOf { it.totalProtein },
            totalCarbs = plans.sumOf { it.totalCarbs },
            totalFat = plans.sumOf { it.totalFat },
            isRefreshing = false,
        )
    }

    private fun resolveMealTypeLabel(mealType: MealType): String {
        val texts = language.mealTypeTexts
        return when (mealType) {
            MealType.BREAKFAST -> texts.breakfast
            MealType.LUNCH -> texts.lunch
            MealType.DINNER -> texts.dinner
            MealType.SNACK -> texts.snack
            MealType.PRE_WORKOUT -> texts.preWorkout
            MealType.POST_WORKOUT -> texts.postWorkout
        }
    }

    private fun formatHeaderText(date: LocalDate): String {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()
        return when (date) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> date.format(language.formats.dayNameAndDate)
                .replaceFirstChar { it.uppercase() }
        }
    }

    private fun formatMonthYear(date: LocalDate): String {
        return "${language.monthNames.names[date.month.ordinal]} ${date.year}"
    }

    private fun isRelativeDate(date: LocalDate): Boolean {
        val today = dateProvider.today()
        return date == today || date == dateProvider.yesterday() || date == dateProvider.tomorrow()
    }
}
