package com.agusstkd.goodlife.presentation.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel



/**
 * Owner del Splash - Solo conecta ViewModel con Screen.
 *
 * La navegación la maneja el ViewModel directamente con ComposeNavigationController.
 * El Owner solo observa el estado para la UI.
 */
@Composable
fun SplashScreenOwner(
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SplashScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}
