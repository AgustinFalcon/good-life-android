package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.header.DateHeaderComponent
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState/**
 * Pantalla Daily (tab de tareas diarias).
 *
 * UI pura: solo renderiza, sin lógica de fechas.
 * Todos los textos formateados vienen del [DailyUiState].
 */
@Composable
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DateHeaderComponent(
            dayNumber = uiState.dayNumber,
            headerText = uiState.headerText,
            monthYear = if (uiState.showFullDate) uiState.monthYear else null,
            onPreviousDay = { onAction(DailyUiAction.OnPreviousDay) },
            onNextDay = { onAction(DailyUiAction.OnNextDay) }
        )        Text(
            text = "Daily: ${uiState.headerText}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
    }
}