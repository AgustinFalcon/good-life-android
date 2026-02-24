package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.DailyTexts
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.presentation.components.header.DateHeaderComponent
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import kotlinx.datetime.LocalDate

/**
 * Pantalla Daily (tab de tareas diarias).
 *
 * UI pura: solo renderiza, sin lógica de negocio.
 * Todos los textos formateados y acciones vienen desde ViewModel.
 */
@Composable
fun DailyScreen(
    uiState: DailyUiState.Success,
    dailyTexts: DailyTexts,
    dateHeaderParams: DateHeaderParams,
    onAction: (DailyUiAction) -> Unit
) {
    val totalItems = uiState.items.size
    val completedItems = uiState.items.count { it.status == DailyItemStatus.COMPLETED }
    val progress = uiState.completionRate.coerceIn(0.0, 1.0).toFloat()
    val percentage = (progress * 100).toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            val onPreviousDay = remember(onAction) { { onAction(DailyUiAction.OnPreviousDay) } }
            val onNextDay = remember(onAction) { { onAction(DailyUiAction.OnNextDay) } }
            DateHeaderComponent(
                params = dateHeaderParams,
                onPreviousDay = onPreviousDay,
                onNextDay = onNextDay
            )
        }

        item {
            CompletionRateCard(
                percentage = percentage,
                progress = progress,
                completedItems = completedItems,
                totalItems = totalItems,
                dailyTexts = dailyTexts
            )
        }

        items(
            items = uiState.items,
            key = { it.id }
        ) { item ->
            DailyItemCard(
                item = item,
                dailyTexts = dailyTexts,
                onClick = { itemId -> onAction(DailyUiAction.OnItemClick(itemId)) },
                onStatusChange = { itemId, newStatus ->
                    onAction(DailyUiAction.OnItemStatusChange(itemId, newStatus))
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        item {
            Text(
                text = dailyTexts.totalForDayFormat
                    .replace("{0}", totalItems.toString()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun CompletionRateCard(
    percentage: Int,
    progress: Float,
    completedItems: Int,
    totalItems: Int,
    dailyTexts: DailyTexts
) {
    Card(
        modifier = Modifier.padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = dailyTexts.dailyProgressFormat
                    .replace("{0}", percentage.toString()),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = dailyTexts.completedOfFormat
                    .replace("{0}", completedItems.toString())
                    .replace("{1}", totalItems.toString()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "DailyScreen - Hoy con items")
@Composable
private fun DailyScreenWithItemsPreview() {
    val a = Spanish.accessibilityTexts
    GoodLifeTheme {
        DailyScreen(
            dailyTexts = Spanish.dailyTexts,
            dateHeaderParams = DateHeaderParams(
                dayNumber = 10, headerText = "Hoy", monthYear = null,
                openCalendarLabel = a.openCalendar, previousDayLabel = a.previousDay,
                nextDayLabel = a.nextDay, notificationsLabel = a.notifications
            ),
            uiState = DailyUiState.Success(
                date = LocalDate(2026, 2, 10),
                dayNumber = 10,
                headerText = "Hoy",
                monthYear = "Febrero 2026",
                showFullDate = false,
                completionRate = 0.5,
                items = listOf(
                    DailyItemUiModel(
                        id = 1L,
                        type = DailyItemType.TASK,
                        typeLabel = "Tarea",
                        title = "Escribir resumen semanal",
                        description = "Revisar objetivos y métricas",
                        scheduledTime = "09:00",
                        status = DailyItemStatus.PENDING,
                    ),
                    DailyItemUiModel(
                        id = 2L,
                        type = DailyItemType.HABIT,
                        typeLabel = "Hábito",
                        title = "Tomar agua",
                        description = "1.5 / 2.0 litros",
                        scheduledTime = null,
                        status = DailyItemStatus.COMPLETED,
                    ),
                    DailyItemUiModel(
                        id = 3L,
                        type = DailyItemType.WORKOUT,
                        typeLabel = "Entrenamiento",
                        title = "Piernas",
                        description = "Rutina fuerza • 5 ejercicios",
                        scheduledTime = "18:30",
                        status = DailyItemStatus.SKIPPED,
                    ),
                    DailyItemUiModel(
                        id = 4L,
                        type = DailyItemType.MEAL,
                        typeLabel = "Comida",
                        title = "Almuerzo",
                        description = "700 kcal • 42g proteína",
                        scheduledTime = "13:15",
                        status = DailyItemStatus.PENDING,
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "DailyScreen - Fecha absoluta sin items")
@Composable
private fun DailyScreenEmptyPreview() {
    val a = Spanish.accessibilityTexts
    GoodLifeTheme {
        DailyScreen(
            dailyTexts = Spanish.dailyTexts,
            dateHeaderParams = DateHeaderParams(
                dayNumber = 9, headerText = "Lun, 09 de feb", monthYear = "Febrero 2026",
                openCalendarLabel = a.openCalendar, previousDayLabel = a.previousDay,
                nextDayLabel = a.nextDay, notificationsLabel = a.notifications
            ),
            uiState = DailyUiState.Success(
                date = LocalDate(2026, 2, 9),
                dayNumber = 9,
                headerText = "Lun, 09 de feb",
                monthYear = "Febrero 2026",
                showFullDate = true,
                completionRate = 0.0,
                items = emptyList()
            ),
            onAction = {}
        )
    }
}
