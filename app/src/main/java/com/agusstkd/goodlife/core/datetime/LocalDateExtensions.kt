@file:OptIn(ExperimentalTime::class)

package com.agusstkd.goodlife.core.datetime

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Conversiones puras entre [LocalDate] y milisegundos epoch.
 *
 * KMP-ready: solo depende de kotlinx.datetime y kotlin.time, sin Android SDK.
 *
 * ## Filosofía del proyecto:
 * - ViewModels formatean fechas usando `DateProvider` + `AppLanguage`
 * - UI recibe strings ya formateados en el `UiState`
 * - Este archivo solo contiene conversiones de tipo, no formateo
 *
 * ## Por qué TimeZone.UTC y no currentSystemDefault():
 * Material3 DatePicker almacena fechas como milisegundos epoch en UTC midnight.
 * Usar la timezone local (ej: UTC-3 en Argentina) haría que esos milisegundos
 * apunten al día anterior a las 21:00 hs local → fecha incorrecta.
 *
 * @see com.agusstkd.goodlife.core.datetime.compose.DatePickerExt
 */
fun Long.toLocalDateUtc(): LocalDate =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date

fun LocalDate.toLongUtc(): Long =
    this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()