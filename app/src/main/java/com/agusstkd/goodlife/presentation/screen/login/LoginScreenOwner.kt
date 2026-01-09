package com.agusstkd.goodlife.presentation.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla de Login.
 *
 * ## Responsabilidades:
 * - Inyectar el ViewModel con Koin
 * - Colectar el estado de UI
 * - Decidir qué pantalla mostrar según el estado
 *
 * ## Estados:
 * - Loading/Content/Error → LoginScreen
 * - Success → LoginSuccessScreen (luego navegar a Main)
 */
@Composable
fun LoginScreenOwner(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        // Estados de UI → Delegar a LoginScreen
        is LoginUiState.Loading,
        is LoginUiState.Content,
        is LoginUiState.Error -> {
            LoginScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }

        // Estado de navegación → Mostrar éxito y navegar
        is LoginUiState.Success -> {
            LoginSuccessScreen(modifier = modifier)
            // La navegación se maneja en el ViewModel via ComposeNavigationController
        }
    }
}

/**
 * Pantalla temporal de éxito.
 * Se muestra brevemente antes de navegar a Main.
 */
@Composable
private fun LoginSuccessScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✅ Login Exitoso!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        // TODO: Agregar Lottie animation o AnimatedVisibility
    }
}
