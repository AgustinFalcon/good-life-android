package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.model.nutrition.ScheduledMealDraft
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateMealPlanResult
import com.agusstkd.goodlife.fake.FakeMealPlanRepository
import com.agusstkd.goodlife.fake.FakeNutritionCatalogRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateMealPlanUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeMealPlanRepository()
    private val useCase = CreateMealPlanUseCase(repository, TestDispatcherProvider(dispatcher))

    @Test
    fun `maps draft to backend meal-plan contract`() = runTest(dispatcher) {
        val result = useCase.execute(validDraft())

        assertTrue(result is CreateMealPlanResult.Success)
        val draft = repository.lastCreateDraft!!
        val scheduled = draft.scheduledMeals.single()
        assertEquals(1L, scheduled.selectedMeal?.id)
        assertEquals(MealType.LUNCH, scheduled.mealType)
        assertEquals(LocalTime(13, 30), scheduled.scheduledTime)
        assertEquals(setOf(DayOfWeek.TUESDAY), draft.daysOfWeek)
        assertEquals(LocalDate(2026, 11, 10), draft.startDate)
        assertEquals(null, draft.endDate)
    }

    @Test
    fun `BadRequest maps to ServerError with backend message`() = runTest(dispatcher) {
        repository.createResult = Result.Error(ApiException.BadRequestException("mealId es obligatorio"))

        val result = useCase.execute(validDraft())

        assertEquals(CreateMealPlanResult.ServerError("mealId es obligatorio"), result)
    }

    @Test
    fun `unknown meal type is ValidationError and does not call API`() = runTest(dispatcher) {
        val scheduled = validDraft().scheduledMeals.single().copy(mealType = MealType.UNKNOWN)
        val result = useCase.execute(validDraft().copy(scheduledMeals = listOf(scheduled)))

        assertEquals(CreateMealPlanResult.ValidationError, result)
        assertEquals(0, repository.createCallCount)
    }

    @Test
    fun `empty days is ValidationError and does not call API`() = runTest(dispatcher) {
        val result = useCase.execute(validDraft().copy(daysOfWeek = emptySet()))

        assertEquals(CreateMealPlanResult.ValidationError, result)
        assertEquals(0, repository.createCallCount)
    }

    private fun validDraft() = MealPlanDraft(
        name = "Almuerzo",
        description = null,
        scheduledMeals = listOf(
            ScheduledMealDraft(
                mealType = MealType.LUNCH,
                scheduledTime = LocalTime(13, 30),
                selectedMeal = FakeNutritionCatalogRepository.defaultMeal(),
                customIngredients = emptyList(),
            )
        ),
        daysOfWeek = setOf(DayOfWeek.TUESDAY),
        startDate = LocalDate(2026, 11, 10),
        endDate = null,
    )
}
