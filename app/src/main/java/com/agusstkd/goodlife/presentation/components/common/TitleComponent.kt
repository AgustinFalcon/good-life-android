package com.agusstkd.goodlife.presentation.components.common

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
import com.agusstkd.goodlife.presentation.theme.AppTitleStyle
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TitleGradientBrush

/**
 * Título con gradiente verde de la aplicación.
 *
 * @param modifier Modificador de Compose
 * @param textId ID del string resource (opcional)
 * @param text Texto directo (opcional, usado si textId es null)
 * @param style Estilo del texto (default: AppTitleStyle)
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

@Preview(showBackground = true, backgroundColor = 0xFFE8FFF8)
@Composable
private fun TitleComponentPreview() {
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
