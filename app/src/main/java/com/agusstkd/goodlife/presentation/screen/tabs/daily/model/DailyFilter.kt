package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import com.agusstkd.goodlife.core.datetime.language.DailyTexts
import com.agusstkd.goodlife.domain.model.daily.DailyItemType

/**
 * Filtro activo en la pantalla Daily.
 *
 * [ALL] muestra todos los items.
 * Los demás filtran por tipo de item.
 */
enum class DailyFilter {
    ALL,
    TASK,
    HABIT,
    WORKOUT,
    MEAL;

    /**
     * Convierte el filtro a DailyItemType para comparar con los items.
     * ALL devuelve null (sin filtro).
     */
    fun toItemType(): DailyItemType? = when (this) {
        ALL     -> null
        TASK    -> DailyItemType.TASK
        HABIT   -> DailyItemType.HABIT
        WORKOUT -> DailyItemType.WORKOUT
        MEAL    -> DailyItemType.MEAL
    }

    /**
     * Convierte el filtro a un texto para mostrar en la UI.
     */
    fun toLabel(dailyTexts: DailyTexts): String = when (this) {
        ALL     -> dailyTexts.all
        TASK    -> dailyTexts.task
        HABIT   -> dailyTexts.habit
        WORKOUT -> dailyTexts.workout
        MEAL    -> dailyTexts.meal
    }
}
