package com.agusstkd.goodlife.presentation.screen.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla de registro.
 *
 * ## Responsabilidades:
 * - Inyectar el [RegisterViewModel] via Koin
 * - Observar el estado UI
 * - Delegar el rendering a [RegisterScreen]
 *
 * ## Patrón Owner:
 * Separa la inyección de dependencias de la UI pura,
 * permitiendo que [RegisterScreen] sea testeable y previewable.
 *
 * @param viewModel ViewModel inyectado por Koin
 */
@Composable
fun RegisterScreenOwner(
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
