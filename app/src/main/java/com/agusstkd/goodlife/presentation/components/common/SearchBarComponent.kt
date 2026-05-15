package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

data class SearchBarParams(
    val query: String,
    val placeholder: String,
    val enabled: Boolean = true,
)

/**
 * Barra de búsqueda reutilizable con ícono de lupa.
 *
 * Sigue el sistema de shapes del proyecto (extraLarge = pill) para
 * consistencia con [TextFieldComponent] y [ButtonComponent].
 *
 * @param params Configuración de la barra: query actual, placeholder y estado enabled
 * @param onQueryChange Callback invocado cada vez que el usuario escribe
 * @param modifier Modificador externo — el caller controla posición y tamaño
 */

// todo que se le pueda configurar el icon color, text color y border color
@Composable
fun SearchBarComponent(
    params: SearchBarParams,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = params.query,
        onValueChange = onQueryChange,
        placeholder = { Text(params.placeholder) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        enabled = params.enabled,
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.fillMaxWidth(),
    )
}

// ════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════
@Preview(showBackground = true, name = "SearchBar — vacía")
@Composable
private fun SearchBarEmptyPreview() {
    GoodLifeTheme {
        SearchBarComponent(
            params = SearchBarParams(
                query = "",
                placeholder = "Buscar ingrediente...",
            ),
            onQueryChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "SearchBar — con texto")
@Composable
private fun SearchBarWithTextPreview() {
    GoodLifeTheme {
        SearchBarComponent(
            params = SearchBarParams(
                query = "Pechuga de pollo",
                placeholder = "Buscar ingrediente...",
            ),
            onQueryChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
