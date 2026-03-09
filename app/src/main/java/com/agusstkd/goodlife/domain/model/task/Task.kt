package com.agusstkd.goodlife.domain.model.task

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Modelo de dominio que representa una tarea del usuario en GoodLife.
 *
 * Una tarea puede programarse de dos maneras mutuamente excluyentes:
 * - **Modo ONCE**: tiene [scheduledDate] y [recurrentDays] vacío.
 * - **Modo RECURRENT**: tiene [recurrentDays] con al menos un día, [startDate] requerido y [scheduledDate] nulo.
 *
 * Esta clase es inmutable y KMP-ready: no depende del Android SDK.
 *
 * @property id Identificador único asignado por el backend al crear la tarea.
 * @property title Nombre descriptivo de la tarea. Requerido, no vacío.
 * @property description Detalle opcional de la tarea.
 * @property scheduledDate Fecha específica de ejecución. Solo presente en modo ONCE.
 * @property scheduledTime Hora de ejecución opcional. Aplica a ambos modos.
 * @property recurrentDays Días de la semana en que se repite la tarea. Vacío en modo ONCE.
 * @property startDate Fecha de inicio de la recurrencia. Requerido si [recurrentDays] no está vacío.
 * @property endDate Fecha de fin de la recurrencia. Opcional; si es nulo, la tarea no tiene fecha de corte.
 * @property reminderEnabled Indica si el usuario activó recordatorio para esta tarea.
 */
data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val scheduledDate: LocalDate?,
    val scheduledTime: LocalTime?,
    val recurrentDays: Set<DayOfWeek>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val reminderEnabled: Boolean,
)
