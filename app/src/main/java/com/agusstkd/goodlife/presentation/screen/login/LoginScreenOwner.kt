package com.agusstkd.goodlife.presentation.screen.login

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig
import com.agusstkd.goodlife.platform.biometric.AndroidBiometricAuthenticator
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Busca la Activity dentro del Context.
 * En Compose, el contexto puede estar envuelto en ContextWrapper.
 */
private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

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
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    // Obtener BiometricAuthenticator de Koin (inyectado como interface)
    val biometricAuthenticator: BiometricAuthenticator = koinInject()

    // Configurar Activity para Android (necesario para BiometricPrompt)
    LaunchedEffect(activity) {
        (biometricAuthenticator as? AndroidBiometricAuthenticator)?.setActivity(activity)
    }

    // Strings para i18n (obtenidos de strings.xml)
    val biometricTitle = stringResource(R.string.biometric_prompt_title)
    val biometricSubtitle = stringResource(R.string.biometric_prompt_subtitle)
    val biometricNegativeButton = stringResource(R.string.biometric_prompt_negative_button)

    // Mostrar BiometricPrompt cuando shouldShowBiometricPrompt es true
    LaunchedEffect(uiState) {
        val content = uiState as? LoginUiState.Content ?: return@LaunchedEffect

        if (content.shouldShowBiometricPrompt && activity != null) {
            // Marcar que ya se mostró (evitar loops)
            viewModel.onAction(LoginUiAction.OnBiometricPromptShown)

            // Mostrar el prompt
            biometricAuthenticator.authenticate(
                config = BiometricPromptConfig(
                    title = biometricTitle,
                    subtitle = biometricSubtitle,
                    negativeButtonText = biometricNegativeButton
                )
            ) { result ->
                viewModel.onAction(LoginUiAction.OnBiometricResult(result))
            }
        }
    }

    when (uiState) {
        // Estados de UI → Delegar a LoginScreen
        is LoginUiState.Loading,
        is LoginUiState.Content,
        is LoginUiState.Error -> {
            LoginScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }

        // Estado de navegación → Mostrar éxito y navegar
        is LoginUiState.Success -> {
            LoginSuccessScreen()
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
