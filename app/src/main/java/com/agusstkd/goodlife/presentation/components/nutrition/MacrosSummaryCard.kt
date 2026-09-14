package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.layout.Arrangement
import java.math.BigDecimal
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.CaloriesColor
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.CarbsColorDark
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.FatColorDark
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.ProteinColor
import com.agusstkd.goodlife.presentation.theme.ProteinColorDark

/**
 * Card de macronutrientes con diseño editorial.
 *
 * Layout:
 * - Fondo [NutritionBackground] (naranja muy suave, `#FFF3E0`).
 * - Primera fila: 🔥 + calorías totales en hero size.
 * - Segunda fila: 3 mini-cards blancas side-by-side — PROTEÍNA / CARBOS / GRASAS,
 *   cada una con label coloreado arriba y valor en gramos abajo.
 *
 * Se usa en:
 * - Paso 2 del wizard (header sticky con preview en tiempo real).
 * - Paso 3 (resumen de la meal seleccionada).
 *
 * @param calories Calorías totales en kcal.
 * @param protein Proteínas totales en gramos.
 * @param carbs Carbohidratos totales en gramos.
 * @param fat Grasas totales en gramos.
 * @param proteinLabel Label localizado para proteína (ej: "PROTEÍNA").
 * @param carbsLabel Label localizado para carbos (ej: "CARBOS").
 * @param fatLabel Label localizado para grasas (ej: "GRASAS").
 * @param modifier Modifier externo.
 */
@Composable
fun MacrosSummaryCard(
    calories: Double,
    protein: Double,
    carbs: Double,
    fat: Double,
    proteinLabel: String,
    carbsLabel: String,
    fatLabel: String,
    calorieUnit: String = "kcal",
    gramUnit: String = "g",
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NutritionBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Calorías (hero row) ───────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = CaloriesColor,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "${formatNutritionValue(calories)} $calorieUnit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = CaloriesColor,
                )
            }

            // ── 3 Macro cards ─────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MacroCard(
                    label = proteinLabel,
                    value = protein,
                    labelColor = ProteinColor,
                    valueColor = ProteinColorDark,
                    gramUnit = gramUnit,
                    modifier = Modifier.weight(1f),
                )
                MacroCard(
                    label = carbsLabel,
                    value = carbs,
                    labelColor = CarbsColor,
                    valueColor = CarbsColorDark,
                    gramUnit = gramUnit,
                    modifier = Modifier.weight(1f),
                )
                MacroCard(
                    label = fatLabel,
                    value = fat,
                    labelColor = FatColor,
                    valueColor = FatColorDark,
                    gramUnit = gramUnit,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Mini-card individual para un macronutriente.
 * Fondo blanco semitransparente, label coloreado arriba, valor en gramos abajo.
 */
@Composable
private fun MacroCard(
    label: String,
    value: Double,
    labelColor: Color,
    valueColor: Color,
    gramUnit: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = labelColor,
            )
            Text(
                text = "${formatNutritionValue(value)}$gramUnit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
        }
    }
}

internal fun formatNutritionValue(value: Double): String = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()

// ════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "MacrosSummaryCard — con datos")
@Composable
private fun MacrosSummaryCardPreview() {
    GoodLifeTheme {
        MacrosSummaryCard(
            calories = 420.0,
            protein = 35.0,
            carbs = 40.0,
            fat = 12.0,
            proteinLabel = "PROTEÍNA",
            carbsLabel = "CARBOS",
            fatLabel = "GRASAS",
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "MacrosSummaryCard — vacío")
@Composable
private fun MacrosSummaryCardEmptyPreview() {
    GoodLifeTheme {
        MacrosSummaryCard(
            calories = 0.0,
            protein = 0.0,
            carbs = 0.0,
            fat = 0.0,
            proteinLabel = "PROTEÍNA",
            carbsLabel = "CARBOS",
            fatLabel = "GRASAS",
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}
