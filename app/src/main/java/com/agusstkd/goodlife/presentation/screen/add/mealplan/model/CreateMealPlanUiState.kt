package com.agusstkd.goodlife.presentation.screen.add.mealplan.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealIngredientDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

enum class MealPlanWizardStep { SELECT_MEAL, MEAL_INGREDIENTS, SCHEDULE }

enum class MealDatePickerField { START_DATE, END_DATE }

@Stable
data class CreateMealPlanUiState(

    val currentStep: MealPlanWizardStep = MealPlanWizardStep.SELECT_MEAL,

    // ── Paso 1A: Catálogo de meals ──────────────────────────────────────
    val mealCatalog: ImmutableList<MealSummary> = emptyList<MealSummary>().toImmutableList(),
    val mealSearchQuery: String = "",
    val selectedMealTypeFilter: MealType? = null,
    val isLoadingMeals: Boolean = false,
    val mealCatalogPage: Int = 0,
    val mealCatalogHasMore: Boolean = true,

    // ── Paso 1B: Crear nueva meal ───────────────────────────────────────
    val isCreatingNewMeal: Boolean = false,
    val newMealName: String = "",
    val newMealDescription: String = "",
    val newMealType: MealType? = null,

    // ── Meal seleccionada ───────────────────────────────────────────────
    val selectedMeal: MealSummary? = null,
    val selectedMealId: Long? = null,

    // ── Paso 2: Ingredientes ────────────────────────────────────────────
    val ingredients: ImmutableList<MealIngredientDraft> = emptyList<MealIngredientDraft>().toImmutableList(),
    val ingredientCatalog: ImmutableList<Ingredient> = emptyList<Ingredient>().toImmutableList(),
    val ingredientSearchQuery: String = "",
    val isLoadingIngredients: Boolean = false,
    val ingredientPage: Int = 0,
    val ingredientHasMore: Boolean = true,
    val previewCalories: Double = 0.0,
    val previewProtein: Double = 0.0,
    val previewCarbs: Double = 0.0,
    val previewFat: Double = 0.0,

    // ── Paso 3: Planificación ───────────────────────────────────────────
    val mealType: MealType? = null,
    val selectedDays: ImmutableList<DayOfWeek> = emptyList<DayOfWeek>().toImmutableList(),
    val hasStartDate: Boolean = false,
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,

    // ── Estado general ──────────────────────────────────────────────────
    val showQuantityBottomSheet: Boolean = false,
    val editingIngredientId: Long? = null,
    val showCreateIngredientDialog: Boolean = false,
    val showTimePicker: Boolean = false,
    val activeDatePickerField: MealDatePickerField? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)
