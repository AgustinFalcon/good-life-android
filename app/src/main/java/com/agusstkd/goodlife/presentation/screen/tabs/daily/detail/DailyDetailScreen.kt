package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailTexts
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

/** Pure read-only UI for a found Daily item. */
@Composable
fun DailyDetailScreen(
    state: DailyDetailUiState.Content,
    texts: DailyDetailTexts,
    onNavigateUp: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = texts.back,
            )
        }
        Text(
            text = texts.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                DetailField(texts.typeLabel, state.type)
                DetailField(texts.statusLabel, state.status)
                state.scheduledTime?.let { DetailField(texts.scheduleLabel, it) }
                state.description?.let { DetailField(texts.descriptionLabel, it) }
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - tarea pendiente")
@Composable
private fun DailyDetailScreenTaskPreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailScreen(
            state = DailyDetailUiState.Content(
                title = "Escribir resumen semanal",
                type = Spanish.dailyTexts.task,
                status = texts.pending,
                scheduledTime = "09:00",
                description = "Revisar objetivos y métricas",
            ),
            texts = texts,
            onNavigateUp = {},
        )
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - hábito completado")
@Composable
private fun DailyDetailScreenHabitPreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailScreen(
            state = DailyDetailUiState.Content(
                title = "Tomar agua",
                type = Spanish.dailyTexts.habit,
                status = texts.completed,
                scheduledTime = null,
                description = null,
            ),
            texts = texts,
            onNavigateUp = {},
        )
    }
}

@Composable
internal fun DailyDetailMessage(
    message: String,
    texts: DailyDetailTexts,
    onNavigateUp: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = texts.back,
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) {
                Text(actionLabel)
            }
        }
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - Loading")
@Composable
private fun DailyDetailLoadingPreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailMessage(message = texts.loading, texts = texts, onNavigateUp = {})
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - NotFound")
@Composable
private fun DailyDetailNotFoundPreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailMessage(message = texts.notFound, texts = texts, onNavigateUp = {})
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - InvalidRoute")
@Composable
private fun DailyDetailInvalidRoutePreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailMessage(message = texts.invalidRoute, texts = texts, onNavigateUp = {})
    }
}

@Preview(showBackground = true, name = "DailyDetailScreen - Error")
@Composable
private fun DailyDetailErrorPreview() {
    val texts = DailyDetailTexts.from(Spanish)
    GoodLifeTheme {
        DailyDetailMessage(
            message = Spanish.errorTexts.connectionError,
            texts = texts,
            onNavigateUp = {},
            actionLabel = texts.retry,
            onAction = {},
        )
    }
}
