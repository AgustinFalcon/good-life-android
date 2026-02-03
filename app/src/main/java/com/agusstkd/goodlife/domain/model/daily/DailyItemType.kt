package com.agusstkd.goodlife.domain.model.daily

/**
 * Tipo de item en el Daily Log.
 *
 * Identifica la categoría de cada actividad registrada en el día.
 * El Daily Log unifica tareas, hábitos, entrenamientos y comidas
 * en una vista centralizada.
 */
enum class DailyItemType {
    TASK,
    HABIT,
    WORKOUT,
    MEAL
}
