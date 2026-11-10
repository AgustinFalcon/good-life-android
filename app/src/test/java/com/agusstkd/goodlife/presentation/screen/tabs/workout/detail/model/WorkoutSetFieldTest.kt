package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import com.agusstkd.goodlife.core.datetime.language.English
import com.agusstkd.goodlife.core.datetime.language.Portuguese
import com.agusstkd.goodlife.core.datetime.language.Spanish
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutSetFieldTest {

    @Test
    fun `spanish labels identify weight reps and rpe`() {
        val texts = Spanish.workoutTexts
        assertEquals("Peso (kg)", WorkoutSetField.Weight.label(texts))
        assertEquals("Reps", WorkoutSetField.Reps.label(texts))
        assertEquals("RPE", WorkoutSetField.Rpe.label(texts))
    }

    @Test
    fun `english labels identify weight reps and rpe`() {
        val texts = English.workoutTexts
        assertEquals("Weight (kg)", WorkoutSetField.Weight.label(texts))
        assertEquals("Reps", WorkoutSetField.Reps.label(texts))
        assertEquals("RPE", WorkoutSetField.Rpe.label(texts))
    }

    @Test
    fun `portuguese labels identify weight reps and rpe`() {
        val texts = Portuguese.workoutTexts
        assertEquals("Peso (kg)", WorkoutSetField.Weight.label(texts))
        assertEquals("Reps", WorkoutSetField.Reps.label(texts))
        assertEquals("RPE", WorkoutSetField.Rpe.label(texts))
    }

    @Test
    fun `weight and reps labels differ in english and are not blank`() {
        val texts = English.workoutTexts
        assertTrue(texts.setWeightLabel.isNotBlank())
        assertTrue(texts.setRepsLabel.isNotBlank())
        assertTrue(texts.setRpeLabel.isNotBlank())
        assertTrue(texts.setWeightLabel != texts.setRepsLabel)
    }
}
