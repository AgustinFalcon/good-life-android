package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import com.agusstkd.goodlife.core.datetime.language.English
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyItemFormattedDescriptionTest {

    @Test
    fun `workout uses day order when exercise count is missing`() {
        val description = workout(
            routineName = "SmokeRutina",
            exerciseCount = null,
            dayOrder = 1,
        ).formattedDescription(Spanish.workoutTexts)

        assertEquals("Rutina: SmokeRutina • Día 1", description)
    }

    @Test
    fun `workout prefers exercise count over day order`() {
        val description = workout(
            routineName = "Push",
            exerciseCount = 4,
            dayOrder = 2,
        ).formattedDescription(Spanish.workoutTexts)

        assertEquals("Rutina: Push • 4 ejercicios", description)
    }

    @Test
    fun `workout copy follows AppLanguage`() {
        val item = workout(routineName = "Push", exerciseCount = 4, dayOrder = null)

        assertEquals(
            "Routine: Push • 4 exercises",
            item.formattedDescription(English.workoutTexts),
        )
    }

    @Test
    fun `meal macros use AppLanguage units`() {
        val item = DailyItem(
            id = 4L,
            type = DailyItemType.MEAL,
            referenceId = 50L,
            scheduledTime = null,
            status = DailyItemStatus.PENDING,
            title = "Almuerzo",
            description = null,
            mealCalories = 700,
            mealProtein = 42,
        )

        assertEquals(
            "700 kcal • 42g",
            item.formattedDescription(
                Spanish.workoutTexts,
                calorieUnit = Spanish.tabDetailTexts.calorieUnit,
                gramUnit = Spanish.tabDetailTexts.gramUnit,
            ),
        )
        assertEquals(
            "700 kcal • 42g",
            item.formattedDescription(
                English.workoutTexts,
                calorieUnit = English.tabDetailTexts.calorieUnit,
                gramUnit = English.tabDetailTexts.gramUnit,
            ),
        )
    }

    @Test
    fun `habit description stays as domain value`() {
        val item = DailyItem(
            id = 2L,
            type = DailyItemType.HABIT,
            referenceId = 20L,
            scheduledTime = null,
            status = DailyItemStatus.PENDING,
            title = "Hydrate",
            description = "2 / 8 vasos",
        )

        assertEquals("2 / 8 vasos", item.formattedDescription(Spanish.workoutTexts))
    }

    private fun workout(
        routineName: String?,
        exerciseCount: Int?,
        dayOrder: Int?,
    ) = DailyItem(
        id = 1L,
        type = DailyItemType.WORKOUT,
        referenceId = 40L,
        scheduledTime = null,
        status = DailyItemStatus.PENDING,
        title = "PushDay",
        description = null,
        workoutRoutineName = routineName,
        workoutExerciseCount = exerciseCount,
        workoutDayOrder = dayOrder,
    )
}
