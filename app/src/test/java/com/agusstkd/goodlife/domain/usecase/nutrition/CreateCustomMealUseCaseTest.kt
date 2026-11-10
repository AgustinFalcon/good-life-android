package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.domain.model.nutrition.CustomMealDraft
import com.agusstkd.goodlife.domain.model.nutrition.CustomMealIngredientDraft
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomMealResult
import com.agusstkd.goodlife.fake.FakeNutritionCatalogRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCustomMealUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeNutritionCatalogRepository()
    private val useCase = CreateCustomMealUseCase(repository, TestDispatcherProvider(dispatcher))

    @Test
    fun `blank name is ValidationError`() = runTest(dispatcher) {
        val result = useCase.execute(validDraft().copy(name = "  "))

        assertEquals(CreateCustomMealResult.ValidationError, result)
        assertNull(repository.lastCreateMealDraft)
    }

    @Test
    fun `success returns created meal`() = runTest(dispatcher) {
        val draft = validDraft()

        val result = useCase.execute(draft)

        assertTrue(result is CreateCustomMealResult.Success)
        assertEquals(draft, repository.lastCreateMealDraft)
        assertEquals(
            FakeNutritionCatalogRepository.defaultMeal(),
            (result as CreateCustomMealResult.Success).meal,
        )
    }

    private fun validDraft() = CustomMealDraft(
        name = "Power bowl",
        description = "Lunch",
        tags = listOf("lunch"),
        ingredients = listOf(
            CustomMealIngredientDraft(ingredientId = 1L, quantity = 200.0, unit = "g"),
        ),
    )
}
