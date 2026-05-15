package com.agusstkd.goodlife.domain.model.habit

/**
 * Categorías de hábito definidas por el backend.
 *
 * Cada categoría agrupa hábitos de un mismo ámbito de bienestar.
 * El backend espera el nombre exacto del enum como String (ej: "HYDRATION").
 *
 * @see Habit
 */
enum class HabitCategory {
    HYDRATION,
    MEDITATION,
    READING,
    EXERCISE,
    SLEEP,
    NUTRITION,
    LEARNING,
    MINDFULNESS,
    SOCIAL,
    CREATIVITY,
    PRODUCTIVITY,
    HEALTH,
    CUSTOM,
}
