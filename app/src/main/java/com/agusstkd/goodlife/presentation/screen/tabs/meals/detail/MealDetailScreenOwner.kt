package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Owner for the tab-local, read-only meal-plan detail. */
@Composable
fun MealDetailScreenOwner(
    planId: Long,
    dateIso: String,
    onNavigateUp: () -> Unit,
    viewModel: MealDetailViewModel = koinViewModel(parameters = { parametersOf(planId, dateIso) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val texts = viewModel.texts

    when (val currentState = state) {
        MealDetailUiState.Loading -> MealDetailMessage(
            message = texts.loading,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is MealDetailUiState.Content -> MealDetailScreen(currentState, texts, onNavigateUp)
        MealDetailUiState.NotFound -> MealDetailMessage(
            message = texts.notFound,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        MealDetailUiState.InvalidRoute -> MealDetailMessage(
            message = texts.invalidRoute,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is MealDetailUiState.Error -> MealDetailMessage(
            message = currentState.message,
            primaryActionLabel = texts.retry,
            onPrimaryAction = viewModel::retry,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
    }
}

@Composable
internal fun MealDetailMessage(
    message: String,
    backLabel: String,
    onNavigateUp: () -> Unit,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (primaryActionLabel != null && onPrimaryAction != null) {
            Button(onClick = onPrimaryAction, modifier = Modifier.padding(top = 16.dp)) {
                Text(primaryActionLabel)
            }
        }
        Button(onClick = onNavigateUp, modifier = Modifier.padding(top = 8.dp)) { Text(backLabel) }
    }
}