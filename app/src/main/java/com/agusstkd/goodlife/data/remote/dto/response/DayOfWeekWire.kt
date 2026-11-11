package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.datetime.DayOfWeek

/**
 * Traduce ints ISO-8601 de wire a [DayOfWeek] sin tumbar el parseo del DTO.
 * Un valor fuera de 1–7 se omite.
 */
internal fun List<Int>?.toDayOfWeekSet(): Set<DayOfWeek> =
    orEmpty().mapNotNull { iso -> iso.toDayOfWeekOrNull() }.toSet()

/**
 * Traduce un int ISO-8601 (1=lunes … 7=domingo) a [DayOfWeek].
 * Un valor nulo o fuera de 1–7 se omite.
 */
internal fun Int?.toDayOfWeekOrNull(): DayOfWeek? =
    this?.let { iso -> runCatching { DayOfWeek(iso) }.getOrNull() }
