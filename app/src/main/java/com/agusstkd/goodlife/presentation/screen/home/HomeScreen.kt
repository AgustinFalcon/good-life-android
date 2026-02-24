package com.agusstkd.goodlife.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.core.datetime.language.HomeTexts
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.screen.home.model.HomeUiAction
import com.agusstkd.goodlife.presentation.screen.home.model.HomeUiState
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import org.koin.androidx.compose.koinViewModel

/**
 * Owner del HomeScreen.
 * Conecta el ViewModel con la UI.
 */
@Composable
fun HomeScreenOwner(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        homeTexts = viewModel.homeTexts
    )
}

/**
 * Pantalla Home principal.
 *
 * Muestra:
 * - Mensaje de bienvenida con el nombre del usuario
 * - Botón de logout
 */
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    homeTexts: HomeTexts
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is HomeUiState.Loading -> LoadingView()
                is HomeUiState.Content -> ContentView(
                    userName = uiState.userName,
                    userEmail = uiState.userEmail,
                    onLogoutClick = { onAction(HomeUiAction.OnLogoutClick) },
                    homeTexts = homeTexts
                )
                is HomeUiState.Error -> ErrorView(
                    message = uiState.message,
                    onRetryClick = { onAction(HomeUiAction.OnRetryClick) },
                    homeTexts = homeTexts
                )
            }
        }
    }
}

@Composable
private fun LoadingView() {
    CircularProgressIndicator(
        color = LightGreen
    )
}

@Composable
private fun ContentView(
    userName: String,
    userEmail: String,
    onLogoutClick: () -> Unit,
    homeTexts: HomeTexts
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = SuccessGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = homeTexts.welcome,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                brush = Brush.horizontalGradient(
                    colors = listOf(DarkGreen, LightGreen)
                )
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = homeTexts.sessionActive,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        TextButton(
            onClick = onLogoutClick
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = homeTexts.logout,
                color = MaterialTheme.colorScheme.error,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetryClick: () -> Unit,
    homeTexts: HomeTexts
) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = homeTexts.error,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onRetryClick) {
            Text(homeTexts.retry)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    GoodLifeTheme {
        HomeScreen(
            uiState = HomeUiState.Loading,
            onAction = {},
            homeTexts = Spanish.homeTexts
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    GoodLifeTheme {
        HomeScreen(
            uiState = HomeUiState.Content(
                userName = "Agustín",
                userEmail = "agustin@gmail.com"
            ),
            onAction = {},
            homeTexts = Spanish.homeTexts
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenErrorPreview() {
    GoodLifeTheme {
        HomeScreen(
            uiState = HomeUiState.Error("No hay sesión activa"),
            onAction = {},
            homeTexts = Spanish.homeTexts
        )
    }
}
