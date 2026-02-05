package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

/**
 * Contenedor de formatos de fecha para un idioma específico.
 *
 * Agrupa todos los formatos necesarios para formatear fechas
 * de manera consistente en la UI.
 *
 * @property full Formato completo: "28 de enero de 2026"
 * @property dayMonth Formato día y mes: "28 de enero"
 * @property dayNameAndDate Formato con nombre de día: "Mié, 28 de enero"
 */
data class DateFormats(
    val full: DateTimeFormat<LocalDate>,
    val dayMonth: DateTimeFormat<LocalDate>,
    val dayNameAndDate: DateTimeFormat<LocalDate>
)

/**
 * Textos localizados para fechas relativas.
 *
 * @property today Texto para "hoy"
 * @property yesterday Texto para "ayer"
 * @property tomorrow Texto para "mañana"
 */
data class RelativeDateTexts(
    val today: String,
    val yesterday: String,
    val tomorrow: String
)
