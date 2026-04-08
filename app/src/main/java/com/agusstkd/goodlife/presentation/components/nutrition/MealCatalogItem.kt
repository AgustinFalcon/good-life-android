package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.MealBackground
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.ProteinColor

/**
 * Card de comida para el catálogo del wizard de creación de plan de comida.
 *
 * Muestra la imagen de la comida con badges superpuestos (calorías y badge "Custom"
 * para comidas del usuario), seguido de nombre, descripción, macros con colores
 * diferenciados y el tiempo de preparación con ícono de reloj.
 *
 * @param meal Datos de la comida a mostrar.
 * @param onChoose Callback al presionar el botón "Elegir".
 * @param modifier Modificador aplicado a la [Card] externa.
 */
@Composable
fun MealCatalogItem(
    meal: MealSummary,
    onChoose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {

        // ── Sección imagen con overlays ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
        ) {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Badge "Custom" — solo aparece si la comida es del usuario
            if (!meal.isGlobal) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    containerColor = NutritionAccent,
                ) {
                    Text(
                        text = "Custom",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }

            // Badge calorías — siempre visible, esquina inferior derecha
            Badge(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                containerColor = MealBackground,
            ) {
                Text(
                    text = "${meal.calories.toInt()} kcal",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }

        // ── Sección info ──────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {

            // Nombre
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Descripción (solo si existe)
            meal.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Macros con colores diferenciados por nutriente
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "P ${meal.protein.toInt()}g",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ProteinColor,
                )
                Text(
                    text = "C ${meal.carbs.toInt()}g",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CarbsColor,
                )
                Text(
                    text = "G ${meal.fat.toInt()}g",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FatColor,
                )
            }

            // Fila inferior: tiempo de preparación + botón Elegir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Tiempo de preparación con ícono de reloj (null-safe)
                meal.prepTimeMinutes?.let { minutes ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "$minutes min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                ButtonComponent(
                    params = ButtonParams(
                        text = "Elegir",
                        variant = ButtonVariant.OUTLINE,
                    ),
                    onClick = onChoose,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "MealCatalogItem — completo", showBackground = true)
@Composable
private fun PreviewMealCatalogItemFull() {
    GoodLifeTheme {
        MealCatalogItem(
            meal = MealSummary(
                id = 1L,
                name = "Ensalada César con Pollo",
                description = "Liviana y proteica, ideal para el almuerzo",
                imageUrl = null,
                prepTimeMinutes = 25,
                servingSize = 1.0,
                servingUnit = "porción",
                calories = 420.0,
                protein = 35.0,
                carbs = 10.0,
                fat = 28.0,
                isGlobal = true,
            ),
            onChoose = {},
        )
    }
}

@Preview(name = "MealCatalogItem — custom, sin descripción ni tiempo", showBackground = true)
@Composable
private fun PreviewMealCatalogItemEdge() {
    GoodLifeTheme {
        MealCatalogItem(
            meal = MealSummary(
                id = 2L,
                name = "Mi batido de proteína personalizado con nombre muy largo que debería truncarse",
                description = null,
                imageUrl = null,
                prepTimeMinutes = null,
                servingSize = 1.0,
                servingUnit = "vaso",
                calories = 310.0,
                protein = 40.0,
                carbs = 20.0,
                fat = 8.0,
                isGlobal = false,
            ),
            onChoose = {},
        )
    }
}
