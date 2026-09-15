package com.agusstkd.goodlife.presentation.screen.tabs.meals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.datetime.language.TabDetailTexts
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.components.header.DateHeaderComponent
import com.agusstkd.goodlife.presentation.components.nutrition.MacrosSummaryCard
import com.agusstkd.goodlife.presentation.components.nutrition.formatNutritionValue
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealPlanUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import com.agusstkd.goodlife.presentation.theme.CaloriesColor
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.MealAccent
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.ProteinColor
import kotlinx.datetime.LocalDate

/**
 * Pantalla Meals (tab de nutrición diaria).
 *
 * UI pura: solo renderiza, sin lógica de negocio.
 * Todos los textos formateados y acciones vienen desde el ViewModel.
 *
 * Reutiliza [MacrosSummaryCard] del paquete de componentes de nutrición
 * para mostrar el resumen diario de macros.
 *
 * @param uiState Estado de éxito con los datos del día.
 * @param dateHeaderParams Parámetros del header de fecha para accesibilidad.
 * @param onAction Callback de acciones del usuario.
 */
@Composable
fun MealsScreen(
    uiState: MealsUiState.Success,
    texts: TabDetailTexts,
    dateHeaderParams: DateHeaderParams,
    listState: LazyListState = rememberLazyListState(),
    onAction: (MealsUiAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Header de fecha (mismo patrón que DailyScreen) ────────────────
        item {
            val onPreviousDay = remember(onAction) { { onAction(MealsUiAction.OnPreviousDay) } }
            val onNextDay = remember(onAction) { { onAction(MealsUiAction.OnNextDay) } }
            DateHeaderComponent(
                params = dateHeaderParams,
                onPreviousDay = onPreviousDay,
                onNextDay = onNextDay,
            )
        }

        // ── Card de resumen de macros del día ─────────────────────────────
        item {
            MacrosSummaryCard(
                calories = uiState.totalCalories,
                protein = uiState.totalProtein,
                carbs = uiState.totalCarbs,
                fat = uiState.totalFat,
                proteinLabel = texts.proteinLabel,
                carbsLabel = texts.carbsLabel,
                fatLabel = texts.fatLabel,
                calorieUnit = texts.calorieUnit,
                gramUnit = texts.gramUnit,
                decimalSeparator = texts.decimalSeparator,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }

        // ── Contador de planes del día ─────────────────────────────────────
        item {
            Text(
                text = "${uiState.mealPlans.size} ${if (uiState.mealPlans.size == 1) texts.mealPlanSingular else texts.mealPlanPlural}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // ── Lista de planes del día ────────────────────────────────────────
        items(
            items = uiState.mealPlans,
            key = { it.id },
        ) { plan ->
            MealPlanCard(
                plan = plan,
                texts = texts,
                onClick = { onAction(MealsUiAction.OnMealPlanClick(plan.id)) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}

/**
 * Card de un plan de comida del día.
 *
 * Muestra imagen (o ícono fallback), nombre del plan, tipo de comida,
 * hora programada y macros con colores diferenciados.
 *
 * @param plan Modelo UI del plan de comida.
 * @param onClick Callback al presionar la card.
 * @param modifier Modificador externo.
 */
@Composable
private fun MealPlanCard(
    plan: MealPlanUiModel,
    texts: TabDetailTexts,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Imagen o ícono fallback ──────────────────────────────────
            MealThumbnail(imageUrl = plan.imageUrl, name = plan.mealName ?: plan.name)

            // ── Contenido: nombre + tipo + hora + macros ─────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Nombre del plan
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Tipo de comida + hora programada
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = plan.mealTypeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = NutritionAccent,
                        fontWeight = FontWeight.SemiBold,
                    )
                    plan.scheduledTimeLabel?.let { time ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Macros con colores diferenciados por nutriente
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${formatNutritionValue(plan.totalCalories)} ${texts.calorieUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CaloriesColor,
                    )
                    Text(
                        text = "${texts.proteinLabel} ${formatNutritionValue(plan.totalProtein)}${texts.gramUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ProteinColor,
                    )
                    Text(
                        text = "${texts.carbsLabel} ${formatNutritionValue(plan.totalCarbs)}${texts.gramUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CarbsColor,
                    )
                    Text(
                        text = "${texts.fatLabel} ${formatNutritionValue(plan.totalFat)}${texts.gramUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = FatColor,
                    )
                }
            }
        }
    }
}

/**
 * Miniatura cuadrada de 52dp para la imagen del plan de comida.
 * Muestra [AsyncImage] si hay URL, o el ícono [Restaurant] como fallback.
 */
@Composable
private fun MealThumbnail(imageUrl: String?, name: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(NutritionBackground),
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
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// PREVIEWS
// ════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "MealsScreen — Hoy con planes")
@Composable
private fun MealsScreenWithPlansPreview() {
    GoodLifeTheme {
        MealsScreen(
            uiState = MealsUiState.Success(
                date = LocalDate(2026, 5, 15),
                dayNumber = 15,
                headerText = "Hoy",
                monthYear = "Mayo 2026",
                showFullDate = false,
                mealPlans = listOf(
                    MealPlanUiModel(
                        id = 1L,
                        name = "Plan Desayuno Proteico",
                        mealTypeLabel = "Desayuno",
                        scheduledTimeLabel = "08:00",
                        mealName = "Avena con proteína",
                        imageUrl = null,
                        totalCalories = 450.0,
                        totalProtein = 32.0,
                        totalCarbs = 50.0,
                        totalFat = 10.0,
                    ),
                    MealPlanUiModel(
                        id = 2L,
                        name = "Plan Almuerzo Equilibrado",
                        mealTypeLabel = "Almuerzo",
                        scheduledTimeLabel = "13:00",
                        mealName = "Pollo con arroz y vegetales",
                        imageUrl = null,
                        totalCalories = 620.0,
                        totalProtein = 48.0,
                        totalCarbs = 60.0,
                        totalFat = 14.0,
                    ),
                ),
                totalCalories = 1070.0,
                totalProtein = 80.0,
                totalCarbs = 110.0,
                totalFat = 24.0,
            ),
            texts = Spanish.tabDetailTexts,
            dateHeaderParams = DateHeaderParams(
                dayNumber = 15,
                headerText = "Hoy",
                monthYear = null,
            ),
            onAction = {},
        )
    }
}
