package com.agusstkd.goodlife.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.agusstkd.goodlife.R

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 * GOODLIFE - FONT FAMILIES
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Este archivo define las familias de fuentes personalizadas para la aplicación GoodLife.
 *
 * ## Fuentes Disponibles:
 *
 * ### Ubuntu
 * - **Uso principal**: Títulos, headers, elementos destacados
 * - **Características**: Fuente humanista con personalidad amigable y moderna
 * - **Pesos disponibles**: Light (300), Regular (400), Medium (500), Bold (700)
 *
 * ### Inter
 * - **Uso principal**: Cuerpo de texto, placeholders, labels, botones
 * - **Características**: Fuente sans-serif altamente legible, optimizada para pantallas
 * - **Pesos disponibles**: Light (300), Regular (400), Medium (500), SemiBold (600), Bold (700)
 *
 * ## Ejemplo de Uso:
 * ```kotlin
 * Text(
 *     text = "Bienvenido",
 *     fontFamily = UbuntuFontFamily,
 *     fontWeight = FontWeight.Bold
 * )
 * ```
 *
 * @see Type.kt para ver las tipografías predefinidas que usan estas fuentes
 */// ═══════════════════════════════════════════════════════════════════════════════════════════════
// UBUNTU FONT FAMILY
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Familia de fuentes Ubuntu.
 *
 * Fuente humanista moderna, ideal para títulos y elementos que requieren personalidad.
 * Diseñada para ser clara y legible en todos los tamaños.
 */
val UbuntuFontFamily = FontFamily(
    Font(R.font.ubuntu_light, FontWeight.Light),
    Font(R.font.ubuntu_regular, FontWeight.Normal),
    Font(R.font.ubuntu_medium, FontWeight.Medium),
    Font(R.font.ubuntu_bold, FontWeight.Bold)
)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// INTER FONT FAMILY
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Familia de fuentes Inter.
 *
 * Fuente sans-serif diseñada específicamente para pantallas de alta resolución.
 * Altamente legible en tamaños pequeños, perfecta para cuerpo de texto y UI elements.
 */
val InterFontFamily = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)
