package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.SearchBarComponent
import com.agusstkd.goodlife.presentation.components.common.SearchBarParams
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.ProteinColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@Composable
fun IngredientCatalogSection(
    modifier: Modifier = Modifier,
    ingredients: ImmutableList<Ingredient>,
    searchQuery: String,
    isLoading: Boolean,
    hasMore: Boolean,
    // ── Estado de ingredientes agregados ──
    addedIngredientIds: ImmutableList<Long>,  // Para mostrar ✓ en vez de "Agregar"
    onSearchQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit,
    onAddClick: (Ingredient) -> Unit,
    onCreateCustomClick: () -> Unit,
    texts: CreateMealPlanTexts,
) {

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 3 && hasMore && !isLoading
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }


    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Label de sección ──────────────────────────────────────────
            Text(
                text = texts.ingredientCatalogSectionTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // ── Label de sección ──────────────────────────────────────────
            Text(
                text = "${ingredients.size} unidades",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            item {
                SearchBarComponent(
                    params = SearchBarParams(
                        query = searchQuery,
                        placeholder = texts.searchIngredientPlaceholder,
                    ),
                    onQueryChange = onSearchQueryChange,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(ingredients) { ingredient ->
                IngredientItem(
                    ingredient = ingredient,
                    isAdded = ingredient.id in addedIngredientIds,
                    onAddClick = onAddClick
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                ButtonComponent(
                    params = ButtonParams(
                        text = texts.createCustomIngredientButton,
                        enabled = true,
                        isLoading = false,
                        variant = ButtonVariant.OUTLINE,
                        colorScheme = ButtonColorScheme.Meal,
                        showTrailingIcon = true,
                        trailingIcon = Icons.Default.Add,
                    ),
                    onClick = onCreateCustomClick,
                )
            }
        }

    }

}

@Composable
fun IngredientItem(
    modifier: Modifier = Modifier,
    ingredient: Ingredient,
    isAdded: Boolean,
    onAddClick: (Ingredient) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isAdded) BorderStroke(2.dp, NutritionAccent) else null,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Imagen 64x64 ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (ingredient.imageUrl != null) {
                    AsyncImage(
                        model = ingredient.imageUrl,
                        contentDescription = ingredient.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            // ── Info central ──────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${ingredient.servingSize.toInt()}${ingredient.servingUnit} • ${ingredient.calories.toInt()} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                MacroBar(
                    protein = ingredient.protein,
                    carbs = ingredient.carbs,
                    fat = ingredient.fat,
                )
            }

            // ── Botón circular + / ✓ ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAdded) NutritionAccent
                        else NutritionAccent.copy(alpha = 0.12f)
                    )
                    .clickable(enabled = !isAdded) { onAddClick(ingredient) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isAdded) Color.White else NutritionAccent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * Barra horizontal proporcional de macros (proteína / carbos / grasas).
 * Cada segmento ocupa el ancho proporcional a su aporte sobre el total P+C+G.
 * Debajo: texto "P Xg C Xg G Xg" con los colores de cada macro.
 */
@Composable
private fun MacroBar(
    protein: Double,
    carbs: Double,
    fat: Double,
    modifier: Modifier = Modifier,
) {
    val total = protein + carbs + fat
    val pRatio = if (total > 0) (protein / total).toFloat() else 0f
    val cRatio = if (total > 0) (carbs / total).toFloat() else 0f
    val fRatio = if (total > 0) (fat / total).toFloat() else 0f

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        // Barra proporcional
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(50)),
        ) {
            if (pRatio > 0f) Box(modifier = Modifier.weight(pRatio).height(5.dp).background(ProteinColor))
            if (cRatio > 0f) Box(modifier = Modifier.weight(cRatio).height(5.dp).background(CarbsColor))
            if (fRatio > 0f) Box(modifier = Modifier.weight(fRatio).height(5.dp).background(FatColor))
        }

        // Texto de valores
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "P ${protein.toInt()}g", style = MaterialTheme.typography.labelSmall, color = ProteinColor, fontWeight = FontWeight.Bold)
            Text(text = "C ${carbs.toInt()}g",   style = MaterialTheme.typography.labelSmall, color = CarbsColor,   fontWeight = FontWeight.Bold)
            Text(text = "G ${fat.toInt()}g",      style = MaterialTheme.typography.labelSmall, color = FatColor,     fontWeight = FontWeight.Bold)
        }
    }
}


// ════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════
@Preview(showBackground = true, name = "IngredientCatalogSection — con datos")
@Composable
private fun IngredientCatalogSectionPreview() {
    GoodLifeTheme {
        IngredientCatalogSection(
            ingredients = persistentListOf(
                Ingredient(
                    id = 1,
                    name = "Pechuga de pollo",
                    brand = "Acme Foods",
                    servingSize = 100.0,
                    servingUnit = "g",
                    calories = 165.0,
                    protein = 31.0,
                    carbs = 0.0,
                    fat = 3.6,
                    isGlobal = true,
                    imageUrl = null,
                ),
                Ingredient(
                    id = 2,
                    name = "Arroz integral",
                    brand = null,  // ← Test de brand null
                    servingSize = 100.0,
                    servingUnit = "g",
                    calories = 123.0,
                    protein = 2.6,
                    carbs = 23.0,
                    fat = 0.9,
                    isGlobal = true,
                    imageUrl = null,
                )
            ),
            searchQuery = "",
            isLoading = false,
            hasMore = true,
            addedIngredientIds = persistentListOf(2L),  // ← Arroz ya agregado
            onSearchQueryChange = {},
            onLoadMore = {},
            onAddClick = {},
            onCreateCustomClick = {},
            texts = previewTexts(),
        )
    }
}

// Mock para textos (solo los que usamos)
private fun previewTexts() = CreateMealPlanTexts(
    screenTitle = "", typeBadge = "", stepOf = "", nextButton = "", backButton = "",
    createPlanButton = "", successTitle = "", searchMealPlaceholder = "", filterAll = "",
    chooseMealButton = "", createNewMealButton = "", newMealNameLabel = "",
    newMealNamePlaceholder = "", newMealDescriptionLabel = "", newMealDescriptionPlaceholder = "",
    newMealTypeLabel = "",
    macrosSummaryTitle = "", myIngredientsSectionTitle = "",
    ingredientCatalogSectionTitle = "Catálogo de ingredientes",
    searchIngredientPlaceholder = "Buscar ingrediente...",
    addIngredientButton = "Agregar",
    createCustomIngredientButton = "+ Crear ingrediente custom",
    quantitySheetQuantityLabel = "", quantitySheetUnitLabel = "", quantitySheetConfirmButton = "",
    quantitySheetCancelButton = "", createIngredientDialogTitle = "", ingredientNameLabel = "",
    ingredientNamePlaceholder = "", ingredientBrandLabel = "", ingredientBrandPlaceholder = "",
    ingredientServingSizeLabel = "", ingredientCaloriesLabel = "", ingredientProteinLabel = "",
    ingredientCarbsLabel = "", ingredientFatLabel = "", saveIngredientButton = "",
    mealSummaryTitle = "", mealTypeSectionTitle = "", errorNoMealSelected = "",
    errorMealNameEmpty = "", errorNoIngredients = "", errorNoMealType = "", errorNoDays = "",
    errorEndBeforeStart = "",
    errorQuantityZero = "",
    errorIngredientNameEmpty = "",
    errorServer = "",
    errorNetwork = "",
    loadingTitle = "",
    macroProteinLabel = "",
    macroCarbsLabel = "",
    macroFatLabel = "",
    quantitySheetMacroTooltip = ""
)
