package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.lifecycle.ViewModel
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.MealIngredientDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateMealPlanUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealPlanWizardStep
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel del wizard de creación de Plan de Comida.
 *
 * Sigue el patrón MVI: expone un único [uiState] inmutable y recibe todas las
 * interacciones del usuario a través de [onAction].
 *
 * El wizard tiene 3 pasos representados por [MealPlanWizardStep]:
 * - **SELECT_MEAL**: el usuario elige una meal existente o crea una nueva.
 * - **MEAL_INGREDIENTS**: agrega ingredientes y ve los macros en tiempo real.
 * - **SCHEDULE**: configura tipo, días, fechas y hora del plan.
 *
 * Responsabilidades principales:
 * - Validar cada paso antes de avanzar ([advanceWizardStep]).
 * - Gestionar la navegación hacia atrás entre pasos ([recoilWizardStep]).
 * - Recalcular macros en tiempo real al agregar/quitar ingredientes.
 * - Coordinar el submit: crear la meal (si es custom) y luego el plan.
 *
 * @param navigationController Controlador de navegación de Compose.
 * @param language Fuente de textos localizados de la app.
 * @param createMealPlanUseCase Caso de uso que crea el plan en el backend.
 *
 * @see CreateMealPlanUiState
 * @see CreateMealPlanUiAction
 */
class CreateMealPlanViewModel(
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
    private val createMealPlanUseCase: CreateMealPlanUseCase,
) : ViewModel() {

    val texts: CreateMealPlanTexts get() = language.createMealPlanTexts
    val sharedTexts: CreateItemSharedTexts get() = language.createItemSharedTexts
    val accessibilityTexts get() = language.accessibilityTexts
    val dayNames: List<String> get() = language.dayNamesShort.names

    val mealTypeEntries: List<Pair<MealType, String>> by lazy {
        val t = language.mealTypeTexts
        listOf(
            MealType.BREAKFAST    to t.breakfast,
            MealType.LUNCH        to t.lunch,
            MealType.DINNER       to t.dinner,
            MealType.SNACK        to t.snack,
            MealType.PRE_WORKOUT  to t.preWorkout,
            MealType.POST_WORKOUT to t.postWorkout,
        )
    }

    private val _uiState = MutableStateFlow(CreateMealPlanUiState())
    val uiState: StateFlow<CreateMealPlanUiState> = _uiState.asStateFlow()

    fun onAction(action: CreateMealPlanUiAction) {
        when (action) {

            // ── Navegación del wizard ────────────────────────────────────────
            is CreateMealPlanUiAction.OnNextStep     -> advanceWizardStep()
            is CreateMealPlanUiAction.OnPreviousStep -> recoilWizardStep()
            is CreateMealPlanUiAction.OnDismiss      -> navigationController.navigateUp()

            // ── Paso 1A: Catálogo de meals ───────────────────────────────────
            is CreateMealPlanUiAction.OnSelectMeal -> {
                _uiState.update {
                    it.copy(
                        selectedMeal = action.meal,
                        currentStep  = MealPlanWizardStep.SCHEDULE,
                    )
                }
            }
            is CreateMealPlanUiAction.OnMealTypeFilterChange -> {
                _uiState.update { it.copy(selectedMealTypeFilter = action.mealType) }
            }

            // ── Paso 1B: Nueva meal ──────────────────────────────────────────
            is CreateMealPlanUiAction.OnCreateNewMeal -> {
                _uiState.update { it.copy(isCreatingNewMeal = true) }
            }
            is CreateMealPlanUiAction.OnNewMealNameChange -> {
                _uiState.update { it.copy(newMealName = action.name) }
            }
            is CreateMealPlanUiAction.OnNewMealDescriptionChange -> {
                _uiState.update { it.copy(newMealDescription = action.description) }
            }
            is CreateMealPlanUiAction.OnNewMealTypeChange -> {
                _uiState.update { it.copy(newMealType = action.newMealType) }
            }

            // ── Paso 2: Ingredientes ─────────────────────────────────────────
            is CreateMealPlanUiAction.OnOpenQuantityBottomSheet -> {
                updateStatusQuantityBottomSheet(
                    ingredientId = action.ingredientId,
                    showQuantityBottomSheet = true,
                )
            }
            is CreateMealPlanUiAction.OnDismissQuantityBottomSheet -> {
                updateStatusQuantityBottomSheet(
                    ingredientId = null,
                    showQuantityBottomSheet = false,
                )
            }
            is CreateMealPlanUiAction.OnConfirmIngredientQuantity -> {
                updateIngredientQuantity(
                    ingredientId = action.ingredientId,
                    quantity     = action.quantity,
                    unit         = action.unit,
                )
            }

            // ── Paso 3: Planificación ────────────────────────────────────────
            is CreateMealPlanUiAction.OnMealTypeSelected -> {
                _uiState.update { it.copy(mealType = action.mealType) }
            }

            // ── Submit ───────────────────────────────────────────────────────
            is CreateMealPlanUiAction.OnErrorDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            is CreateMealPlanUiAction.OnSuccessAnimationFinished -> {
                navigationController.navigateUp()
            }

            else -> {}
        }
    }

    // ── Navegación entre pasos ───────────────────────────────────────────────

    private fun advanceWizardStep() {
        val state = _uiState.value

        // Guard clauses: validamos primero, salimos en error
        when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL -> {
                if (state.isCreatingNewMeal && state.newMealName.isBlank()) {
                    _uiState.update { it.copy(errorMessage = texts.errorMealNameEmpty) }
                    return
                }
            }
            MealPlanWizardStep.MEAL_INGREDIENTS -> {
                if (state.ingredients.isEmpty()) {
                    _uiState.update { it.copy(errorMessage = texts.errorNoIngredients) }
                    return
                }
            }
            MealPlanWizardStep.SCHEDULE -> return
        }

        // when como expresión: el paso siguiente se asigna directo a un val
        val nextStep = when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL      -> MealPlanWizardStep.MEAL_INGREDIENTS
            MealPlanWizardStep.MEAL_INGREDIENTS -> MealPlanWizardStep.SCHEDULE
            MealPlanWizardStep.SCHEDULE         -> return
        }

        _uiState.update { it.copy(currentStep = nextStep) }
    }

    private fun recoilWizardStep() {
        val state = _uiState.value

        // Primer paso: cerrar el wizard
        if (state.currentStep == MealPlanWizardStep.SELECT_MEAL) {
            navigationController.navigateUp()
            return
        }

        val previousStep = when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL      -> return
            MealPlanWizardStep.MEAL_INGREDIENTS -> MealPlanWizardStep.SELECT_MEAL
            MealPlanWizardStep.SCHEDULE         -> {
                // Si vino por meal existente (salteó Paso 2), vuelve al Paso 1
                if (state.isCreatingNewMeal) MealPlanWizardStep.MEAL_INGREDIENTS
                else MealPlanWizardStep.SELECT_MEAL
            }
        }

        _uiState.update { it.copy(currentStep = previousStep) }
    }

    // ── Helpers privados ─────────────────────────────────────────────────────
    private fun updateIngredientQuantity(ingredientId: Long, quantity: Double, unit: String) {
        val ingredient = _uiState.value.ingredientCatalog.find { it.id == ingredientId } ?: return
        val draft = MealIngredientDraft(ingredient = ingredient, quantity = quantity, unit = unit)

        _uiState.update { state ->
            val updated = (state.ingredients + draft).toImmutableList()
            state.copy(
                ingredients             = updated,
                previewCalories         = updated.sumOf { it.scaledCalories },
                previewProtein          = updated.sumOf { it.scaledProtein },
                previewCarbs            = updated.sumOf { it.scaledCarbs },
                previewFat              = updated.sumOf { it.scaledFat },
                showQuantityBottomSheet = false,
                editingIngredientId     = null,
            )
        }
    }

    private fun updateStatusQuantityBottomSheet(ingredientId: Long?, showQuantityBottomSheet: Boolean) {
        _uiState.update {
            it.copy(
                editingIngredientId     = ingredientId,
                showQuantityBottomSheet = showQuantityBottomSheet,
            )
        }
    }
}
