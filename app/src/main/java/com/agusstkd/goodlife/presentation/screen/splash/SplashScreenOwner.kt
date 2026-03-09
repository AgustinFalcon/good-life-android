package com.agusstkd.goodlife.presentation.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreenOwner(
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SplashScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        tapToContinueText = viewModel.splashTexts.tapToContinue,
        logoContentDescription = viewModel.accessibilityTexts.appLogo,
    )
}
