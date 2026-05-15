package com.agusstkd.goodlife.core.extensions

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Parsea un String ISO-8601 a [LocalDate], o null si el formato es inválido.
 */
fun String?.parseLocalDateOrNull(): LocalDate? =
    this?.runCatching { LocalDate.parse(this) }?.getOrNull()

/**
 * Parsea un String ISO-8601 a [LocalTime], o null si el formato es inválido.
 */
fun String?.parseLocalTimeOrNull(): LocalTime? =
    this?.runCatching { LocalTime.parse(this) }?.getOrNull()

/**
 * Formatea un [LocalTime] como "HH:mm" (ej: "08:30", "14:05").
 */
fun LocalTime.toDisplayString(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

/**
 * Convierte [LocalDate] a String ISO-8601, o cadena vacía si es null.
 */
fun LocalDate?.toIsoStringOrEmpty(): String = this?.toString() ?: ""

/**
 * Convierte [LocalTime] a String ISO-8601, o cadena vacía si es null.
 */
fun LocalTime?.toIsoStringOrEmpty(): String = this?.toString() ?: ""

/**
 * Agrega o quita un [DayOfWeek] del Set (toggle).
 * Si el día ya está, lo saca; si no está, lo agrega.
 */
fun Set<DayOfWeek>.toggleDay(day: DayOfWeek): Set<DayOfWeek> =
    if (day in this) this - day else this + day
