package com.agusstkd.goodlife.presentation.screen.tabs.daily

import com.agusstkd.goodlife.core.extensions.formatArgs
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyFilter
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.filteredItems
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import kotlinx.datetime.LocalDate

/**
 * Pantalla Daily (tab de tareas diarias).
 *
 * UI pura: solo renderiza, sin lógica de negocio.
 * Todos los textos formateados y acciones vienen desde ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScreen(
    uiState: DailyUiState.Success,
    dailyTexts: DailyTexts,
    dateHeaderParams: DateHeaderParams,
    listState: LazyListState = rememberLazyListState(),
    onAction: (DailyUiAction) -> Unit
) {
    val totalItems = uiState.items.size
    val completedItems = uiState.items.count { it.status == DailyItemStatus.COMPLETED }
    val progress = if (totalItems == 0) 0f else completedItems.toFloat() / totalItems
    val percentage = (progress * 100).toInt()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onAction(DailyUiAction.OnRefresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            val onPreviousDay = remember(onAction) { { onAction(DailyUiAction.OnPreviousDay) } }
            val onNextDay = remember(onAction) { { onAction(DailyUiAction.OnNextDay) } }
            val onCalendarClick = remember(onAction) { { onAction(DailyUiAction.OnCalendarClick) } }
            DateHeaderComponent(
                params = dateHeaderParams,
                onPreviousDay = onPreviousDay,
                onNextDay = onNextDay,
                onCalendarClick = onCalendarClick,
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

        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DailyFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.activeFilter == filter,
                        onClick = { onAction(DailyUiAction.OnFilterChange(filter)) },
                        label = { Text(text = filter.toLabel(dailyTexts)) }
                    )
                }
            }
        }

        items(
            items = uiState.filteredItems,
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
                text = dailyTexts.totalForDayFormat.formatArgs(totalItems),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
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
                text = dailyTexts.dailyProgressFormat.formatArgs(percentage),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = dailyTexts.completedOfFormat.formatArgs(completedItems, totalItems),
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
                items = persistentListOf(
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
                items = persistentListOf()
            ),
            onAction = {}
        )
    }
}

@Composable
internal fun DailyLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DailySkeletonBlock(height = 64.dp, alpha = 0.35f)
        DailySkeletonBlock(height = 96.dp, alpha = 0.30f)
        DailySkeletonBlock(height = 88.dp, alpha = 0.25f)
        DailySkeletonBlock(height = 88.dp, alpha = 0.20f)
    }
}

@Composable
internal fun DailyEmptyState(
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
internal fun DailyErrorState(
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
private fun DailySkeletonBlock(
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

@Preview(showBackground = true, name = "DailyScreen - Loading")
@Composable
private fun DailyLoadingPreview() {
    GoodLifeTheme { DailyLoadingState() }
}

@Preview(showBackground = true, name = "DailyScreen - Empty")
@Composable
private fun DailyEmptyPreview() {
    GoodLifeTheme {
        DailyEmptyState(dailyTexts = Spanish.dailyTexts, onRetry = {})
    }
}

@Preview(showBackground = true, name = "DailyScreen - Error")
@Composable
private fun DailyErrorPreview() {
    GoodLifeTheme {
        DailyErrorState(
            message = Spanish.errorTexts.connectionError,
            dailyTexts = Spanish.dailyTexts,
            onRetry = {},
        )
    }
}
