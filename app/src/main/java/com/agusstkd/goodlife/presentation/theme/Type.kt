package com.agusstkd.goodlife.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 * GOODLIFE - TYPOGRAPHY SYSTEM
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Sistema tipográfico de GoodLife basado en Material Design 3.
 *
 * ## Estrategia de Fuentes:
 * - **Ubuntu**: Usada para títulos y headers (Display, Headline, Title)
 *   - Personalidad amigable y moderna
 *   - Excelente legibilidad en tamaños grandes
 *
 * - **Inter**: Usada para cuerpo de texto y labels (Body, Label)
 *   - Optimizada para pantallas
 *   - Altamente legible en tamaños pequeños
 *
 * ## Escala Tipográfica Material 3:
 *
 * | Rol           | Fuente  | Peso     | Tamaño | Uso                              |
 * |---------------|---------|----------|--------|----------------------------------|
 * | displayLarge  | Ubuntu  | Bold     | 57sp   | Splash, títulos hero             |
 * | displayMedium | Ubuntu  | Bold     | 45sp   | Títulos principales              |
 * | displaySmall  | Ubuntu  | Bold     | 36sp   | Títulos secundarios              |
 * | headlineLarge | Ubuntu  | SemiBold | 32sp   | Headers de sección               |
 * | headlineMedium| Ubuntu  | SemiBold | 28sp   | Subtítulos importantes           |
 * | headlineSmall | Ubuntu  | Medium   | 24sp   | Subtítulos                       |
 * | titleLarge    | Ubuntu  | Medium   | 22sp   | Títulos de cards                 |
 * | titleMedium   | Inter   | SemiBold | 16sp   | Nombres de campos, labels        |
 * | titleSmall    | Inter   | SemiBold | 14sp   | Subtítulos pequeños              |
 * | bodyLarge     | Inter   | Normal   | 16sp   | Texto principal                  |
 * | bodyMedium    | Inter   | Normal   | 14sp   | Texto secundario                 |
 * | bodySmall     | Inter   | Normal   | 12sp   | Texto pequeño, captions          |
 * | labelLarge    | Inter   | Medium   | 14sp   | Botones                          |
 * | labelMedium   | Inter   | Medium   | 12sp   | Labels de campos                 |
 * | labelSmall    | Inter   | Medium   | 11sp   | Labels pequeños, badges          |
 *
 * @see Font.kt para las definiciones de FontFamily
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// TYPOGRAPHY - MATERIAL 3 COMPLETO
// ═══════════════════════════════════════════════════════════════════════════════════════════════

val GoodLifeTypography = Typography(

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DISPLAY - Títulos Hero (Ubuntu Bold)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    displayLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // HEADLINE - Headers de sección (Ubuntu Medium/SemiBold)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    headlineLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TITLE - Títulos de cards y secciones (Ubuntu/Inter Medium)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    titleLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // BODY - Texto de contenido (Inter Regular)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // LABEL - Botones y labels (Inter Medium)
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// ESTILOS PERSONALIZADOS ADICIONALES
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Estilo para el nombre de la app "GoodLife" en splash/login.
 * Usa Ubuntu Bold con tamaño grande para impacto visual.
 */
val AppTitleStyle = TextStyle(
    fontFamily = UbuntuFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 60.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Estilo para placeholders en inputs.
 * Usa Inter Regular con opacidad reducida implícita.
 */
val PlaceholderStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

/**
 * Estilo para texto de botones primarios.
 * Usa Inter SemiBold para legibilidad en botones.
 */
val ButtonTextStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

/**
 * Estilo para links clickeables.
 * Usa Inter Medium con color de link.
 */
val LinkTextStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Estilo para mensajes de error debajo de inputs.
 */
val ErrorTextStyle = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)
