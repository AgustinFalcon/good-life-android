package com.agusstkd.goodlife.presentation.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.core.extensions.findFragmentActivity
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla de Login.
 *
 * ## Responsabilidades:
 * - Inyectar el ViewModel con Koin
 * - Colectar el estado de UI
 * - Observar [shouldShowBiometricPrompt] y delegar la autenticación al ViewModel
 *   pasando la Activity como contexto opaco (sin conocer BiometricAuthenticator)
 * - Decidir qué pantalla mostrar según el estado
 */
@Composable
fun LoginScreenOwner(
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findFragmentActivity()

    LaunchedEffect(uiState) {
        val content = uiState as? LoginUiState.Content ?: return@LaunchedEffect

        if (content.shouldShowBiometricPrompt && activity != null) {
            viewModel.onAction(LoginUiAction.OnBiometricPromptShown)
            viewModel.onAction(LoginUiAction.OnBiometricAuthenticate(activity))
        }
    }

    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        texts = viewModel.screenTexts
    )
}
