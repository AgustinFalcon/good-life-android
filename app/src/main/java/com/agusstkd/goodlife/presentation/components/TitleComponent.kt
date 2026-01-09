package com.agusstkd.goodlife.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.theme.AppTitleStyle
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TitleGradientBrush

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 * TITLE COMPONENT - Título con gradiente verde
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 *
 * Componente reutilizable para títulos con degradado.
 * El gradiente va de DarkGreen → LightGreen → DarkGreen.
 *
 * ## Ejemplo de uso:
 * ```kotlin
 * // Con string resource
 * TitleComponent(textId = R.string.app_name)
 *
 * // Con texto directo
 * TitleComponent(text = "GoodLife")
 * ```
 */
@Composable
fun TitleComponent(
    modifier: Modifier = Modifier,
    textId: Int? = null,
    text: String? = null,
    style: TextStyle = AppTitleStyle
) {
    val displayText = textId?.let { stringResource(it) } ?: text ?: ""

    Text(
        modifier = modifier,
        text = displayText,
        style = style.copy(brush = TitleGradientBrush)
    )
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFFE8FFF8)
@Composable
fun TitleComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleComponent(text = "GoodLife")
        }
    }
}
