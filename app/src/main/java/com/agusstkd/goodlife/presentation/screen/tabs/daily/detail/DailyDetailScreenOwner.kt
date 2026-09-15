package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailTexts
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Owner for injection and state routing; rendering stays in pure composables. */
@Composable
fun DailyDetailScreenOwner(
    itemId: Long,
    dateIso: String,
    onNavigateUp: () -> Unit,
    viewModel: DailyDetailViewModel = koinViewModel(
        parameters = { parametersOf(itemId, dateIso) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = state) {
        DailyDetailUiState.Loading -> DailyDetailMessage(
            message = viewModel.texts.loading,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        is DailyDetailUiState.Content -> DailyDetailScreen(
            state = currentState,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        DailyDetailUiState.NotFound -> DailyDetailMessage(
            message = viewModel.texts.notFound,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        DailyDetailUiState.InvalidRoute -> DailyDetailMessage(
            message = viewModel.texts.invalidRoute,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        is DailyDetailUiState.Error -> DailyDetailMessage(
            message = currentState.message,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
            actionLabel = viewModel.texts.retry,
            onAction = viewModel::retry,
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
