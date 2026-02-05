package com.agusstkd.goodlife.presentation.screen.main.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType

//falta kdoc
@Stable
sealed interface MainScaffoldUiAction {
    data class OnTabSelected(val tab: BottomMenuOption) : MainScaffoldUiAction
    data object OnFabClick : MainScaffoldUiAction
    data object OnModalDismiss : MainScaffoldUiAction
    data class OnQuickActionClick(val action: QuickActionType) : MainScaffoldUiAction
    data class OnMealOptionClick(val mealType: MealType?) : MainScaffoldUiAction
}
