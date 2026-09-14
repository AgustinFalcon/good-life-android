package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/** Observable states for the read-only Daily detail route. */
@Stable
sealed interface DailyDetailUiState {
    @Immutable data object Loading : DailyDetailUiState

    @Immutable
    data class Content(
        val title: String,
        val type: String,
        val status: String,
        val scheduledTime: String?,
        val description: String?,
    ) : DailyDetailUiState

    @Immutable data object NotFound : DailyDetailUiState
    @Immutable data object InvalidRoute : DailyDetailUiState
    @Immutable data class Error(val message: String) : DailyDetailUiState
}
