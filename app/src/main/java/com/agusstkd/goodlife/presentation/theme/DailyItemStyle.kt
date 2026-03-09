package com.agusstkd.goodlife.presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.agusstkd.goodlife.domain.model.daily.DailyItemType

/**
 * Sistema visual para cards de Daily por tipo de item.
 *
 * Centraliza todos los colores y el ícono de cada tipo,
 * para mantener una única fuente de verdad y facilitar rediseños.
 *
 * Paleta basada en spec de diseño:
 * - [background]: pastel ultra-claro
 * - [accentColor]: saturado, para íconos y bordes de highlight
 * - [badgeText]: misma tonalidad que [accentColor], sobre fondo blanco
 * - [iconCircleBackground]: tono intermedio para el círculo decorativo
 * - [timeColor]: variante más oscura para contraste del texto de hora
 */
data class DailyItemStyle(
    val background: Color,
    val accentColor: Color,
    val badgeBackground: Color,
    val badgeText: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val timeColor: Color,
    val iconCircleBackground: Color,
    val icon: ImageVector
)

val HabitStyle = DailyItemStyle(
    background           = HabitBackground,
    accentColor          = HabitAccent,
    badgeBackground      = Color.White,
    badgeText            = Color(0xFF4CAF50),
    titleColor           = Color(0xFF1B5E20),
    descriptionColor     = Color(0xFF558B2F),
    timeColor            = Color(0xFF689F38),
    iconCircleBackground = Color(0xFFC8E6C9),
    icon                 = Icons.Outlined.WaterDrop
)

val WorkoutStyle = DailyItemStyle(
    background           = WorkoutBackground,
    accentColor          = WorkoutAccent,
    badgeBackground      = Color.White,
    badgeText            = Color(0xFFE91E63),
    titleColor           = Color(0xFF1A0010),
    descriptionColor     = Color(0xFF880E4F),
    timeColor            = Color(0xFFD81B60),
    iconCircleBackground = Color(0xFFF8BBD0),
    icon                 = Icons.Default.DirectionsRun
)

val MealStyle = DailyItemStyle(
    background           = MealBackground,
    accentColor          = MealAccent,
    badgeBackground      = Color.White,
    badgeText            = Color(0xFFA67C00),
    titleColor           = Color(0xFF4E3A00),
    descriptionColor     = Color(0xFF8D6E63),
    timeColor            = Color(0xFFE6B000),
    iconCircleBackground = Color(0xFFFFECB3),
    icon                 = Icons.Outlined.DinnerDining
)

val TaskStyle = DailyItemStyle(
    background           = TaskBackground,
    accentColor          = TaskAccent,
    badgeBackground      = Color.White,
    badgeText            = Color(0xFF1976D2),
    titleColor           = Color(0xFF0D1B4B),
    descriptionColor     = Color(0xFF5C6BC0),
    timeColor            = Color(0xFF1565C0),
    iconCircleBackground = Color(0xFFBBDEFB),
    icon                 = Icons.Outlined.Assignment
)

fun DailyItemType.toDailyItemStyle(): DailyItemStyle = when (this) {
    DailyItemType.HABIT   -> HabitStyle
    DailyItemType.WORKOUT -> WorkoutStyle
    DailyItemType.MEAL    -> MealStyle
    DailyItemType.TASK    -> TaskStyle
}
