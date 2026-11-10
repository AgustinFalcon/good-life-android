package com.agusstkd.goodlife.data.remote.dto.response.training

import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutineResponseTest {

    @Test
    fun `known difficulty and goal map through fromWire`() {
        val routine = sampleRoutine(difficultyLevel = "ADVANCED", goalType = "ENDURANCE").toDomain()
        assertEquals(DifficultyLevel.ADVANCED, routine.difficultyLevel)
        assertEquals(GoalType.ENDURANCE, routine.goalType)
        assertEquals("Fuerza", routine.name)
    }

    @Test
    fun `unknown difficulty and goal are kept not remapped to Beginner or Strength`() {
        val routine = sampleRoutine(difficultyLevel = "EXPERT", goalType = "HYPERTROPHY").toDomain()
        assertEquals(DifficultyLevel.UNKNOWN, routine.difficultyLevel)
        assertEquals(GoalType.UNKNOWN, routine.goalType)
        assertEquals("Fuerza", routine.name)
    }

    private fun sampleRoutine(difficultyLevel: String?, goalType: String?) = RoutineResponse(
        id = 1L,
        name = "Fuerza",
        difficultyLevel = difficultyLevel,
        goalType = goalType,
    )
}
