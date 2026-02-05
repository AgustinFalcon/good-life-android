package com.agusstkd.goodlife.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector


// ═══════════════════════════════════════════════════════════════════════════════════════════
// 5. GRADIENT ICON - Icono con degradado
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Icono con degradado aplicado.
 *
 * @param imageVector Icono a mostrar
 * @param colors Colores del degradado
 * @param modifier Modificador de Compose
 * @param contentDescription Descripción para accesibilidad
 */
@Composable
fun GradientIcon(
    imageVector: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                val brush = Brush.linearGradient(colors)
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        tint = Color.White
    )
}
