package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.nutrition.MacrosSummaryCard
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState

/** Pure Compose rendering for a read-only meal-plan detail. */
@Composable
fun MealDetailScreen(
    state: MealDetailUiState.Content,
    texts: MealDetailTexts,
    onNavigateUp: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.back)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.planName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(state.mealTypeLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { MealDetailImage(state.imageUrl, texts.imageUnavailable) }
        state.mealName?.let { mealName ->
            item { MealDetailField(texts.mealLabel, mealName) }
        }
        state.scheduledTimeLabel?.let { scheduledTime ->
            item { MealDetailField(texts.scheduleLabel, scheduledTime) }
        }
        item { MealDetailField(texts.statusLabel, if (state.isActive) texts.active else texts.inactive) }
        item {
            MacrosSummaryCard(
                calories = state.totalCalories,
                protein = state.totalProtein,
                carbs = state.totalCarbs,
                fat = state.totalFat,
                proteinLabel = texts.proteinLabel,
                carbsLabel = texts.carbsLabel,
                fatLabel = texts.fatLabel,
                calorieUnit = texts.calorieUnit,
                gramUnit = texts.gramUnit,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun MealDetailField(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
internal fun MealDetailImage(imageUrl: String?, unavailableDescription: String, forceFallback: Boolean = false) {
    var failed by remember(imageUrl, forceFallback) { mutableStateOf(forceFallback || imageUrl.isNullOrBlank()) }
    if (failed) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 12.dp)
                .semantics { contentDescription = unavailableDescription },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Restaurant, contentDescription = null)
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            onError = { failed = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        )
    }
}


