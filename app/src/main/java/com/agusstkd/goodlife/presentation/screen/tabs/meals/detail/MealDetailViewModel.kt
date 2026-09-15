package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.result.GetMealPlansResult
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/** Loads one meal plan from the authenticated user's daily meal-plan collection. */
class MealDetailViewModel(
    private val planId: Long,
    private val dateIso: String,
    private val language: AppLanguage,
    private val getMealPlansUseCase: GetMealPlansUseCase,
) : ViewModel() {
    private var loadJob: Job? = null
    private var latestRequest = 0L

    private val _uiState = MutableStateFlow<MealDetailUiState>(MealDetailUiState.Loading)
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()
    val texts: MealDetailTexts = language.mealDetailTexts()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        val date = validRouteDateOrNull()
        if (date == null) {
            loadJob?.cancel()
            _uiState.value = MealDetailUiState.InvalidRoute
            return
        }

        val request = ++latestRequest
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MealDetailUiState.Loading
            val state = when (val result = getMealPlansUseCase(date)) {
                is GetMealPlansResult.Success -> result.plans.firstOrNull { it.id == planId }
                    ?.let(::content) ?: MealDetailUiState.NotFound
                GetMealPlansResult.NotFound -> MealDetailUiState.NotFound
                is GetMealPlansResult.ServerError -> MealDetailUiState.Error(language.errorTexts.dataLoadError)
                GetMealPlansResult.NetworkError -> MealDetailUiState.Error(language.errorTexts.connectionError)
            }
            if (request == latestRequest) _uiState.value = state
        }
    }

    private fun validRouteDateOrNull(): LocalDate? {
        if (planId <= 0) return null
        return runCatching { LocalDate.parse(dateIso) }
            .getOrNull()
            ?.takeIf { it.toString() == dateIso }
    }

    private fun content(plan: DailyMealPlanSummary): MealDetailUiState.Content =
        MealDetailUiState.Content(
            planName = plan.name,
            mealTypeLabel = mealTypeLabel(plan.mealType),
            scheduledTimeLabel = plan.scheduledTime?.let {
                "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}"
            },
            mealName = plan.mealName?.takeIf { it.isNotBlank() },
            imageUrl = plan.imageUrl?.takeIf { it.isNotBlank() },
            totalCalories = plan.totalCalories,
            totalProtein = plan.totalProtein,
            totalCarbs = plan.totalCarbs,
            totalFat = plan.totalFat,
            isActive = plan.isActive,
        )

    private fun mealTypeLabel(mealType: MealType): String = with(language.mealTypeTexts) {
        when (mealType) {
            MealType.BREAKFAST -> breakfast
            MealType.LUNCH -> lunch
            MealType.DINNER -> dinner
            MealType.SNACK -> snack
            MealType.PRE_WORKOUT -> preWorkout
            MealType.POST_WORKOUT -> postWorkout
        }
    }
}
