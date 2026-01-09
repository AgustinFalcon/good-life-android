package com.agusstkd.goodlife.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 * GOODLIFE - THEME CONFIGURATION
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Configuración del tema Material 3 para GoodLife.
 *
 * ## Filosofía del Tema:
 * - **Modo Claro**: Fondo blanco/claro con acentos verdes vibrantes
 * - **Modo Oscuro**: Fondo oscuro con verdes más brillantes para contraste
 *
 * ## Características:
 * - NO usa Dynamic Color para mantener identidad de marca consistente
 * - Configura automáticamente la barra de estado según el tema
 * - Soporta edge-to-edge con status bar transparente
 *
 * @see Color.kt para la paleta de colores
 * @see Type.kt para la tipografía
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// LIGHT COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Esquema de colores para modo claro.
 *
 * Usa los verdes característicos de GoodLife sobre fondos claros,
 * proporcionando una experiencia fresca y natural.
 */
private val LightColorScheme = lightColorScheme(
    // --- Colores Primarios ---
    primary = LightGreen,
    onPrimary = Color.White,
    primaryContainer = DegradeBackground2,
    onPrimaryContainer = DarkGreen,

    // --- Colores Secundarios ---
    secondary = DarkGreen,
    onSecondary = Color.White,
    secondaryContainer = DegradeBackground1,
    onSecondaryContainer = DarkGreen,

    // --- Colores Terciarios ---
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = DegradeBackground3,
    onTertiaryContainer = DarkGreen,

    // --- Fondos ---
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,

    // --- Bordes y Outlines ---
    outline = BorderDefault,
    outlineVariant = DividerColor,

    // --- Errores ---
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // --- Inverse (para snackbars, etc.) ---
    inverseSurface = SurfaceDark,
    inverseOnSurface = TextPrimaryDark,
    inversePrimary = LightGreenDark,

    // --- Scrim ---
    scrim = Color.Black.copy(alpha = 0.32f)
)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Esquema de colores para modo oscuro.
 *
 * Ajusta los verdes para mayor brillo y contraste sobre fondos oscuros,
 * manteniendo la identidad de marca mientras reduce fatiga visual.
 */
private val DarkColorScheme = darkColorScheme(
    // --- Colores Primarios ---
    primary = LightGreenDark,
    onPrimary = DarkGreen,
    primaryContainer = DarkGreenDark,
    onPrimaryContainer = LightGreenDark,

    // --- Colores Secundarios ---
    secondary = GreenSelected,
    onSecondary = DarkGreen,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = LightGreenDark,

    // --- Colores Terciarios ---
    tertiary = EmeraldGreen,
    onTertiary = DarkGreen,
    tertiaryContainer = Color(0xFF005B4F),
    onTertiaryContainer = DegradeBackground2,

    // --- Fondos ---
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark,

    // --- Bordes y Outlines ---
    outline = BorderDefaultDark,
    outlineVariant = Color(0xFF444444),

    // --- Errores ---
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // --- Inverse ---
    inverseSurface = SurfaceLight,
    inverseOnSurface = TextPrimary,
    inversePrimary = LightGreen,

    // --- Scrim ---
    scrim = Color.Black.copy(alpha = 0.5f)
)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// THEME COMPOSABLE
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Tema principal de GoodLife.
 *
 * Configura Material Theme con la paleta de colores y tipografía personalizada.
 * También configura la barra de estado para que coincida con el tema.
 *
 * @param darkTheme Si es true, usa el tema oscuro. Por defecto sigue la configuración del sistema.
 * @param content El contenido composable a mostrar con este tema.
 */
@Composable
fun GoodLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Configurar la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GoodLifeTypography,
        content = content
    )
}

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// HELPER - COLORES EXTENDIDOS
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Colores extendidos que no están en Material ColorScheme.
 */
data class ExtendedColors(
    val link: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

/** Colores extendidos para modo claro */
val LightExtendedColors = ExtendedColors(
    link = TextLink,
    success = SuccessGreen,
    warning = WarningOrange,
    info = InfoBlue,
    gradientStart = DegradeBackground1,
    gradientEnd = DegradeBackground5
)

/** Colores extendidos para modo oscuro */
val DarkExtendedColors = ExtendedColors(
    link = Color(0xFF64B5F6),
    success = Color(0xFF81C784),
    warning = Color(0xFFFFB74D),
    info = Color(0xFF4FC3F7),
    gradientStart = Color(0xFF1A3A2F),
    gradientEnd = Color(0xFF1A2F3A)
)
