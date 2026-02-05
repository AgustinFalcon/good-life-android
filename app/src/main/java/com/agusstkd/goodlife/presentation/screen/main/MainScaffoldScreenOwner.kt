package com.agusstkd.goodlife.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScaffoldScreenOwner(
    viewModel: MainScaffoldViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScaffoldScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
