package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiAction
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Settings.
 *
 * Inyecta [SettingsTabViewModel], conecta el estado y delega la navegación:
 * - Logout: lo maneja el ViewModel. El logout limpia los tokens vía [LogoutUseCase];
 *   [SessionEventBus] (escuchado por MainActivity) redirige al login automáticamente.
 * - NavigateToProfile: delegado al caller vía [onNavigateToProfile].
 */
@Composable
fun SettingsScreenOwner(
    onNavigateToProfile: () -> Unit = {}
) {
    val viewModel: SettingsTabViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                is SettingsUiAction.NavigateToProfile -> onNavigateToProfile()
                else -> viewModel.onAction(action)
            }
        }
    )
}
