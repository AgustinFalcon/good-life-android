package com.agusstkd.goodlife.presentation.screen.add.mealplan.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.presentation.components.nutrition.IngredientCatalogSection
import com.agusstkd.goodlife.presentation.components.nutrition.MacrosSummaryCard
import com.agusstkd.goodlife.presentation.components.nutrition.MyIngredientsSection
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import kotlinx.collections.immutable.toImmutableList

/**
 * Paso 2 del wizard de creación de plan de comida.
 *
 * Muestra:
 * - [MacrosSummaryCard] fijo en la parte superior con el total de macros acumulados.
 * - [IngredientCatalogSection] con búsqueda, scroll infinito y botón de ingrediente custom.
 *
 * Tocar "+ Agregar" en un ingrediente abre el [QuantityBottomSheet] (manejado por ScreenOwner).
 * Tocar "+ Crear ingrediente custom" abre el [CreateIngredientDialog] (manejado por ScreenOwner).
 */
@Composable
fun MealIngredientsStep(
    uiState: CreateMealPlanUiState,
    onAction: (CreateMealPlanUiAction) -> Unit,
    texts: CreateMealPlanTexts,
    modifier: Modifier = Modifier,
) {
    // IDs de los ingredientes ya agregados a la meal (para mostrar ✓ en el catálogo)
    val addedIngredientIds = remember(uiState.ingredients) {
        uiState.ingredients.map { it.ingredient.id }.toImmutableList()
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Resumen de macros (header sticky) ──────────────────────────────
        MacrosSummaryCard(
            calories = uiState.previewCalories,
            protein = uiState.previewProtein,
            carbs = uiState.previewCarbs,
            fat = uiState.previewFat,
            proteinLabel = texts.macroProteinLabel,
            carbsLabel = texts.macroCarbsLabel,
            fatLabel = texts.macroFatLabel,
            decimalSeparator = texts.decimalSeparator,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // ── Mis ingredientes ────────────────────────────────────────
        MyIngredientsSection(
            modifier = Modifier.weight(1f),
            ingredients = uiState.ingredients,
            texts = texts,
            onRemove = { onAction(CreateMealPlanUiAction.OnRemoveIngredient(ingredientId = it)) }
        )

        // ── Catálogo de ingredientes ────────────────────────────────────────
        IngredientCatalogSection(
            modifier = Modifier.weight(1f),
            ingredients = uiState.ingredientCatalog,
            searchQuery = uiState.ingredientSearchQuery,
            isLoading = uiState.isLoadingIngredients,
            hasMore = uiState.ingredientHasMore,
            addedIngredientIds = addedIngredientIds,
            onSearchQueryChange = { onAction(CreateMealPlanUiAction.OnIngredientSearchQueryChange(it)) },
            onLoadMore = { onAction(CreateMealPlanUiAction.OnLoadMoreIngredients) },
            onAddClick = { ingredient: Ingredient ->
                onAction(CreateMealPlanUiAction.OnOpenQuantityBottomSheet(ingredient.id))
            },
            onCreateCustomClick = { onAction(CreateMealPlanUiAction.OnShowCreateIngredientDialog) },
            texts = texts,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "MealIngredientsStep — vacío")
@Composable
private fun MealIngredientsStepEmptyPreview() {
    GoodLifeTheme {
        MealIngredientsStep(
            uiState = previewMealPlanUiState(),
            onAction = {},
            texts = previewMealPlanTexts(),
        )
    }
}

@Preview(showBackground = true, name = "MealIngredientsStep — con ingredientes")
@Composable
private fun MealIngredientsStepWithDataPreview() {
    GoodLifeTheme {
        MealIngredientsStep(
            uiState = previewMealPlanUiState().copy(
                previewCalories = 420.0,
                previewProtein = 35.0,
                previewCarbs = 40.0,
                previewFat = 12.0,
                ingredientCatalog = previewIngredientCatalog(),
            ),
            onAction = {},
            texts = previewMealPlanTexts(),
        )
    }
}
