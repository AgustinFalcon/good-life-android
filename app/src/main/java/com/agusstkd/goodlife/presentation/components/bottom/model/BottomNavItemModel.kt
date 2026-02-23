package com.agusstkd.goodlife.presentation.components.bottom.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.agusstkd.goodlife.core.datetime.language.AccessibilityTexts
import com.agusstkd.goodlife.core.datetime.language.MainScaffoldTexts

@Stable
data class BottomNavItemModel(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val option: BottomMenuOption,
    val contentDescription: String
)

fun getDefaultBottomNavItems(
    scaffoldTexts: MainScaffoldTexts,
    accessibilityTexts: AccessibilityTexts
): List<BottomNavItemModel> = listOf(
    BottomNavItemModel(
        label = scaffoldTexts.tabDaily,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        option = BottomMenuOption.HOME,
        contentDescription = accessibilityTexts.goToDaily
    ),
    BottomNavItemModel(
        label = scaffoldTexts.tabWorkouts,
        icon = Icons.Outlined.FitnessCenter,
        selectedIcon = Icons.Filled.FitnessCenter,
        option = BottomMenuOption.EXERCISES,
        contentDescription = accessibilityTexts.goToWorkouts
    ),
    BottomNavItemModel(
        label = scaffoldTexts.tabFood,
        icon = Icons.Outlined.Restaurant,
        selectedIcon = Icons.Filled.Restaurant,
        option = BottomMenuOption.FOOD,
        contentDescription = accessibilityTexts.goToMeals
    ),
    BottomNavItemModel(
        label = scaffoldTexts.tabMore,
        icon = Icons.Outlined.MoreHoriz,
        selectedIcon = Icons.Filled.MoreHoriz,
        option = BottomMenuOption.SETTINGS,
        contentDescription = accessibilityTexts.goToMore
    )
)
