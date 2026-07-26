package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.*
import com.agusstkd.goodlife.fake.FakeMealPlanRepository
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.fake.FakeNutritionCatalogRepository
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealDatePickerField
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealPlanWizardStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class CreateMealPlanViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private lateinit var mealRepo: FakeMealPlanRepository
    private lateinit var catalogRepo: FakeNutritionCatalogRepository
    private lateinit var nav: FakeNavigationController

    @Before fun setUp() { Dispatchers.setMain(dispatcher); mealRepo = FakeMealPlanRepository(); catalogRepo = FakeNutritionCatalogRepository(); nav = FakeNavigationController() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun vm() = CreateMealPlanViewModel(
        navigationController = nav,
        language = Spanish,
        createMealPlanUseCase = CreateMealPlanUseCase(mealRepo, TestDispatcherProvider(dispatcher)),
        createCustomMealUseCase = CreateCustomMealUseCase(catalogRepo, TestDispatcherProvider(dispatcher)),
        createCustomIngredientUseCase = CreateCustomIngredientUseCase(catalogRepo, TestDispatcherProvider(dispatcher)),
        searchMealUseCase = SearchMealsUseCase(catalogRepo, TestDispatcherProvider(dispatcher)),
        searchIngredientUseCase = SearchIngredientsUseCase(catalogRepo, TestDispatcherProvider(dispatcher)),
    )

    @Test fun `initial search loads catalog and selecting existing meal jumps to schedule`() = runTest(dispatcher) {
        catalogRepo.mealResult = Result.Success(PageResult(listOf(FakeNutritionCatalogRepository.defaultMeal()), 0, 20, 1, true))
        val vm = vm(); advanceUntilIdle()
        Assert.assertEquals(1, vm.uiState.value.mealCatalog.size)
        vm.onAction(CreateMealPlanUiAction.OnSelectMeal(FakeNutritionCatalogRepository.defaultMeal()))
        Assert.assertEquals(MealPlanWizardStep.SCHEDULE, vm.uiState.value.currentStep)
    }

    @Test fun `new meal requires name then loads ingredients and updates macros`() = runTest(dispatcher) {
        catalogRepo.ingredientResult = Result.Success(PageResult(listOf(FakeNutritionCatalogRepository.defaultIngredient()), 0, 20, 1, true))
        val vm = vm(); advanceUntilIdle()
        vm.onAction(CreateMealPlanUiAction.OnCreateNewMeal)
        vm.onAction(CreateMealPlanUiAction.OnNextStep)
        Assert.assertNotNull(vm.uiState.value.errorMessage)
        vm.onAction(CreateMealPlanUiAction.OnErrorDismissed)
        vm.onAction(CreateMealPlanUiAction.OnNewMealNameChange("Power bowl"))
        vm.onAction(CreateMealPlanUiAction.OnNewMealDescriptionChange("Lunch"))
        vm.onAction(CreateMealPlanUiAction.OnNewMealTypeChange(MealType.LUNCH))
        vm.onAction(CreateMealPlanUiAction.OnNextStep)
        advanceUntilIdle()
        Assert.assertEquals(MealPlanWizardStep.MEAL_INGREDIENTS, vm.uiState.value.currentStep)
        Assert.assertEquals(1, vm.uiState.value.ingredientCatalog.size)
        vm.onAction(CreateMealPlanUiAction.OnOpenQuantityBottomSheet(1L))
        Assert.assertTrue(vm.uiState.value.showQuantityBottomSheet)
        vm.onAction(CreateMealPlanUiAction.OnConfirmIngredientQuantity(1L, 200.0, "g"))
        Assert.assertEquals(260.0, vm.uiState.value.previewCalories, 0.01)
        vm.onAction(CreateMealPlanUiAction.OnRemoveIngredient(1L))
        Assert.assertEquals(0.0, vm.uiState.value.previewCalories, 0.01)
    }

    @Test fun `schedule toggles dates time days and submit existing meal successfully`() = runTest(dispatcher) {
        val vm = vm(); advanceUntilIdle()
        vm.onAction(CreateMealPlanUiAction.OnSelectMeal(FakeNutritionCatalogRepository.defaultMeal()))
        vm.onAction(CreateMealPlanUiAction.OnSubmit)
        advanceUntilIdle()
        Assert.assertNotNull(vm.uiState.value.errorMessage)
        vm.onAction(CreateMealPlanUiAction.OnErrorDismissed)
        vm.onAction(CreateMealPlanUiAction.OnMealTypeSelected(MealType.LUNCH))
        vm.onAction(CreateMealPlanUiAction.OnDayToggled(DayOfWeek.MONDAY))
        vm.onAction(CreateMealPlanUiAction.OnHasStartDateToggle)
        vm.onAction(CreateMealPlanUiAction.OnDatePickerOpen(MealDatePickerField.START_DATE))
        vm.onAction(CreateMealPlanUiAction.OnDateSelected(MealDatePickerField.START_DATE, LocalDate(2026, 7, 26)))
        vm.onAction(CreateMealPlanUiAction.OnHasEndDateToggle)
        vm.onAction(CreateMealPlanUiAction.OnDatePickerOpen(MealDatePickerField.END_DATE))
        vm.onAction(CreateMealPlanUiAction.OnDateSelected(MealDatePickerField.END_DATE, LocalDate(2026, 8, 26)))
        vm.onAction(CreateMealPlanUiAction.OnHasTimeToggle)
        vm.onAction(CreateMealPlanUiAction.OnTimePickerOpen)
        vm.onAction(CreateMealPlanUiAction.OnTimeSelected(LocalTime(13, 30)))
        vm.onAction(CreateMealPlanUiAction.OnSubmit)
        advanceUntilIdle()
        Assert.assertTrue(vm.uiState.value.isSuccess)
        Assert.assertEquals(1, mealRepo.createCallCount)
        vm.onAction(CreateMealPlanUiAction.OnSuccessAnimationFinished)
        Assert.assertTrue(nav.navigatedActions.isNotEmpty())
    }

    @Test fun `previous dismiss picker dismiss and load more branches update state or navigate`() = runTest(dispatcher) {
        catalogRepo.mealResult = Result.Success(PageResult(listOf(FakeNutritionCatalogRepository.defaultMeal()), 0, 20, 40, false))
        catalogRepo.ingredientResult = Result.Success(PageResult(listOf(FakeNutritionCatalogRepository.defaultIngredient()), 0, 20, 40, false))
        val vm = vm(); advanceUntilIdle()
        vm.onAction(CreateMealPlanUiAction.OnLoadMoreMeals); advanceUntilIdle()
        vm.onAction(CreateMealPlanUiAction.OnDismiss)
        vm.onAction(CreateMealPlanUiAction.OnDatePickerOpen(MealDatePickerField.START_DATE))
        vm.onAction(CreateMealPlanUiAction.OnDatePickerDismiss)
        vm.onAction(CreateMealPlanUiAction.OnTimePickerOpen)
        vm.onAction(CreateMealPlanUiAction.OnTimePickerDismiss)
        Assert.assertTrue(nav.navigatedActions.isNotEmpty())
        Assert.assertFalse(vm.uiState.value.showTimePicker)
        Assert.assertNull(vm.uiState.value.activeDatePickerField)
    }

    @Test fun `custom ingredient success adds to catalog and search debounce reloads`() = runTest(dispatcher) {
        val vm = vm(); advanceUntilIdle()
        vm.onAction(CreateMealPlanUiAction.OnShowCreateIngredientDialog)
        Assert.assertTrue(vm.uiState.value.showCreateIngredientDialog)
        vm.onAction(CreateMealPlanUiAction.OnCreateCustomIngredient("Oats", null, 100.0, "g", 380.0, 12.0, 60.0, 7.0))
        advanceUntilIdle()
        Assert.assertFalse(vm.uiState.value.showCreateIngredientDialog)
        Assert.assertEquals(1, vm.uiState.value.ingredientCatalog.size)
        vm.onAction(CreateMealPlanUiAction.OnMealSearchQueryChange("bowl"))
        vm.onAction(CreateMealPlanUiAction.OnIngredientSearchQueryChange("rice"))
        advanceTimeBy(350)
        advanceUntilIdle()
        Assert.assertTrue(catalogRepo.searchMealsCallCount > 0)
        Assert.assertTrue(catalogRepo.searchIngredientsCallCount > 0)
    }
}
