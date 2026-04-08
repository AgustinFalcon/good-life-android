package com.agusstkd.goodlife.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 * GOODLIFE - COLOR PALETTE
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Paleta de colores oficial de GoodLife, inspirada en naturaleza, salud y bienestar.
 *
 * ## Filosofía de Diseño:
 * - **Verde Oscuro (DarkGreen)**: Estabilidad, crecimiento, confianza
 * - **Verde Claro (LightGreen)**: Frescura, vitalidad, energía positiva
 * - **Degradados Aqua**: Transición suave entre salud física y mental
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 1. COLORES PRIMARIOS DE MARCA
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Verde oscuro principal - Color de marca principal.
 * Uso: Títulos, iconos activos, elementos destacados, inicio de gradientes.
 */
val DarkGreen = Color(0xFF003C3C)

/**
 * Verde claro vibrante - Color de acento principal.
 * Uso: Botones, enlaces, elementos interactivos, punto medio de gradientes.
 */
val LightGreen = Color(0xFF1EC691)

/**
 * Verde seleccionado - Para estados de selección.
 * Uso: Items seleccionados, checkboxes activos, estados hover.
 */
val GreenSelected = Color(0xFF00D26B)

/**
 * Verde esmeralda - Variante elegante.
 * Uso: Acentos sutiles, badges premium.
 */
val EmeraldGreen = Color(0xFF50C878)


// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 2. COLORES POR FEATURE (Daily Items)
// ═══════════════════════════════════════════════════════════════════════════════════════════════
// Cada feature tiene: Background (fondo pastel), AccentLight (gradiente claro), Accent (acento principal).
// Uso de AccentLight: inicio del gradiente en ButtonComponent.
// Uso de Accent: fin del gradiente, chips, iconos, bordes activos.

val HabitBackground  = Color(0xFFF1F8E9)
val HabitAccentLight = Color(0xFF81C784)  // Verde claro — inicio de gradiente
val HabitAccent      = Color(0xFF4CAF50)  // Verde medio — acento principal

val WorkoutBackground  = Color(0xFFFCE4EC)
val WorkoutAccentLight = Color(0xFFF06292)  // Rosa claro — inicio de gradiente
val WorkoutAccent      = Color(0xFFE91E63)  // Rosa vibrante — acento principal

val MealBackground  = Color(0xFFFFF9E6)
val MealAccentLight = Color(0xFFFFCA28)  // Ámbar claro — inicio de gradiente
val MealAccent      = Color(0xFFA67C00)  // Ámbar oscuro — acento principal

val TaskBackground  = Color(0xFFE3F2FD)
val TaskAccentLight = Color(0xFF64B5F6)  // Azul claro — inicio de gradiente
val TaskAccent      = Color(0xFF1976D2)  // Azul medio — acento principal

val CompletionGreen = Color(0xFF2ECC71)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 3. COLORES DE FONDO (DEGRADADO PRINCIPAL)
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Colores para el degradado de fondo de las pantallas de autenticación.
 * Transición: Verde menta → Aqua → Celeste (de arriba hacia abajo)
 *
 * NOTA: Mantenemos la nomenclatura DegradeBackground para compatibilidad con componentes existentes.
 */
val DegradeBackground1 = Color(0xFFB7FFD6)  // Verde menta claro (top)
val DegradeBackground2 = Color(0xFFA3F6E8)  // Verde aqua
val DegradeBackground3 = Color(0xFF85F8DC)  // Aqua
val DegradeBackground4 = Color(0xFF84E8F6)  // Aqua celeste
val DegradeBackground5 = Color(0xFF6EDBFF)  // Celeste claro (bottom) - Usado en gradientes de botones/inputs

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 4. COLORES SEMÁNTICOS
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/** Color de éxito - Para mensajes positivos y confirmaciones */
val SuccessGreen = Color(0xFF4CAF50)

/** Color de error - Para errores y advertencias críticas */
val ErrorRed = Color(0xFFE53935)

/** Color de advertencia - Para alertas moderadas */
val WarningOrange = Color(0xFFFF9800)

/** Color informativo - Para mensajes informativos */
val InfoBlue = Color(0xFF2196F3)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 5. COLORES DE UI - COMPONENTES
// ═══════════════════════════════════════════════════════════════════════════════════════════════

// --- Texto ---
val TextPrimary = Color(0xFF1A1A1A)        // Texto principal (casi negro)
val TextSecondary = Color(0xFF666666)      // Texto secundario
val TextTertiary = Color(0xFF999999)       // Texto terciario / placeholder
val TextOnPrimary = Color(0xFFFFFFFF)      // Texto sobre fondos primarios
val TextLink = Color(0xFF3F8CFF)           // Enlaces

// --- Superficies ---
val SurfaceLight = Color(0xFFFFFFFF)       // Fondo claro
val SurfaceDark = Color(0xFF121212)        // Fondo oscuro
val SurfaceCard = Color(0xFFF8F8F8)        // Fondo de cards

// --- Bordes e Inputs ---
val BorderDefault = Color(0xFFE0E0E0)      // Borde normal
val BorderFocused = LightGreen             // Borde cuando tiene foco
val BorderError = ErrorRed                 // Borde con error

// --- Botones ---
val ButtonDisabledBg = Color(0xFFBDBDBD)   // Fondo de botón deshabilitado
val ButtonDisabledText = Color(0xFF9E9E9E) // Texto de botón deshabilitado

// --- Divisores ---
val DividerColor = Color(0xFFE0E0E0)       // Color de líneas divisorias

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 6. COLORES MODO OSCURO
// ═══════════════════════════════════════════════════════════════════════════════════════════════

val DarkGreenDark = Color(0xFF00524F)      // Verde oscuro más brillante para dark mode
val LightGreenDark = Color(0xFF26D9A0)     // Verde claro más vibrante para dark mode

val TextPrimaryDark = Color(0xFFE8E8E8)    // Texto principal en dark mode
val TextSecondaryDark = Color(0xFFB0B0B0)  // Texto secundario en dark mode
val TextTertiaryDark = Color(0xFF808080)   // Texto terciario en dark mode

val SurfaceCardDark = Color(0xFF1E1E1E)    // Fondo de cards en dark mode
val BorderDefaultDark = Color(0xFF3D3D3D)  // Borde en dark mode

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 7. BRUSHES - DEGRADADOS REUTILIZABLES
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Degradado horizontal para títulos.
 * Efecto: DarkGreen → LightGreen → DarkGreen
 */
val TitleGradientBrush = Brush.horizontalGradient(
    colors = listOf(DarkGreen, LightGreen, DarkGreen)
)

/**
 * Degradado vertical para fondos de pantallas de autenticación.
 * Efecto: Verde menta → Aqua → Celeste (de arriba hacia abajo)
 */
val BackgroundGradientBrushVertical = Brush.verticalGradient(
    colors = listOf(
        DegradeBackground1,
        DegradeBackground2,
        DegradeBackground3,
        DegradeBackground4,
        DegradeBackground5
    )
)

val BackgroundGradientBrushHorizontal = Brush.verticalGradient(
    colors = listOf(
        DegradeBackground5,
        DegradeBackground4,
        DegradeBackground3,
        DegradeBackground2,
        DegradeBackground1,
    )
)

/**
 * Degradado horizontal para botones primarios ENABLED.
 * Efecto: DarkGreen → LightGreen → Celeste
 *
 * Uso en ButtonComponent:
 * ```kotlin
 * Box(
 *     modifier = Modifier.background(
 *         brush = ButtonEnabledGradientBrush,
 *         shape = RoundedCornerShape(50.dp)
 *     )
 * )
 * ```
 */
val ButtonEnabledGradientBrush = Brush.horizontalGradient(
    colors = listOf(DarkGreen, LightGreen, DegradeBackground5)
)

/**
 * Degradado para botones DISABLED.
 * Efecto: Gris claro → Gris oscuro
 */
val ButtonDisabledGradientBrush = Brush.horizontalGradient(
    colors = listOf(ButtonDisabledBg, Color(0xFF9E9E9E))
)

/**
 * Degradado horizontal para bordes de inputs en estado NORMAL.
 * Efecto: DarkGreen → LightGreen → Celeste
 *
 * Uso en TextFieldComponent:
 * ```kotlin
 * Modifier.border(
 *     width = 2.dp,
 *     brush = InputBorderNormalGradientBrush,
 *     shape = RoundedCornerShape(50.dp)
 * )
 * ```
 */
val InputBorderNormalGradientBrush = Brush.horizontalGradient(
    colors = listOf(DarkGreen, LightGreen, DegradeBackground5)
)

/**
 * Degradado para bordes de inputs en estado FOCUSED.
 * Efecto: Verde sólido (mismo color en toda la línea)
 */
val InputBorderFocusedGradientBrush = Brush.horizontalGradient(
    colors = listOf(LightGreen, LightGreen, LightGreen)
)

/**
 * Degradado para bordes de inputs en estado ERROR.
 * Efecto: Rojo sólido
 */
val InputBorderErrorGradientBrush = Brush.horizontalGradient(
    colors = listOf(ErrorRed, ErrorRed, ErrorRed)
)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 8. LISTAS DE COLORES PARA ESTADOS DE COMPONENTES
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Lista de colores para estado NORMAL de inputs/botones.
 * Uso: Brush.horizontalGradient(colors = InputBorderColorsNormal)
 */
val InputBorderColorsNormal = listOf(DarkGreen, LightGreen, DegradeBackground5)

/**
 * Lista de colores para estado FOCUSED de inputs.
 * Uso: Brush.horizontalGradient(colors = InputBorderColorsFocused)
 */
val InputBorderColorsFocused = listOf(LightGreen, LightGreen, LightGreen)

/**
 * Lista de colores para estado ERROR de inputs.
 * Uso: Brush.horizontalGradient(colors = InputBorderColorsError)
 */
val InputBorderColorsError = listOf(ErrorRed, ErrorRed, ErrorRed)

/**
 * Lista de colores para botón ENABLED.
 * Uso: Brush.horizontalGradient(colors = ButtonColorsEnabled)
 */
val ButtonColorsEnabled = listOf(DarkGreen, LightGreen, DegradeBackground5)

/**
 * Lista de colores para botón DISABLED.
 * Uso: Brush.horizontalGradient(colors = ButtonColorsDisabled)
 */
val ButtonColorsDisabled = listOf(ButtonDisabledBg, ButtonDisabledText)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 9. COLORES Y BRUSHES PARA LOGIN/REGISTER (Diseño limpio)
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Verde menta muy claro para fondos de pantallas de autenticación.
 * Más suave y limpio que el gradiente del Splash.
 */
val BackgroundMint = Color(0xFFE8FFF8)

/**
 * Lista de colores para botón de Auth (Login/Register).
 */
val AuthButtonColors = listOf(LightGreen, GreenSelected)

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// 10. COLORES DE NUTRICIÓN
// ═══════════════════════════════════════════════════════════════════════════════════════════════

/**
 * Naranja claro - Inicio de gradiente para botones del módulo de nutrición.
 */
val NutritionAccentLight = Color(0xFFFFB74D)

/**
 * Naranja oscuro - Acento principal del módulo de nutrición.
 * Uso: Badge "COMIDA", chips de MealType seleccionados, barra de progreso del wizard.
 */
val NutritionAccent = Color(0xFFF57C00)

/**
 * Naranja muy suave - Fondo del badge "COMIDA".
 * Uso: Fondo de chips de MealType no seleccionados, fondos de cards de nutrición.
 */
val NutritionBackground = Color(0xFFFFF3E0)

/**
 * Naranja rojizo - Color para calorías.
 * Uso: MacrosSummaryCard (valor de kcal), indicadores de energía.
 */
val CaloriesColor = Color(0xFFFF7043)

/**
 * Azul claro - Color para proteínas.
 * Uso: MacrosSummaryCard (valor de proteínas).
 */
val ProteinColor = Color(0xFF42A5F5)

/**
 * Amarillo dorado - Color para carbohidratos.
 * Uso: MacrosSummaryCard (valor de carbohidratos).
 */
val CarbsColor = Color(0xFFFFCA28)

/**
 * Violeta - Color para grasas.
 * Uso: MacrosSummaryCard (valor de grasas).
 */
val FatColor = Color(0xFFAB47BC)

/**
 * Azul oscuro - Label de proteína en la macro card del nuevo diseño.
 * Uso: texto "PROTEÍNA" y valor en MacrosSummaryCard.
 */
val ProteinColorDark = Color(0xFF004A78)

/**
 * Ámbar oscuro - Label de carbohidratos en la macro card del nuevo diseño.
 * Uso: texto "CARBOS" y valor en MacrosSummaryCard.
 */
val CarbsColorDark = Color(0xFF7B4F00)

/**
 * Violeta oscuro - Label de grasas en la macro card del nuevo diseño.
 * Uso: texto "GRASAS" y valor en MacrosSummaryCard.
 */
val FatColorDark = Color(0xFF4A0072)

/**
 * Naranja primario oscuro - Usado para texto y elementos de acento sobre fondo claro.
 * Mapea a `primary` (#964900) del design system de nutrición.
 */
val NutritionPrimary = Color(0xFF964900)
