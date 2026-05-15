package com.agusstkd.goodlife.domain.model.daily

import kotlinx.serialization.Serializable

/**
 * Tipo de item en el Daily Log.
 *
 * Identifica la categoría de cada actividad registrada en el día.
 * El Daily Log unifica tareas, hábitos, entrenamientos y comidas
 * en una vista centralizada.
 *
 * @Serializable: kotlinx.serialization necesita esta anotación para deserializar
 * el campo "itemType" del JSON. Trade-off aceptado — el enum es simple y estable.
 */
@Serializable
enum class DailyItemType {
    TASK,
    HABIT,
    WORKOUT,
    MEAL
}
