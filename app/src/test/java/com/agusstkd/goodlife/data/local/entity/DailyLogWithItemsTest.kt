package com.agusstkd.goodlife.data.local.entity

import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DailyLogWithItemsTest {

    @Test
    fun `unknown type is omitted from domain`() {
        val entity = item(type = "NOTE", status = "PENDING")
        assertNull(entity.toDomain())
    }

    @Test
    fun `unknown status is omitted from domain`() {
        val entity = item(type = "TASK", status = "CANCELLED")
        assertNull(entity.toDomain())
    }

    @Test
    fun `known item maps and unknown siblings are dropped`() {
        val log = DailyLogWithItems(
            log = DailyLogEntity(id = 1L, date = "2026-03-09", completionRate = 0.5),
            items = listOf(
                item(id = 1L, type = "TASK", status = "PENDING"),
                item(id = 2L, type = "NOTE", status = "PENDING"),
                item(id = 3L, type = "HABIT", status = "CANCELLED"),
            ),
        ).toDomain()

        assertEquals(1, log.items.size)
        assertEquals(1L, log.items.single().id)
        assertEquals(DailyItemType.TASK, log.items.single().type)
        assertEquals(DailyItemStatus.PENDING, log.items.single().status)
    }

    private fun item(
        id: Long = 1L,
        type: String,
        status: String,
    ) = DailyItemEntity(
        id = id,
        dailyLogId = 1L,
        type = type,
        referenceId = 10L,
        scheduledTime = null,
        status = status,
        title = "Item",
        description = null,
    )
}
