package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiState

/**
 * Pantalla de Settings (UI pura).
 *
 * Muestra opciones de cuenta: acceso al perfil y cerrar sesión.
 * No contiene lógica de negocio — todo viene del ViewModel vía [SettingsScreenOwner].
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Mi cuenta",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            SettingsOptionCard(
                icon = Icons.Default.Person,
                title = "Mi Perfil",
                subtitle = uiState.username.ifBlank { "Ver perfil" },
                onClick = { onAction(SettingsUiAction.NavigateToProfile) }
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            SettingsOptionCard(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Cerrar sesión",
                subtitle = "Salir de tu cuenta",
                onClick = { onAction(SettingsUiAction.Logout) },
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SettingsOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = tint
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
