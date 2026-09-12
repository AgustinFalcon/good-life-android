package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

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
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorkoutDetailScreenOwner(
    workoutId: Long,
    onNavigateUp: () -> Unit,
    viewModel: WorkoutDetailViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = state) {
        WorkoutDetailUiState.Loading -> DetailMessage(viewModel.texts.loading)
        is WorkoutDetailUiState.Content -> WorkoutDetailScreen(currentState, viewModel.texts, onNavigateUp)
        WorkoutDetailUiState.NotFound -> DetailMessage(viewModel.texts.workoutNotFound)
        is WorkoutDetailUiState.Error -> DetailMessage(currentState.message, viewModel.texts.retry, viewModel::retry)
    }
}

@Composable
private fun DetailMessage(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) { Text(actionLabel) }
        }
    }
}
