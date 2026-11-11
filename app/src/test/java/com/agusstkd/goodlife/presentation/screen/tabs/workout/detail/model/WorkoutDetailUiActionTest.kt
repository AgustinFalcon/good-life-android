package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutDetailUiActionTest {

    @Test
    fun `closed set exposes retry finish notes and set field change`() {
        val actions = listOf(
            WorkoutDetailUiAction.OnRetry,
            WorkoutDetailUiAction.OnFinishWorkout,
            WorkoutDetailUiAction.OnNotesChange("feedback"),
            WorkoutDetailUiAction.OnSetFieldChange(1L, 2L, WorkoutSetField.Weight, "80"),
        )

        assertEquals(4, actions.distinct().size)
        assertEquals("retry", label(actions[0]))
        assertEquals("finish", label(actions[1]))
        assertEquals("notes", label(actions[2]))
        assertEquals("set", label(actions[3]))
    }

    private fun label(action: WorkoutDetailUiAction): String = when (action) {
        WorkoutDetailUiAction.OnRetry -> "retry"
        WorkoutDetailUiAction.OnFinishWorkout -> "finish"
        is WorkoutDetailUiAction.OnNotesChange -> "notes"
        is WorkoutDetailUiAction.OnSetFieldChange -> "set"
    }
}
