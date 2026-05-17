package com.agusstkd.goodlife.presentation.screen.tabs.meals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Meals.
 *
 * Inyecta el ViewModel, colecta el estado y hace routing entre estados.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun MealsScreenOwner(
    viewModel: MealsTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        MealsUiState.Loading -> {
            MealsLoadingState()
        }

        MealsUiState.Empty -> {
            MealsEmptyState(
                onCreateMealPlan = { viewModel.onAction(MealsUiAction.OnCreateMealPlan) }
            )
        }

        is MealsUiState.Success -> {
            val a = viewModel.accessibilityTexts
            MealsScreen(
                uiState = state,
                dateHeaderParams = DateHeaderParams(
                    dayNumber = state.dayNumber,
                    headerText = state.headerText,
                    monthYear = if (state.showFullDate) state.monthYear else null,
                    openCalendarLabel = a.openCalendar,
                    previousDayLabel = a.previousDay,
                    nextDayLabel = a.nextDay,
                    notificationsLabel = a.notifications,
                ),
                onAction = viewModel::onAction,
            )
        }

        is MealsUiState.Error -> {
            MealsErrorState(
                message = state.message,
                onRetry = { viewModel.onAction(MealsUiAction.OnRefresh) },
            )
        }
    }
}

@Composable
private fun MealsLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SkeletonBlock(height = 64.dp, alpha = 0.35f)
        SkeletonBlock(height = 110.dp, alpha = 0.30f)
        SkeletonBlock(height = 80.dp, alpha = 0.25f)
        SkeletonBlock(height = 80.dp, alpha = 0.20f)
    }
}

@Composable
private fun MealsEmptyState(
    onCreateMealPlan: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Sin planes de comida para hoy",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Creá tu primer plan de comida para llevar un registro de tu nutrición diaria.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onCreateMealPlan,
                colors = ButtonDefaults.buttonColors(containerColor = NutritionAccent),
            ) {
                Text(text = "Crear plan de comida")
            }
        }
    }
}

@Composable
private fun MealsErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$message\nTap para reintentar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(32.dp)
                .clickable(onClick = onRetry),
        )
    }
}

@Composable
private fun SkeletonBlock(
    height: androidx.compose.ui.unit.Dp,
    alpha: Float,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {}
}
