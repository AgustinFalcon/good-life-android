package com.agusstkd.goodlife.presentation.screen.tabs.workout

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
import com.agusstkd.goodlife.core.datetime.language.WorkoutTexts
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import com.agusstkd.goodlife.presentation.theme.WorkoutsTabAccent
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Workouts.
 *
 * Inyecta el ViewModel y conecta el estado con la UI pura.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun WorkoutsScreenOwner(
    viewModel: WorkoutsTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        WorkoutsUiState.Loading -> {
            WorkoutsLoadingState()
        }

        WorkoutsUiState.NoRoutine -> {
            WorkoutsNoRoutineState(
                texts = viewModel.workoutTexts,
                onCreateRoutine = { viewModel.onAction(WorkoutsUiAction.OnCreateRoutine) }
            )
        }

        is WorkoutsUiState.Success -> {
            WorkoutsScreen(
                uiState = state,
                texts = viewModel.workoutTexts,
                onAction = viewModel::onAction
            )
        }

        is WorkoutsUiState.Error -> {
            WorkoutsErrorState(
                message = state.message,
                retryText = viewModel.workoutTexts.retry,
                onRetry = { viewModel.onAction(WorkoutsUiAction.OnRefresh) }
            )
        }
    }
}

@Composable
private fun WorkoutsLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SkeletonBlock(height = 120.dp, alpha = 0.35f)
        SkeletonBlock(height = 80.dp, alpha = 0.30f)
        SkeletonBlock(height = 80.dp, alpha = 0.25f)
        SkeletonBlock(height = 80.dp, alpha = 0.20f)
    }
}

@Composable
private fun WorkoutsNoRoutineState(
    texts: WorkoutTexts,
    onCreateRoutine: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = texts.noRoutineTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = texts.noRoutineDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCreateRoutine,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WorkoutsTabAccent
                )
            ) {
                Text(text = texts.createRoutine)
            }
        }
    }
}

@Composable
private fun WorkoutsErrorState(
    message: String,
    retryText: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$message\n$retryText",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(32.dp)
                .clickable(onClick = onRetry)
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
