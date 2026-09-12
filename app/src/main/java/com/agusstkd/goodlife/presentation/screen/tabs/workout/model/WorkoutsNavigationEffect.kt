package com.agusstkd.goodlife.presentation.screen.tabs.workout.model

/**
 * One-shot navigation requests owned by the Workouts tab.
 *
 * The Workouts owner consumes these effects and delegates to the tab-local
 * navigation graph. They must not be sent through the application-level
 * navigation controller because [com.agusstkd.goodlife.presentation.navigation.route.TabRoute] destinations live in the nested tab graph.
 */
sealed interface WorkoutsNavigationEffect {
    data class NavigateToWorkoutDetail(val workoutId: Long) : WorkoutsNavigationEffect
}
