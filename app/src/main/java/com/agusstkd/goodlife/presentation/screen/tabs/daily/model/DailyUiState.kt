package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

// falta kdoc
@Stable
data class DiaryUiState(
    val date: LocalDate
)
