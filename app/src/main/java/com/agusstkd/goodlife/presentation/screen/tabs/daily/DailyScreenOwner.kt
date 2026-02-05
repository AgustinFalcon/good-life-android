package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Daily.
 *
 * Inyecta el ViewModel y conecta el estado con la UI pura.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun DailyScreenOwner(
    viewModel: DailyTabViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DailyScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
