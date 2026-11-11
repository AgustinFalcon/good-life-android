package com.agusstkd.goodlife.presentation.screen.add.habit.model

import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateHabitUiStateTest {

    private fun validState() = CreateHabitUiState(
        name = "Agua",
        selectedCategory = HabitCategory.HEALTH,
        selectedDays = setOf(DayOfWeek.MONDAY),
        targetValue = "8",
        unit = "vasos",
        startDate = LocalDate(2026, 11, 11),
    )

    @Test
    fun `complete form is valid`() {
        assertTrue(validState().isFormValid)
    }

    @Test
    fun `unknown category is not valid`() {
        assertFalse(validState().copy(selectedCategory = HabitCategory.UNKNOWN).isFormValid)
    }

    @Test
    fun `loading is not valid`() {
        assertFalse(validState().copy(isLoading = true).isFormValid)
    }
}
