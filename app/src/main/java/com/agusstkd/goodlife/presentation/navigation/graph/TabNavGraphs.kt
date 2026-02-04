package com.agusstkd.goodlife.presentation.navigation.graph

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.agusstkd.goodlife.presentation.navigation.route.TabGraphRoute

fun NavGraphBuilder.addDailyGraph() {
    composable<TabGraphRoute.DailyGraph> {
        DailyScreen()
    }
}

fun NavGraphBuilder.addWorkoutsGraph() {
    composable<TabGraphRoute.WorkoutsGraph> {
        WorkoutsScreen()
    }
}

fun NavGraphBuilder.addMealsGraph() {
    composable<TabGraphRoute.MealsGraph> {
        MealsScreen()
    }
}

fun NavGraphBuilder.addSettingsGraph() {
    composable<TabGraphRoute.SettingsGraph> {
        SettingsScreen()
    }
}

@Composable
private fun DailyScreen() {
    Text("Daily Screen")
}

@Composable
private fun WorkoutsScreen() {
    Text("Workouts Screen")
}

@Composable
private fun MealsScreen() {
    Text("Meals Screen")
}

@Composable
private fun SettingsScreen() {
    Text("Settings Screen")
}
