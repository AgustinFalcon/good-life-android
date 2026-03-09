package com.agusstkd.goodlife.presentation.screen.main.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomNavItemModel
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class MainScaffoldUiState(
    val bottomNavItems: ImmutableList<BottomNavItemModel> = persistentListOf(),
    val selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    val isModalOpen: Boolean = false,
    val currentDateFormatted: String = "",
    val quickActions: ImmutableList<QuickActionItem> = persistentListOf(),
    val mealOptions: ImmutableList<MealOptionItem> = persistentListOf(),
    val fabContentDescription: String = ""
)
