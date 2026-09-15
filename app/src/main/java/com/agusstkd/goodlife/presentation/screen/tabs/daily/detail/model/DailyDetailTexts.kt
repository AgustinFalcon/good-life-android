package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.English
import com.agusstkd.goodlife.core.datetime.language.Portuguese
import com.agusstkd.goodlife.core.datetime.language.Spanish

/** Localized copy used exclusively by the Daily read-only detail. */
data class DailyDetailTexts(
    val title: String,
    val back: String,
    val loading: String,
    val notFound: String,
    val invalidRoute: String,
    val retry: String,
    val typeLabel: String,
    val statusLabel: String,
    val scheduleLabel: String,
    val descriptionLabel: String,
    val pending: String,
    val inProgress: String,
    val completed: String,
    val skipped: String,
) {
    companion object {
        fun from(language: AppLanguage): DailyDetailTexts = language.tabDetailTexts.let {
            DailyDetailTexts(it.dailyTitle, it.back, it.loading, it.dailyNotFound, it.invalidRoute, it.retry,
                it.typeLabel, it.statusLabel, it.scheduleLabel, it.descriptionLabel, it.pending, it.inProgress,
                it.completed, it.skipped)
        }
    }
}
