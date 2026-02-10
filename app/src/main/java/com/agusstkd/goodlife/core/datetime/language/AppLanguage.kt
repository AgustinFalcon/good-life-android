package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

/**
 * Idiomas soportados por la aplicación.
 *
 * Cada idioma define sus propios nombres de meses, días y formatos de fecha.
 * Usar sealed interface permite que el compilador verifique exhaustividad
 * en expresiones `when`.
 *
 * ## Agregar nuevo idioma:
 * 1. Crear un nuevo `data object` que implemente `AppLanguage`
 * 2. Definir monthNames, dayNamesShort, formats y relativeTexts
 * 3. El compilador marcará error en todos los `when` incompletos
 */
sealed interface AppLanguage {    val monthNames: MonthNames
    val dayNamesShort: DayOfWeekNames
    val formats: DateFormats
    val relativeTexts: RelativeDateTexts

    // ═══════════════════════════════════════════════════════════════════
    // ESPAÑOL
    // ═══════════════════════════════════════════════════════════════════

    data object Spanish : AppLanguage {

        override val monthNames = MonthNames(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )

        override val dayNamesShort = DayOfWeekNames(
            "lun", "mar", "mié", "jue", "vie", "sáb", "dom"
        )

        override val relativeTexts = RelativeDateTexts(
            today = "Hoy",
            yesterday = "Ayer",
            tomorrow = "Mañana"
        )

        override val formats = DateFormats(
            full = LocalDate.Format {
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
                chars(" de ")
                year()
            },
            dayMonth = LocalDate.Format {
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
            },
            dayNameAndDate = LocalDate.Format {
                dayOfWeek(dayNamesShort)
                chars(", ")
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
            }
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // ENGLISH
    // ═══════════════════════════════════════════════════════════════════

    data object English : AppLanguage {

        override val monthNames = MonthNames(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        override val dayNamesShort = DayOfWeekNames(
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        )

        override val relativeTexts = RelativeDateTexts(
            today = "Today",
            yesterday = "Yesterday",
            tomorrow = "Tomorrow"
        )

        override val formats = DateFormats(
            full = LocalDate.Format {
                monthName(monthNames)
                char(' ')
                dayOfMonth()
                chars(", ")
                year()
            },
            dayMonth = LocalDate.Format {
                monthName(monthNames)
                char(' ')
                dayOfMonth()
            },
            dayNameAndDate = LocalDate.Format {
                dayOfWeek(dayNamesShort)
                chars(", ")
                monthName(monthNames)
                char(' ')
                dayOfMonth()
            }
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // PORTUGUÊS
    // ═══════════════════════════════════════════════════════════════════    data object Portuguese : AppLanguage {        override val monthNames = MonthNames(
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        )        override val dayNamesShort = DayOfWeekNames(
            "seg", "ter", "qua", "qui", "sex", "sáb", "dom"
        )

        override val relativeTexts = RelativeDateTexts(
            today = "Hoje",
            yesterday = "Ontem",
            tomorrow = "Amanhã"
        )

        override val formats = DateFormats(
            full = LocalDate.Format {
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
                chars(" de ")
                year()
            },
            dayMonth = LocalDate.Format {
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
            },
            dayNameAndDate = LocalDate.Format {
                dayOfWeek(dayNamesShort)
                chars(", ")
                dayOfMonth()
                chars(" de ")
                monthName(monthNames)
            }
        )
    }
}
