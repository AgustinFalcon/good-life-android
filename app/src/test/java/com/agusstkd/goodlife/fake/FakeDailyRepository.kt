package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.datetime.LocalDate

class FakeDailyRepository : DailyRepository {

    var getDailyLogResult: Result<DailyLog> = Result.Success(DEFAULT_DAILY_LOG)
    var updateItemStatusResult: Result<DailyLog> = Result.Success(DEFAULT_DAILY_LOG)

    var getDailyLogCallCount = 0
        private set
    var updateItemStatusCallCount = 0
        private set
    var lastRequestedDate: LocalDate? = null
        private set

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        getDailyLogCallCount++
        lastRequestedDate = date
        return getDailyLogResult
    }

    override suspend fun updateItemStatus(itemId: Long, status: DailyItemStatus): Result<DailyLog> {
        updateItemStatusCallCount++
        return updateItemStatusResult
    }

    companion object {
        val DEFAULT_DAILY_LOG = DailyLog(
            id = 1L,
            date = LocalDate(2026, 3, 9),
            completionRate = 0.5,
            items = emptyList()
        )
    }
}
