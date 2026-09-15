package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import kotlinx.datetime.LocalDate

sealed interface DailyNavigationEffect {
    data class NavigateToDailyDetail(
        val itemId: Long,
        val date: LocalDate,
    ) : DailyNavigationEffect
}