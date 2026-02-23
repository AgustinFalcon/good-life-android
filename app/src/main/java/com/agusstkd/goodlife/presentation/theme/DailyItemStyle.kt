package com.agusstkd.goodlife.presentation.theme

import androidx.compose.ui.graphics.Color
import com.agusstkd.goodlife.domain.model.daily.DailyItemType

/**
 * Sistema visual para cards de Daily por tipo de item.
 *
 * Centraliza colores de fondo, badge, textos y acción principal
 * para mantener consistencia visual y facilitar futuros rediseños.
 */
data class DailyItemStyle(
    val background: Color,
    val badgeBackground: Color,
    val badgeText: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val timeColor: Color,
    val actionColor: Color
)

val HabitStyle = DailyItemStyle(
    background = Color(0xFFE8F3EC),
    badgeBackground = Color(0xFFD6EBDD),
    badgeText = Color(0xFF2E7D32),
    titleColor = Color(0xFF1B5E20),
    descriptionColor = Color(0xFF5E8C61),
    timeColor = Color(0xFF4CAF50),
    actionColor = Color(0xFF2ED573)
)

val WorkoutStyle = DailyItemStyle(
    background = Color(0xFFF4E6E7),
    badgeBackground = Color(0xFFF0D5D7),
    badgeText = Color(0xFFB23A3A),
    titleColor = Color(0xFF2B2B2B),
    descriptionColor = Color(0xFF7A7A7A),
    timeColor = Color(0xFFD16A6A),
    actionColor = Color(0xFF2ED573)
)

val MealStyle = DailyItemStyle(
    background = Color(0xFFF3E8D8),
    badgeBackground = Color(0xFFEAD7BE),
    badgeText = Color(0xFFB26A00),
    titleColor = Color(0xFF5D4037),
    descriptionColor = Color(0xFF8D6E63),
    timeColor = Color(0xFFEF6C00),
    actionColor = Color(0xFFFFA726)
)

val TaskStyle = DailyItemStyle(
    background = Color(0xFFE3EDF6),
    badgeBackground = Color(0xFFD1E3F3),
    badgeText = Color(0xFF1565C0),
    titleColor = Color(0xFF1A237E),
    descriptionColor = Color(0xFF5C6BC0),
    timeColor = Color(0xFF1E88E5),
    actionColor = Color(0xFF42A5F5)
)

fun DailyItemType.toDailyItemStyle(): DailyItemStyle = when (this) {
    DailyItemType.HABIT -> HabitStyle
    DailyItemType.WORKOUT -> WorkoutStyle
    DailyItemType.MEAL -> MealStyle
    DailyItemType.TASK -> TaskStyle
}
