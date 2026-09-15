package com.agusstkd.goodlife.presentation.navigation.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.DailyDetailScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.meals.MealsScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.profile.ProfileScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.settings.SettingsScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.workout.WorkoutsScreenOwner
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.WorkoutDetailScreenOwner
import kotlinx.datetime.LocalDate

/**
 * Defines the nested graph of every tab. Each tab retains its own back stack.
 */
fun NavGraphBuilder.addTabNavGraph(navController: NavHostController) {
    addDailyTabGraph(
        navController = navController,
        dailyList = { onNavigateToDetail -> DailyScreenOwner(onNavigateToDetail) },
        dailyDetail = { itemId, dateIso, onNavigateUp ->
            DailyDetailScreenOwner(itemId, dateIso, onNavigateUp)
        },
    )

    navigation<TabGraphRoute.WorkoutsGraph>(startDestination = TabRoute.Workouts) {
        composable<TabRoute.Workouts> {
            WorkoutsScreenOwner(
                onNavigateToWorkoutDetail = { workoutId ->
                    navController.navigate(TabRoute.WorkoutDetail(workoutId))
                },
            )
        }
        composable<TabRoute.WorkoutDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TabRoute.WorkoutDetail>()
            WorkoutDetailScreenOwner(
                workoutId = route.workoutId,
                onNavigateUp = navController::navigateUp,
            )
        }
    }

    addMealsTabGraph(
        navController = navController,
        mealsList = { onNavigateToDetail -> MealsScreenOwner(onNavigateToDetail) },
        mealDetail = { planId, dateIso, onNavigateUp ->
            MealDetailScreenOwner(planId, dateIso, onNavigateUp)
        },
    )

    navigation<TabGraphRoute.SettingsGraph>(startDestination = TabRoute.Settings) {
        composable<TabRoute.Settings> {
            SettingsScreenOwner(
                onNavigateToProfile = { navController.navigate(TabRoute.Profile) },
            )
        }
        composable<TabRoute.Profile> { ProfileScreenOwner() }
    }
}

/**
 * Typed Daily destinations extracted so production wiring can be exercised
 * with deterministic content in instrumentation tests without a live backend.
 */
internal fun NavGraphBuilder.addDailyTabGraph(
    navController: NavHostController,
    dailyList: @Composable ((Long, LocalDate) -> Unit) -> Unit,
    dailyDetail: @Composable (Long, String, () -> Unit) -> Unit,
) {
    navigation<TabGraphRoute.DailyGraph>(startDestination = TabRoute.Daily) {
        composable<TabRoute.Daily> {
            dailyList { itemId, date ->
                navController.navigate(TabRoute.DailyDetail(itemId, date.toString()))
            }
        }
        composable<TabRoute.DailyDetail> { entry ->
            val route = entry.toRoute<TabRoute.DailyDetail>()
            dailyDetail(route.itemId, route.dateIso, navController::navigateUp)
        }
    }
}

/** Same testable typed-route boundary as Daily, for the Meals tab graph. */
internal fun NavGraphBuilder.addMealsTabGraph(
    navController: NavHostController,
    mealsList: @Composable ((Long, String) -> Unit) -> Unit,
    mealDetail: @Composable (Long, String, () -> Unit) -> Unit,
) {
    navigation<TabGraphRoute.MealsGraph>(startDestination = TabRoute.Meals) {
        composable<TabRoute.Meals> {
            mealsList { planId, dateIso ->
                navController.navigate(TabRoute.MealDetail(planId, dateIso))
            }
        }
        composable<TabRoute.MealDetail> { entry ->
            val route = entry.toRoute<TabRoute.MealDetail>()
            mealDetail(route.planId, route.dateIso, navController::navigateUp)
        }
    }
}