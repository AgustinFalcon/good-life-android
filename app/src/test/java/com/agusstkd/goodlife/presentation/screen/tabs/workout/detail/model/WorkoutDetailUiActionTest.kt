package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutDetailUiActionTest {

    @Test
    fun `closed set exposes retry and finish`() {
        val actions = listOf(
            WorkoutDetailUiAction.OnRetry,
            WorkoutDetailUiAction.OnFinishWorkout,
        )

        assertEquals(2, actions.distinct().size)
        assertEquals("retry", label(actions[0]))
        assertEquals("finish", label(actions[1]))
    }

    private fun label(action: WorkoutDetailUiAction): String = when (action) {
        WorkoutDetailUiAction.OnRetry -> "retry"
        WorkoutDetailUiAction.OnFinishWorkout -> "finish"
    }
}
