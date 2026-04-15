package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultComponent
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultParams
import com.agusstkd.goodlife.presentation.components.dialog.PopupResultState
import com.agusstkd.goodlife.presentation.components.dialog.CreationItemType
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialog
import com.agusstkd.goodlife.presentation.components.dialog.SuccessDialogParams
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogComponent
import com.agusstkd.goodlife.presentation.components.dialog.TimePickerDialogParams
import com.agusstkd.goodlife.presentation.components.nutrition.CreateIngredientDialog
import com.agusstkd.goodlife.presentation.components.nutrition.QuantityBottomSheet
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealDatePickerField
import org.koin.androidx.compose.koinViewModel

/**
 * Composable raíz del wizard de creación de Plan de Comida.
 *
 * Actúa como orquestador de overlays: obtiene el [CreateMealPlanViewModel] via Koin,
 * observa el [CreateMealPlanUiState] y decide qué componentes superpuestos mostrar
 * encima de la pantalla principal [CreateMealPlanScreen].
 *
 * ### Overlays gestionados
 * - **DatePickerBottomSheetComponent** — se muestra cuando [CreateMealPlanUiState.activeDatePickerField] no es `null`.
 * - **TimePickerDialogComponent** — se muestra cuando [CreateMealPlanUiState.showTimePicker] es `true`.
 * - **QuantityBottomSheet** — se muestra cuando [CreateMealPlanUiState.showQuantityBottomSheet] es `true` y hay un ingrediente en edición.
 * - **CreateIngredientDialog** — pendiente de implementar; se mostrará cuando [CreateMealPlanUiState.showCreateIngredientDialog] sea `true`.
 * - **PopupResultComponent** (LOADING / ERROR) — se muestra durante la carga o ante cualquier error.
 * - **SuccessDialog** — se muestra cuando [CreateMealPlanUiState.isSuccess] es `true`; al terminar la animación dispara [CreateMealPlanUiAction.OnSuccessAnimationFinished].
 *
 * @param modifier Modifier aplicado al [Box] raíz.
 * @param viewModel ViewModel del wizard; por defecto se obtiene via Koin.
 */
@Composable
fun CreateMealPlanScreenOwner(
    modifier: Modifier = Modifier,
    viewModel: CreateMealPlanViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {

        // ── 1. Pantalla principal del wizard ──────────────────────────────────
        CreateMealPlanScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            texts = viewModel.texts,
            sharedTexts = viewModel.sharedTexts,
            mealTypeEntries = viewModel.mealTypeEntries,
            dayNames = viewModel.dayNames,
            closeContentDescription = viewModel.accessibilityTexts.close,
        )

        // ── 2. Date picker (inicio / fin) ─────────────────────────────────────
        uiState.activeDatePickerField?.let { field ->
            DatePickerBottomSheetComponent(
                texts = viewModel.datePickerTexts,
                initialDate = when (field) {
                    MealDatePickerField.START_DATE -> uiState.startDate
                    MealDatePickerField.END_DATE -> uiState.endDate
                },
                onDateSelected = { date ->
                    viewModel.onAction(CreateMealPlanUiAction.OnDateSelected(field = field, date = date))
                },
                onDismiss = {
                    viewModel.onAction(CreateMealPlanUiAction.OnDatePickerDismiss)
                },
            )
        }

        // ── 3. Time picker ────────────────────────────────────────────────────
        if (uiState.showTimePicker) {
            TimePickerDialogComponent(
                params = TimePickerDialogParams(
                    initialHour = uiState.scheduledTime?.hour ?: 9,
                    initialMinute = uiState.scheduledTime?.minute ?: 0,
                    confirmLabel = viewModel.sharedTexts.confirmLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onTimeSelected = { time ->
                    viewModel.onAction(CreateMealPlanUiAction.OnTimeSelected(time))
                },
                onDismiss = {
                    viewModel.onAction(CreateMealPlanUiAction.OnTimePickerDismiss)
                },
            )
        }

        // ── 4. Quantity bottom sheet (ingrediente seleccionado) ───────────────
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
                            ingredientId = ingredient.id,
                            quantity = quantity,
                            unit = unit,
                        )
                    )
                },
            )
        }

        // ── 5. Create ingredient dialog ───────────────────────────────────────
        if (uiState.showCreateIngredientDialog) {
            CreateIngredientDialog(
                texts = viewModel.texts,
                onDismiss = { viewModel.onAction(CreateMealPlanUiAction.OnDismissCreateIngredientDialog) },
                onConfirm = { name, brand, servingSize, servingUnit, calories, protein, carbs, fat ->
                    viewModel.onAction(
                        CreateMealPlanUiAction.OnCreateCustomIngredient(
                            name = name,
                            brand = brand,
                            servingSize = servingSize,
                            servingUnit = servingUnit,
                            calories = calories,
                            protein = protein,
                            carbs = carbs,
                            fat = fat,
                        )
                    )
                },
            )
        }

        // ── 6. Loading / Error overlay ────────────────────────────────────────
        if (uiState.isLoading || uiState.errorMessage != null) {
            val popupState = if (uiState.isLoading) PopupResultState.LOADING else PopupResultState.ERROR
            PopupResultComponent(
                params = PopupResultParams(
                    state = popupState,
                    itemType = CreationItemType.MEAL,
                    title = if (uiState.isLoading) viewModel.texts.loadingTitle
                            else viewModel.sharedTexts.errorTitle,
                    message = if (uiState.isLoading) viewModel.sharedTexts.loadingMessage
                              else uiState.errorMessage ?: "",
                    retryLabel = viewModel.sharedTexts.retryLabel,
                    cancelLabel = viewModel.sharedTexts.cancelLabel,
                ),
                onRetry = { viewModel.onAction(CreateMealPlanUiAction.OnSubmit) },
                onCancel = { viewModel.onAction(CreateMealPlanUiAction.OnErrorDismissed) },
            )
        }

        // ── 7. Success dialog ─────────────────────────────────────────────────
        if (uiState.isSuccess) {
            SuccessDialog(
                params = SuccessDialogParams(
                    title = viewModel.texts.successTitle,
                ),
                onAnimationFinished = {
                    viewModel.onAction(CreateMealPlanUiAction.OnSuccessAnimationFinished)
                },
            )
        }
    }
}
