package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealIngredientDraft
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.ProteinColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
private fun MacroPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

/**
 * Sección "Mis ingredientes" del Paso 2 del wizard de creación de plan de comida.
 *
 * Muestra la lista de [MealIngredientDraft] que el usuario ya confirmó, cada uno
 * con su nombre, cantidad, marca y macros calculados ([MealIngredientDraft.scaledProtein],
 * [MealIngredientDraft.scaledCarbs], [MealIngredientDraft.scaledFat]).
 *
 * Si la lista está vacía no renderiza nada.
 *
 * @param ingredients Lista inmutable de drafts confirmados por el usuario.
 * @param texts Textos localizados — usa [CreateMealPlanTexts.myIngredientsSectionTitle].
 * @param onRemove Callback al tocar el ícono de eliminar; recibe el ID del ingrediente.
 * @param modifier Modificador de Compose.
 */
@Composable
fun MyIngredientsSection(
    modifier: Modifier = Modifier,
    ingredients: ImmutableList<MealIngredientDraft>,
    texts: CreateMealPlanTexts,
    onRemove: (ingredientId: Long) -> Unit,
) {
    if (ingredients.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${texts.myIngredientsSectionTitle} (${ingredients.size})",
            style = MaterialTheme.typography.titleLarge,
        )

        ingredients.forEach { draft ->
            MyIngredientItem(
                draft = draft,
                onRemove = onRemove,
            )
        }
    }
}

@Composable
private fun MyIngredientItem(
    draft: MealIngredientDraft,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, NutritionAccent.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = NutritionBackground),
                contentAlignment = Alignment.Center
            ) {
                if (draft.ingredient.imageUrl != null) {
                    AsyncImage(
                        model = draft.ingredient.imageUrl,
                        contentDescription = draft.ingredient.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Egg,
                        contentDescription = null,
                        tint = NutritionAccent.copy(alpha = 0.7f),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.ingredient.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                // Cantidad • brand
                Text(
                    text = "${draft.quantity.toInt()}${draft.unit} • ${draft.ingredient.brand ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Macros con pill
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroPill("P: ${draft.scaledProtein.toInt()}g", ProteinColor)
                    MacroPill("C: ${draft.scaledCarbs.toInt()}g", CarbsColor)
                    MacroPill("G: ${draft.scaledFat.toInt()}g", FatColor)
                }
            }

            // Boton eliminar
            Spacer(Modifier.width(4.dp))
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = { onRemove(draft.ingredient.id) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "MyIngredientsSection — con datos")
@Composable
private fun MyIngredientsSectionPreview() {
    GoodLifeTheme {
        MyIngredientsSection(
            ingredients = persistentListOf(
                MealIngredientDraft(
                    ingredient = Ingredient(
                        id = 1, name = "Pechuga de Pollo", brand = "A la plancha",
                        servingSize = 100.0, servingUnit = "g",
                        calories = 165.0, protein = 31.0, carbs = 0.0, fat = 3.5,
                        isGlobal = true, imageUrl = null,
                    ),
                    quantity = 150.0, unit = "g",
                ),
                MealIngredientDraft(
                    ingredient = Ingredient(
                        id = 2, name = "Arroz Integral", brand = "Cocido",
                        servingSize = 100.0, servingUnit = "g",
                        calories = 123.0, protein = 2.6, carbs = 23.0, fat = 0.9,
                        isGlobal = true, imageUrl = null,
                    ),
                    quantity = 100.0, unit = "g",
                ),
            ),
            texts = previewMyIngredientsTexts(),
            onRemove = {},
        )
    }
}

private fun previewMyIngredientsTexts() = CreateMealPlanTexts(
    myIngredientsSectionTitle = "Mis ingredientes",
    // region campos requeridos vacíos
    screenTitle = "", typeBadge = "", stepOf = "", nextButton = "", backButton = "",
    createPlanButton = "", successTitle = "", searchMealPlaceholder = "", filterAll = "",
    chooseMealButton = "", createNewMealButton = "", newMealNameLabel = "",
    newMealNamePlaceholder = "", newMealDescriptionLabel = "", newMealDescriptionPlaceholder = "",
    newMealTypeLabel = "", macrosSummaryTitle = "", ingredientCatalogSectionTitle = "",
    searchIngredientPlaceholder = "", addIngredientButton = "", createCustomIngredientButton = "",
    quantitySheetQuantityLabel = "", quantitySheetUnitLabel = "", quantitySheetConfirmButton = "",
    quantitySheetCancelButton = "", quantitySheetMacroTooltip = "", createIngredientDialogTitle = "",
    ingredientNameLabel = "", ingredientNamePlaceholder = "", ingredientBrandLabel = "",
    ingredientBrandPlaceholder = "", ingredientServingSizeLabel = "", ingredientCaloriesLabel = "",
    ingredientProteinLabel = "", ingredientCarbsLabel = "", ingredientFatLabel = "",
    saveIngredientButton = "", mealSummaryTitle = "", mealTypeSectionTitle = "",
    errorNoMealSelected = "", errorMealNameEmpty = "", errorNoIngredients = "",
    errorNoMealType = "", errorNoDays = "", errorEndBeforeStart = "", errorQuantityZero = "",
    errorIngredientNameEmpty = "", errorServer = "", errorNetwork = "", loadingTitle = "",
    macroProteinLabel = "", macroCarbsLabel = "", macroFatLabel = "",
    // endregion
)
