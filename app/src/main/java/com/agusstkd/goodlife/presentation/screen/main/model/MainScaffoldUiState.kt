package com.agusstkd.goodlife.presentation.screen.main.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomNavItemModel
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem

//falta kdoc
@Stable
data class MainScaffoldUiState(
    val bottomNavItems: List<BottomNavItemModel> = emptyList(),
    val selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    val isModalOpen: Boolean = false,
    val currentDateFormatted: String = "",
    val quickActions: List<QuickActionItem> = emptyList(),
    val mealOptions: List<MealOptionItem> = emptyList(),
    val fabContentDescription: String = ""
)
