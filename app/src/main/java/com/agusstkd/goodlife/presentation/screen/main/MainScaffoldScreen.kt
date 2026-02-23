package com.agusstkd.goodlife.presentation.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.agusstkd.goodlife.presentation.components.bottom.BottomNavigationComponent
import com.agusstkd.goodlife.presentation.components.bottom.BottomNavigationParams
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.modal.AddActionModalComponent
import com.agusstkd.goodlife.presentation.navigation.route.TabGraphRoute
import com.agusstkd.goodlife.presentation.navigation.route.addTabNavGraph
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiAction
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiState


@Composable
fun MainScaffoldScreen(
    uiState: MainScaffoldUiState,
    onAction: (MainScaffoldUiAction) -> Unit
) {
    MainScaffoldContent(
        uiState = uiState,
        onAction = onAction
    )
}

@Composable
private fun MainScaffoldContent(
    uiState: MainScaffoldUiState,
    onAction: (MainScaffoldUiAction) -> Unit
) {
    val tabNavController = rememberNavController()

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
                    onTabClick = { selected ->
                        onAction(MainScaffoldUiAction.OnTabSelected(selected))
                        tabNavController.navigateToTabGraph(selected)
                    },
                    onFabClick = { onAction(MainScaffoldUiAction.OnFabClick) }
                )
            }
        ) { paddingValues ->
            TabNavHost(
                navController = tabNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
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
        addTabNavGraph()
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