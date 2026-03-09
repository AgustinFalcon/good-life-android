package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.core.datetime.language.DailyTexts
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Daily.
 *
 * Inyecta el ViewModel y conecta el estado con la UI pura.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun DailyScreenOwner(
    viewModel: DailyTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        DailyUiState.Loading -> {
            DailyLoadingState()
        }

        DailyUiState.Empty -> {
            DailyEmptyState(
                dailyTexts = viewModel.dailyTexts,
                onRetry = { viewModel.onAction(DailyUiAction.OnRefresh) }
            )
        }

        is DailyUiState.Success -> {
            val a = viewModel.accessibilityTexts
            DailyScreen(
                uiState = state,
                dailyTexts = viewModel.dailyTexts,
                dateHeaderParams = DateHeaderParams(
                    dayNumber = state.dayNumber,
                    headerText = state.headerText,
                    monthYear = if (state.showFullDate) state.monthYear else null,
                    openCalendarLabel = a.openCalendar,
                    previousDayLabel = a.previousDay,
                    nextDayLabel = a.nextDay,
                    notificationsLabel = a.notifications
                ),
                onAction = viewModel::onAction
            )
        }

        is DailyUiState.Error -> {
            DailyErrorState(
                message = state.message,
                dailyTexts = viewModel.dailyTexts,
                onRetry = { viewModel.onAction(DailyUiAction.OnRefresh) }
            )
        }
    }
}

@Composable
private fun DailyLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SkeletonBlock(height = 64.dp, alpha = 0.35f)
        SkeletonBlock(height = 96.dp, alpha = 0.30f)
        SkeletonBlock(height = 88.dp, alpha = 0.25f)
        SkeletonBlock(height = 88.dp, alpha = 0.20f)
    }
}

@Composable
private fun DailyEmptyState(
    dailyTexts: DailyTexts,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${dailyTexts.noData}\n${dailyTexts.tapToRetry}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(onClick = onRetry)
        )
    }
}

@Composable
private fun DailyErrorState(
    message: String,
    dailyTexts: DailyTexts,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$message\n${dailyTexts.tapToRetry}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable(onClick = onRetry)
        )
    }
}

@Composable
private fun SkeletonBlock(
    height: androidx.compose.ui.unit.Dp,
    alpha: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {}
}
