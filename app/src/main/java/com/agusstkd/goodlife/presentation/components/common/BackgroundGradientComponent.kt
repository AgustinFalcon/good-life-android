package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.agusstkd.goodlife.presentation.theme.BackgroundMint
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Componente de fondo con gradiente y efectos de glow.
 *
 * Crea un fondo con:
 * - Gradiente vertical de menta claro a blanco
 * - Glow verde en esquina superior izquierda
 * - Glow verde en esquina inferior derecha
 *
 * @param modifier Modificador del componente
 * @param content Contenido a mostrar sobre el fondo
 */
@Composable
fun BackgroundGradientComponent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Capa 1: Gradiente base vertical (menta claro → blanco)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundMint,
                            Color.White
                        )
                    )
                )
        )

        // Capa 2: Glow superior izquierdo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LightGreen.copy(alpha = 0.20f),
                            Color.Transparent
                        ),
                        center = Offset(0f, 0f),
                        radius = widthPx * 0.8f
                    )
                )
        )

        // Capa 3: Glow inferior derecho
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LightGreen.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(widthPx, heightPx),
                        radius = widthPx * 0.8f
                    )
                )
        )

        // Contenido
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
