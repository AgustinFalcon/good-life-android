package com.agusstkd.goodlife.presentation.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.bottom.BottomNavigationComponent
import com.agusstkd.goodlife.presentation.components.bottom.BottomNavigationParams
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.bottom.model.getDefaultBottomNavItems
import com.agusstkd.goodlife.presentation.components.modal.AddActionModalComponent
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType
import com.agusstkd.goodlife.presentation.navigation.route.TabGraphRoute
import com.agusstkd.goodlife.presentation.navigation.route.addTabNavGraph
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiAction
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiState
import com.agusstkd.goodlife.presentation.theme.EmeraldGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.InfoBlue
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import com.agusstkd.goodlife.presentation.theme.WarningOrange
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MainScaffoldScreen(
    uiState: MainScaffoldUiState,
    onAction: (MainScaffoldUiAction) -> Unit
) {
    val tabNavController = rememberNavController()
    MainScaffoldChrome(
        uiState = uiState,
        onAction = onAction,
        onTabClick = { selected ->
            onAction(MainScaffoldUiAction.OnTabSelected(selected))
            tabNavController.navigateToTabGraph(selected)
        },
        content = { paddingValues ->
            TabNavHost(
                navController = tabNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    )
}

@Composable
internal fun MainScaffoldChrome(
    uiState: MainScaffoldUiState,
    onAction: (MainScaffoldUiAction) -> Unit,
    onTabClick: (BottomMenuOption) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomNavigationComponent(
                    params = BottomNavigationParams(
                        items = uiState.bottomNavItems,
                        selectedTab = uiState.selectedTab,
                        isFabRotated = uiState.isModalOpen,
                        fabContentDescription = uiState.fabContentDescription
                    ),
                    onTabClick = onTabClick,
                    onFabClick = { onAction(MainScaffoldUiAction.OnFabClick) }
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }

        AddActionModalComponent(
            isVisible = uiState.isModalOpen,
            dateFormatted = uiState.currentDateFormatted,
            quickActions = uiState.quickActions,
            mealOptions = uiState.mealOptions,
            onDismiss = { onAction(MainScaffoldUiAction.OnModalDismiss) },
            onQuickActionClick = { onAction(MainScaffoldUiAction.OnQuickActionClick(it)) },
            onMealOptionClick = { onAction(MainScaffoldUiAction.OnMealOptionClick(it)) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TabNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TabGraphRoute.DailyGraph,
        modifier = modifier
    ) {
        addTabNavGraph(navController)
    }
}

private fun NavHostController.navigateToTabGraph(tab: BottomMenuOption) {
    val graphRoute = when (tab) {
        BottomMenuOption.HOME -> TabGraphRoute.DailyGraph
        BottomMenuOption.EXERCISES -> TabGraphRoute.WorkoutsGraph
        BottomMenuOption.FOOD -> TabGraphRoute.MealsGraph
        BottomMenuOption.SETTINGS -> TabGraphRoute.SettingsGraph
    }

    navigate(graphRoute) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) { saveState = true }
    }
}

private fun previewMainScaffoldUiState(
    selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    isModalOpen: Boolean = false,
): MainScaffoldUiState {
    val t = Spanish.mainScaffoldTexts
    return MainScaffoldUiState(
        bottomNavItems = getDefaultBottomNavItems(t, Spanish.accessibilityTexts),
        selectedTab = selectedTab,
        isModalOpen = isModalOpen,
        currentDateFormatted = "Martes 27 oct",
        quickActions = listOf(
            QuickActionItem(QuickActionType.TASK, t.task, Icons.Default.CheckBox, SuccessGreen),
            QuickActionItem(QuickActionType.HABIT, t.habit, Icons.Default.Repeat, InfoBlue),
            QuickActionItem(QuickActionType.WORKOUT, t.workout, Icons.Default.FitnessCenter, EmeraldGreen),
            QuickActionItem(QuickActionType.MEAL, t.meal, Icons.Default.Restaurant, WarningOrange),
            QuickActionItem(QuickActionType.OTHER, t.others, Icons.Default.MoreHoriz, WarningOrange),
        ).toImmutableList(),
        mealOptions = listOf(
            MealOptionItem(null, t.dailySummary, Icons.Default.WbSunny),
            MealOptionItem(MealType.BREAKFAST, t.breakfast, Icons.Default.LocalDining),
            MealOptionItem(MealType.LUNCH, t.lunch, Icons.Default.Dining),
            MealOptionItem(MealType.DINNER, t.dinner, Icons.Default.Dining),
            MealOptionItem(MealType.SNACK, t.snack, Icons.Default.LocalDining),
            MealOptionItem(MealType.AFTERNOON_SNACK, t.afternoonSnack, Icons.Default.LocalDining),
        ).toImmutableList(),
        fabContentDescription = Spanish.accessibilityTexts.add,
    )
}

@Preview(showBackground = true, name = "MainScaffold - Daily")
@Composable
private fun MainScaffoldDailyPreview() {
    GoodLifeTheme {
        MainScaffoldChrome(
            uiState = previewMainScaffoldUiState(),
            onAction = {},
            onTabClick = {},
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Daily", style = MaterialTheme.typography.titleLarge)
                }
            },
        )
    }
}

@Preview(showBackground = true, name = "MainScaffold - modal abierto")
@Composable
private fun MainScaffoldModalPreview() {
    GoodLifeTheme {
        MainScaffoldChrome(
            uiState = previewMainScaffoldUiState(isModalOpen = true),
            onAction = {},
            onTabClick = {},
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Daily", style = MaterialTheme.typography.titleLarge)
                }
            },
        )
    }
}
