package com.agusstkd.goodlife.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes de Material 3 para GoodLife.
 *
 * Uso en componentes:
 * ```kotlin
 * shape = MaterialTheme.shapes.medium
 * shape = MaterialTheme.shapes.extraLarge
 * ```
 *
 * Escala:
 * - extraSmall: 4dp - Badges, chips pequeños
 * - small: 8dp - Chips, campos pequeños
 * - medium: 12dp - Cards, dialogs
 * - large: 16dp - Bottom sheets
 * - extraLarge: 50dp - Botones pill, inputs redondeados
 */
val GoodLifeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(50.dp)
)
