package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.CustomIngredientDraft
import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomIngredientResult
import com.agusstkd.goodlife.fake.FakeNutritionCatalogRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCustomIngredientUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeNutritionCatalogRepository()
    private val useCase = CreateCustomIngredientUseCase(repository, TestDispatcherProvider(dispatcher))

    @Test
    fun `blank name is ValidationError`() = runTest(dispatcher) {
        val result = useCase.execute(validDraft().copy(name = "  "))

        assertEquals(CreateCustomIngredientResult.ValidationError, result)
        assertNull(repository.lastCreateIngredientDraft)
    }

    @Test
    fun `unknown serving unit is ValidationError without calling repository`() = runTest(dispatcher) {
        val result = useCase.execute(validDraft().copy(servingUnit = PortionUnit.UNKNOWN))

        assertEquals(CreateCustomIngredientResult.ValidationError, result)
        assertNull(repository.lastCreateIngredientDraft)
    }

    @Test
    fun `success returns created ingredient`() = runTest(dispatcher) {
        val draft = validDraft()

        val result = useCase.execute(draft)

        assertTrue(result is CreateCustomIngredientResult.Success)
        assertEquals(draft, repository.lastCreateIngredientDraft)
        assertEquals(
            FakeNutritionCatalogRepository.defaultIngredient(),
            (result as CreateCustomIngredientResult.Success).ingredient,
        )
    }

    @Test
    fun `ServerException maps to ServerError`() = runTest(dispatcher) {
        repository.createIngredientResult = Result.Error(ApiException.ServerException("boom"))

        val result = useCase.execute(validDraft())

        assertEquals(CreateCustomIngredientResult.ServerError("boom"), result)
    }

    private fun validDraft() = CustomIngredientDraft(
        name = "Oats",
        brand = null,
        servingSize = 100.0,
        servingUnit = PortionUnit.GRAM,
        calories = 380.0,
        protein = 12.0,
        carbs = 60.0,
        fat = 7.0,
    )
}
