package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.presentation.components.nutrition.QuantityBottomSheet
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateMealPlanScreenOwner(
    modifier: Modifier = Modifier,
    viewModel: CreateMealPlanViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {

        // ── 1. Pantalla principal del wizard ──
        CreateMealPlanScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            texts = viewModel.texts,
            sharedTexts = viewModel.sharedTexts,
            mealTypeEntries = viewModel.mealTypeEntries,
            dayNames = viewModel.dayNames,
            closeContentDescription = viewModel.accessibilityTexts.close,
        )

        // TODO: DatePicker bottom sheet (activeDatePickerField)
        // TODO: TimePicker dialog (showTimePicker)
        // TODO: QuantityBottomSheet (showQuantityBottomSheet)

        // Buscar ingrediente seleccionado
        val ingredient: Ingredient? = uiState.editingIngredientId?.let { id ->
            uiState.ingredientCatalog.find { it.id == id }
        }
        if (uiState.showQuantityBottomSheet && ingredient != null) {
            QuantityBottomSheet(
                ingredient = ingredient,
                texts = viewModel.texts,
                onDismiss = { viewModel.onAction(CreateMealPlanUiAction.OnDismissQuantityBottomSheet) },
                onConfirm = { quantity, unit ->
                    viewModel.onAction(
                        CreateMealPlanUiAction.OnConfirmIngredientQuantity(
                            quantity = quantity,
                            unit = unit,
                            ingredientId = ingredient.id
                        )
                    )
                },
            )
        }
        // TODO: CreateIngredientDialog (showCreateIngredientDialog)
        // TODO: PopupResultComponent (isLoading / isSuccess / errorMessage)
    }
}
