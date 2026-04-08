package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agusstkd.goodlife.presentation.theme.CaloriesColor
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.MealAccent
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.ProteinColor

/**
 * Parámetros de configuración de la card de resumen de meal.
 *
 * @property name Nombre de la meal a mostrar.
 * @property imageUrl URL de la imagen de la meal. Si es null se muestra el ícono [Restaurant].
 * @property calories Calorías totales en kcal.
 * @property protein Proteínas en gramos.
 * @property carbs Carbohidratos en gramos.
 */
data class MealHeaderCardParams(
    val name: String,
    val imageUrl: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
)

/**
 * Card compacta que resume la meal seleccionada.
 *
 * Diseñada para el header del Paso 3 del wizard de creación de plan de comida.
 * Muestra la imagen (o ícono fallback), el nombre de la meal y los macros clave
 * (calorías, proteína, carbos) en una sola línea.
 *
 * Es clickeable — al tocarla puede abrir un detalle de la meal.
 *
 * @param params Datos a mostrar en la card.
 * @param onClick Callback al tocar la card (ej: abrir sheet de detalle).
 * @param modifier Modificador de Compose.
 */
@Composable
fun MealHeaderCard(
    params: MealHeaderCardParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NutritionBackground),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Imagen o ícono fallback ─────────────────────────────────────
            MealThumbnail(imageUrl = params.imageUrl, name = params.name)

            // ── Nombre + macros inline ──────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = params.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔥 ${params.calories.toInt()} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = CaloriesColor,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${params.protein.toInt()}g P",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProteinColor,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${params.carbs.toInt()}g C",
                        style = MaterialTheme.typography.labelSmall,
                        color = CarbsColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/**
 * Miniatura cuadrada de 48dp para la imagen de la meal.
 * Muestra [AsyncImage] si hay URL, o el ícono [Restaurant] como fallback.
 */
@Composable
private fun MealThumbnail(imageUrl: String?, name: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MealAccent.copy(alpha = 0.1f)),
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = MealAccent.copy(alpha = 0.7f),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "MealHeaderCard — con imagen")
@Composable
private fun MealHeaderCardWithImagePreview() {
    GoodLifeTheme {
        MealHeaderCard(
            params = MealHeaderCardParams(
                name = "Ensalada de Quinoa y Pollo",
                imageUrl = "https://example.com/meal.jpg",
                calories = 490.0,
                protein = 30.0,
                carbs = 45.0,
            ),
            onClick = {},
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "MealHeaderCard — sin imagen")
@Composable
private fun MealHeaderCardNoImagePreview() {
    GoodLifeTheme {
        MealHeaderCard(
            params = MealHeaderCardParams(
                name = "Pollo a la plancha con arroz",
                imageUrl = null,
                calories = 380.0,
                protein = 42.0,
                carbs = 28.0,
            ),
            onClick = {},
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}
