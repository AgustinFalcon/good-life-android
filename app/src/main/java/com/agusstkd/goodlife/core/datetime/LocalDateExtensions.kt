package com.agusstkd.goodlife.core.datetime

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn/**
 * Obtiene la fecha de hoy en la zona horaria local del sistema.
 */
fun Clock.todayHere(): LocalDate = todayIn(TimeZone.currentSystemDefault())

/**
 * Verifica si esta fecha es hoy.
 *
 * @param clock Clock inyectado para testabilidad
 */
fun LocalDate.isToday(clock: Clock): Boolean =
    this == clock.todayHere()

/**
 * Verifica si esta fecha es ayer.
 *
 * @param clock Clock inyectado para testabilidad
 */
fun LocalDate.isYesterday(clock: Clock): Boolean =
    this == clock.todayHere().minus(1, DateTimeUnit.DAY)

/**
 * Verifica si esta fecha es mañana.
 *
 * @param clock Clock inyectado para testabilidad
 */
fun LocalDate.isTomorrow(clock: Clock): Boolean =
    this == clock.todayHere().plus(1, DateTimeUnit.DAY)

/**
 * Formatea la fecha de forma amigable para el usuario.
 *
 * @param clock Clock para determinar si es hoy/ayer/mañana
 * @param language Idioma para los textos y formato
 * @return "Hoy", "Ayer", "Mañana", o "Mié, 28 de enero" (según idioma)
 */
fun LocalDate.toFriendlyString(clock: Clock, language: AppLanguage): String = when {
    isToday(clock) -> language.relativeTexts.today
    isYesterday(clock) -> language.relativeTexts.yesterday
    isTomorrow(clock) -> language.relativeTexts.tomorrow
    else -> format(language.formats.dayNameAndDate).replaceFirstChar { it.uppercase() }
}

/**
 * Formatea para el header del modal de acciones.
 *
 * @param clock Clock para determinar si es hoy/ayer/mañana
 * @param language Idioma para los textos y formato
 * @return "Hoy, 28 de enero de 2026" o "28 de enero de 2026" (según idioma)
 */
fun LocalDate.toModalHeaderString(clock: Clock, language: AppLanguage): String {
    val texts = language.relativeTexts
    val prefix = when {
        isToday(clock) -> "${texts.today}, "
        isYesterday(clock) -> "${texts.yesterday}, "
        isTomorrow(clock) -> "${texts.tomorrow}, "
        else -> ""
    }
    return prefix + format(language.formats.full)
}
