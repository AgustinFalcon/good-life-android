package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable

// falta kdoc
@Stable
sealed interface DiaryUiAction {
    data object OnPreviousDay : DiaryUiAction
    data object OnNextDay : DiaryUiAction
}
