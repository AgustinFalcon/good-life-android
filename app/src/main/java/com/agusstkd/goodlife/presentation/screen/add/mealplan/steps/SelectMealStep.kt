package com.agusstkd.goodlife.presentation.screen.add.mealplan.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.SearchBarComponent
import com.agusstkd.goodlife.presentation.components.common.SearchBarParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.nutrition.MealCatalogItem
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

/**
 * Paso 1 del wizard de creación de plan de comida.
 *
 * Tiene dos modos según [CreateMealPlanUiState.isCreatingNewMeal]:
 * - **Catálogo**: [SearchBarComponent] + filtros por [MealType] en [LazyRow]
 *   + lista de [MealCatalogItem] con scroll infinito + botón "Crear nueva comida".
 * - **Formulario nueva meal**: campos nombre, descripción y selector de tipo
 *   en el composable privado [NewMealForm].
 *
 * Tocar "Elegir" dispara [CreateMealPlanUiAction.OnSelectMeal] y avanza al Paso 2.
 * Tocar "Crear nueva comida" dispara [CreateMealPlanUiAction.OnCreateNewMeal].
 *
 * @param uiState Estado actual del wizard.
 * @param mealTypeEntries Tipos de comida con sus nombres localizados para los filtros.
 * @param texts Textos localizados del wizard.
 * @param onAction Callback para todas las acciones del usuario.
 * @param modifier Modificador de Compose.
 */
@Composable
fun SelectMealStep(
    uiState: CreateMealPlanUiState,
    mealTypeEntries: List<Pair<MealType, String>>,
    texts: CreateMealPlanTexts,
    onAction: (CreateMealPlanUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isCreatingNewMeal) {
        NewMealForm(
            uiState = uiState,
            mealTypeEntries = mealTypeEntries,
            texts = texts,
            onAction = onAction,
            modifier = modifier,
        )

    } else {
        val listState = rememberLazyListState()

        LaunchedEffect(listState) {
            snapshotFlow {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                totalItems > 0 && lastVisible >= totalItems - 3
            }
                .distinctUntilChanged()
                .filter { it }
                .collect { onAction(CreateMealPlanUiAction.OnLoadMoreMeals) }
        }

        LazyColumn(
            state = listState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                SearchBarComponent(
                    params = SearchBarParams(
                        query = uiState.mealSearchQuery,
                        placeholder = texts.searchMealPlaceholder,
                    ),
                    onQueryChange = { onAction(CreateMealPlanUiAction.OnMealSearchQueryChange(it)) },
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = uiState.selectedMealTypeFilter == null,
                            onClick = { onAction(CreateMealPlanUiAction.OnMealTypeFilterChange(null)) },
                            label = { Text(text = texts.filterAll) },
                        )
                    }
                    items(mealTypeEntries) { (mealType, label) ->
                        FilterChip(
                            selected = uiState.selectedMealTypeFilter == mealType,
                            onClick = { onAction(CreateMealPlanUiAction.OnMealTypeFilterChange(mealType)) },
                            label = { Text(text = label) },
                        )
                    }
                }
            }

            items(uiState.mealCatalog, key = { it.id }) { meal ->
                MealCatalogItem(
                    meal = meal,
                    onChoose = { onAction(CreateMealPlanUiAction.OnSelectMeal(meal)) },
                )
            }

            if (uiState.isLoadingMeals) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            item {
                ButtonComponent(
                    params = ButtonParams(
                        text = texts.createNewMealButton,
                        variant = ButtonVariant.OUTLINE,
                        colorScheme = ButtonColorScheme.Meal,
                    ),
                    onClick = { onAction(CreateMealPlanUiAction.OnCreateNewMeal) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NewMealForm(
    uiState: CreateMealPlanUiState,
    mealTypeEntries: List<Pair<MealType, String>>,
    texts: CreateMealPlanTexts,
    onAction: (CreateMealPlanUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = texts.newMealNameLabel)

        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.newMealName,
                placeholder = texts.newMealNamePlaceholder,
                type = TextFieldType.TEXT,
            ),
            onValueChange = { onAction(CreateMealPlanUiAction.OnNewMealNameChange(name = it)) }
        )

        Text(text = texts.newMealDescriptionLabel)

        TextFieldComponent(
            params = TextFieldParams(
                value = uiState.newMealDescription,
                placeholder = texts.newMealDescriptionPlaceholder,
                type = TextFieldType.MULTILINE,
                minLines = 3,
            ),
            onValueChange = { onAction(CreateMealPlanUiAction.OnNewMealDescriptionChange(description = it)) }
        )

        Text(text = texts.newMealTypeLabel)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            mealTypeEntries.forEach { (mealType, label) ->
                FilterChip(
                    selected = uiState.newMealType == mealType,
                    onClick = { onAction(CreateMealPlanUiAction.OnNewMealTypeChange(newMealType = mealType)) },
                    label = { Text(text = label) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "SelectMealStep — catálogo")
@Composable
private fun SelectMealStepPreview() {
    GoodLifeTheme {
        SelectMealStep(
            uiState = previewMealPlanUiState(),
            mealTypeEntries = previewMealTypeEntries(),
            texts = previewMealPlanTexts(),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "SelectMealStep — nueva comida")
@Composable
private fun NewMealFormPreview() {
    GoodLifeTheme {
        SelectMealStep(
            uiState = previewMealPlanUiState().copy(isCreatingNewMeal = true),
            mealTypeEntries = previewMealTypeEntries(),
            texts = previewMealPlanTexts(),
            onAction = {},
        )
    }
}