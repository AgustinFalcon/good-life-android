package com.agusstkd.goodlife

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.navigation.route.TabGraphRoute
import com.agusstkd.goodlife.presentation.navigation.route.addDailyTabGraph
import com.agusstkd.goodlife.presentation.navigation.route.addMealsTabGraph
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyScreen
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.DailyDetailMessage
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.DailyDetailScreen
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailTexts
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemHighlight
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import com.agusstkd.goodlife.presentation.screen.tabs.meals.MealsScreen
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailImage
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailMessage
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailScreen
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailTexts
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealPlanUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailScreensInstrumentedTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun dailyDetail_rendersFieldsAndInvokesAccessibleBackControl() {
        val backPressed = AtomicBoolean(false)
        composeRule.setContent {
            MaterialTheme {
                DailyDetailScreen(
                    state = DailyDetailUiState.Content("Caminar", "Hábito", "Completado", "08:00", "Paseo diario"),
                    texts = DailyDetailTexts.from(Spanish),
                    onNavigateUp = { backPressed.set(true) },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Volver").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.runOnIdle { assertTrue(backPressed.get()) }
        composeRule.onNodeWithText("Caminar").assertIsDisplayed()
        composeRule.onNodeWithText("Paseo diario").assertIsDisplayed()
    }

    @Test
    fun dailyDetailMessages_renderNotFoundInvalidRouteAndRetryWithAccessibleBack() {
        val texts = DailyDetailTexts.from(Spanish)
        val backPressed = AtomicBoolean(false)
        val retryPressed = AtomicBoolean(false)
        lateinit var mode: MutableIntState
        composeRule.setContent {
            mode = remember { mutableIntStateOf(0) }
            MaterialTheme {
                when (mode.intValue) {
                    0 -> DailyDetailMessage(texts.notFound, texts, { backPressed.set(true) })
                    1 -> DailyDetailMessage(texts.invalidRoute, texts, {})
                    else -> DailyDetailMessage(
                        message = "Error de conexión",
                        texts = texts,
                        onNavigateUp = {},
                        actionLabel = texts.retry,
                        onAction = { retryPressed.set(true) },
                    )
                }
            }
        }

        composeRule.onNodeWithText(texts.notFound).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(texts.back).performClick()
        composeRule.runOnIdle { assertTrue(backPressed.get()); mode.intValue = 1 }
        composeRule.onNodeWithText(texts.invalidRoute).assertIsDisplayed()
        composeRule.runOnIdle { mode.intValue = 2 }
        composeRule.onNodeWithText(texts.retry).performClick()
        composeRule.runOnIdle { assertTrue(retryPressed.get()) }
    }
    @Test
    fun dailyTypedGraph_preservesScrolledListPositionAfterDetailBack() {
        lateinit var listState: LazyListState
        val dailyItems = (1..20).map { index ->
            DailyItemUiModel(
                id = index.toLong(),
                type = DailyItemType.HABIT,
                typeLabel = "Hábito",
                title = "Caminar $index",
                description = null,
                scheduledTime = null,
                status = DailyItemStatus.PENDING,
                highlight = DailyItemHighlight.NONE,
            )
        }.toImmutableList()

        composeRule.setContent {
            val navController = rememberNavController()
            listState = rememberLazyListState()
            MaterialTheme {
                NavHost(navController, startDestination = TabGraphRoute.DailyGraph) {
                    addDailyTabGraph(
                        navController = navController,
                        dailyList = { onNavigateToDetail ->
                            DailyScreen(
                                uiState = DailyUiState.Success(
                                    date = LocalDate(2026, 9, 14),
                                    dayNumber = 14,
                                    headerText = "Hoy",
                                    monthYear = "Septiembre 2026",
                                    showFullDate = false,
                                    completionRate = 0.0,
                                    items = dailyItems,
                                ),
                                dailyTexts = Spanish.dailyTexts,
                                dateHeaderParams = DateHeaderParams(14, "Hoy", null),
                                listState = listState,
                                onAction = { action ->
                                    if (action is DailyUiAction.OnItemClick) {
                                        onNavigateToDetail(action.itemId, LocalDate(2026, 9, 14))
                                    }
                                },
                            )
                        },
                        dailyDetail = { itemId, dateIso, onNavigateUp ->
                            DailyDetailScreen(
                                state = DailyDetailUiState.Content("Caminar $itemId", "Hábito", "Pendiente", null, dateIso),
                                texts = DailyDetailTexts.from(Spanish),
                                onNavigateUp = onNavigateUp,
                            )
                        },
                    )
                }
            }
        }

        composeRule.runOnIdle { runBlocking { listState.scrollToItem(17) } }
        composeRule.waitForIdle()
        var indexBeforeNavigation = -1
        var offsetBeforeNavigation = -1
        composeRule.runOnIdle {
            indexBeforeNavigation = listState.firstVisibleItemIndex
            offsetBeforeNavigation = listState.firstVisibleItemScrollOffset
            assertTrue(indexBeforeNavigation > 0)
        }
        composeRule.onNodeWithText("Caminar 15").performClick()
        composeRule.onNodeWithText("Detalle diario").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.onNodeWithText("Caminar 15").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(indexBeforeNavigation, listState.firstVisibleItemIndex)
            assertEquals(offsetBeforeNavigation, listState.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun mealsTypedGraph_preservesScrolledListPositionAfterDetailBack() {
        lateinit var listState: LazyListState
        val plans = (1..20).map { index ->
            MealPlanUiModel(
                id = index.toLong(),
                name = "Plan $index",
                mealTypeLabel = "Desayuno",
                scheduledTimeLabel = null,
                mealName = null,
                imageUrl = null,
                totalCalories = 400.0,
                totalProtein = 20.0,
                totalCarbs = 45.0,
                totalFat = 10.0,
            )
        }

        composeRule.setContent {
            val navController = rememberNavController()
            listState = rememberLazyListState()
            MaterialTheme {
                NavHost(navController, startDestination = TabGraphRoute.MealsGraph) {
                    addMealsTabGraph(
                        navController = navController,
                        mealsList = { onNavigateToDetail ->
                            MealsScreen(
                                uiState = MealsUiState.Success(
                                    date = LocalDate(2026, 9, 14),
                                    dayNumber = 14,
                                    headerText = "Hoy",
                                    monthYear = "Septiembre 2026",
                                    showFullDate = false,
                                    mealPlans = plans,
                                    totalCalories = 8000.0,
                                    totalProtein = 400.0,
                                    totalCarbs = 900.0,
                                    totalFat = 200.0,
                                ),
                                texts = Spanish.tabDetailTexts,
                                dateHeaderParams = DateHeaderParams(14, "Hoy", null),
                                listState = listState,
                                onAction = { action ->
                                    if (action is MealsUiAction.OnMealPlanClick) {
                                        onNavigateToDetail(action.planId, "2026-09-14")
                                    }
                                },
                            )
                        },
                        mealDetail = { planId, dateIso, onNavigateUp ->
                            MealDetailScreen(
                                state = MealDetailUiState.Content("Plan $planId", "Desayuno", null, null, null, 400.0, 20.0, 45.0, 10.0, true),
                                texts = spanishMealTexts(),
                                onNavigateUp = onNavigateUp,
                            )
                        },
                    )
                }
            }
        }

        composeRule.runOnIdle { runBlocking { listState.scrollToItem(17) } }
        composeRule.waitForIdle()
        var indexBeforeNavigation = -1
        var offsetBeforeNavigation = -1
        composeRule.runOnIdle {
            indexBeforeNavigation = listState.firstVisibleItemIndex
            offsetBeforeNavigation = listState.firstVisibleItemScrollOffset
            assertTrue(indexBeforeNavigation > 0)
        }
        composeRule.onNodeWithText("Plan 15").performClick()
        composeRule.onNodeWithText("Plan 15").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.onNodeWithText("Plan 15").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(indexBeforeNavigation, listState.firstVisibleItemIndex)
            assertEquals(offsetBeforeNavigation, listState.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun mealDetailMessages_renderNotFoundInvalidRouteAndRetryWithBack() {
        val texts = spanishMealTexts()
        val backPressed = AtomicBoolean(false)
        val retryPressed = AtomicBoolean(false)
        lateinit var mode: MutableIntState
        composeRule.setContent {
            mode = remember { mutableIntStateOf(0) }
            MaterialTheme {
                when (mode.intValue) {
                    0 -> MealDetailMessage(texts.notFound, texts.back, { backPressed.set(true) })
                    1 -> MealDetailMessage(texts.invalidRoute, texts.back, {})
                    else -> MealDetailMessage(
                        message = "Error de conexión",
                        backLabel = texts.back,
                        onNavigateUp = {},
                        primaryActionLabel = texts.retry,
                        onPrimaryAction = { retryPressed.set(true) },
                    )
                }
            }
        }

        composeRule.onNodeWithText(texts.notFound).assertIsDisplayed()
        composeRule.onNodeWithText(texts.back).performClick()
        composeRule.runOnIdle { assertTrue(backPressed.get()); mode.intValue = 1 }
        composeRule.onNodeWithText(texts.invalidRoute).assertIsDisplayed()
        composeRule.runOnIdle { mode.intValue = 2 }
        composeRule.onNodeWithText(texts.retry).performClick()
        composeRule.runOnIdle { assertTrue(retryPressed.get()) }
    }
    @Test
    fun mealDetail_rendersStatusAndAccessibleImageFallback() {
        composeRule.setContent {
            MaterialTheme {
                MealDetailScreen(
                    state = MealDetailUiState.Content("Plan desayuno", "Desayuno", "08:00", "Avena", null, 420.0, 22.0, 55.0, 12.0, true),
                    texts = spanishMealTexts(),
                    onNavigateUp = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Imagen de comida no disponible").assertIsDisplayed()
        composeRule.onNodeWithText("Activo").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Volver").assertIsDisplayed()
    }

    @Test
    fun mealImage_errorStateUsesAccessibleFallback() {
        composeRule.setContent {
            MaterialTheme {
                MealDetailImage("https://example.invalid/meal.png", "Imagen de comida no disponible", forceFallback = true)
            }
        }
        composeRule.onNodeWithContentDescription("Imagen de comida no disponible").assertIsDisplayed()
    }

    private fun spanishMealTexts() = Spanish.tabDetailTexts.let {
        MealDetailTexts(
            it.mealTitle, it.back, it.retry, it.loading, it.mealNotFound, it.invalidRoute,
            it.mealLabel, it.scheduleLabel, it.proteinLabel, it.carbsLabel, it.fatLabel,
            it.statusLabel, it.active, it.inactive, it.mealImageUnavailable, it.calorieUnit, it.gramUnit, it.decimalSeparator,
        )
    }
}