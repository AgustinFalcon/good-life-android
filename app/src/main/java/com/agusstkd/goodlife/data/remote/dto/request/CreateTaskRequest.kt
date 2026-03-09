package com.agusstkd.goodlife.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para el endpoint POST /api/v1/tasks.
 *
 * Representa el body JSON que se envía al backend para crear una nueva tarea.
 * Los campos [scheduledDate] y [daysOfWeek] son mutuamente excluyentes:
 * - Modo ONCE: [scheduledDate] con valor, [daysOfWeek] nulo.
 * - Modo RECURRENT: [daysOfWeek] con valores 1-7, [scheduledDate] nulo.
 *
 * Los campos de fecha usan `String` en formato ISO-8601 (YYYY-MM-DD)
 * porque la serialización es responsabilidad de la capa data,
 * no del dominio.
 *
 * @property title Título de la tarea. Requerido, no vacío.
 * @property description Descripción opcional.
 * @property scheduledDate Fecha única en formato "YYYY-MM-DD". Nulo en modo RECURRENT.
 * @property scheduledTime Hora en formato "HH:mm:ss". Opcional para ambos modos.
 * @property daysOfWeek Días de la semana como enteros 1 (lunes) a 7 (domingo). Nulo en modo ONCE.
 * @property startDate Inicio de recurrencia en formato "YYYY-MM-DD". Requerido si [daysOfWeek] no es nulo.
 * @property endDate Fin de recurrencia en formato "YYYY-MM-DD". Opcional.
 */
@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String?,
    val scheduledDate: String?,
    val scheduledTime: String?,
    val daysOfWeek: List<Int>?,
    val startDate: String?,
    val endDate: String?,
)
