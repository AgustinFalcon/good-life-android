package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsNavigationEffect
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Settings.
 *
 * Inyecta [SettingsTabViewModel], conecta el estado y colecta efectos de navegación:
 * - Logout: lo maneja el ViewModel. El logout limpia los tokens vía [LogoutUseCase];
 *   [SessionEventBus] (escuchado por MainActivity) redirige al login automáticamente.
 * - NavigateToProfile: el VM emite [SettingsNavigationEffect]; el Owner llama [onNavigateToProfile].
 */
@Composable
fun SettingsScreenOwner(
    onNavigateToProfile: () -> Unit = {}
) {
    val viewModel: SettingsTabViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, onNavigateToProfile) {
        viewModel.navigationEffects.collect { effect ->
            when (effect) {
                is SettingsNavigationEffect.NavigateToProfile -> onNavigateToProfile()
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        texts = viewModel.texts,
        onAction = viewModel::onAction,
    )
}
