package com.agusstkd.goodlife.presentation.navigation.route

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.meals.MealsScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.profile.ProfileScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.settings.SettingsScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.workout.WorkoutsScreenOwner

/**
 * Define los grafos de navegación de cada tab.
 *
 * Cada tab tiene su propio grafo anidado con navigation().
 * Esto permite mantener el estado de navegación independiente por tab.
 *
 * @param navController Controlador de navegación del tab NavHost.
 *   Se usa para navegar entre pantallas dentro de un mismo tab graph.
 */
fun NavGraphBuilder.addTabNavGraph(navController: NavHostController) {

    // ═══════════════════════════════════════════════════════════════════
    // HOME TAB (Daily)
    // ═══════════════════════════════════════════════════════════════════
    navigation<TabGraphRoute.DailyGraph>(startDestination = TabRoute.Daily) {
        composable<TabRoute.Daily> {
            DailyScreenOwner()
        }
        composable<TabRoute.DailyDetail> {
            // TODO: TaskDetailScreen
            PlaceholderScreen("Detalle de tarea")
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // WORKOUTS TAB
    // ═══════════════════════════════════════════════════════════════════
    navigation<TabGraphRoute.WorkoutsGraph>(startDestination = TabRoute.Workouts) {
        composable<TabRoute.Workouts> {
            WorkoutsScreenOwner()
        }
        composable<TabRoute.WorkoutDetail> {
            // TODO: WorkoutDetailScreen
            PlaceholderScreen("Detalle workout")
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MEALS TAB
    // ═══════════════════════════════════════════════════════════════════
    navigation<TabGraphRoute.MealsGraph>(startDestination = TabRoute.Meals) {
        composable<TabRoute.Meals> {
            MealsScreenOwner()
        }
        composable<TabRoute.MealDetail> {
            // TODO: MealDetailScreen
            PlaceholderScreen("Detalle comida")
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SETTINGS TAB
    // ═══════════════════════════════════════════════════════════════════
    navigation<TabGraphRoute.SettingsGraph>(startDestination = TabRoute.Settings) {
        composable<TabRoute.Settings> {
            SettingsScreenOwner(
                onNavigateToProfile = { navController.navigate(TabRoute.Profile) }
            )
        }
        composable<TabRoute.Profile> {
            ProfileScreenOwner()
        }
    }
}

/**
 * Placeholder temporal para pantallas pendientes.
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Text(
        text = "Pantalla: $name",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
