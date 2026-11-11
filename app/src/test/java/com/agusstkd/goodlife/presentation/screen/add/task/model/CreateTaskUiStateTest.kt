package com.agusstkd.goodlife.presentation.screen.add.task.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateTaskUiStateTest {

    @Test
    fun `once with title and date is valid`() {
        assertTrue(
            CreateTaskUiState(
                title = "Comprar",
                scheduledDate = LocalDate(2026, 11, 11),
            ).isFormValid,
        )
    }

    @Test
    fun `once without date is not valid`() {
        assertFalse(CreateTaskUiState(title = "Comprar").isFormValid)
    }

    @Test
    fun `recurrent with days and start date is valid`() {
        assertTrue(
            CreateTaskUiState(
                title = "Gym",
                schedulingMode = SchedulingMode.RECURRENT,
                recurrentDays = setOf(DayOfWeek.MONDAY),
                startDate = LocalDate(2026, 11, 11),
            ).isFormValid,
        )
    }

    @Test
    fun `recurrent without days is not valid`() {
        assertFalse(
            CreateTaskUiState(
                title = "Gym",
                schedulingMode = SchedulingMode.RECURRENT,
                startDate = LocalDate(2026, 11, 11),
            ).isFormValid,
        )
    }

    @Test
    fun `loading is not valid`() {
        assertFalse(
            CreateTaskUiState(
                title = "Comprar",
                scheduledDate = LocalDate(2026, 11, 11),
                isLoading = true,
            ).isFormValid,
        )
    }
}
