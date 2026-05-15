package com.agusstkd.goodlife.data.remote.dto.response.task

import com.agusstkd.goodlife.domain.model.task.Task
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * DTO de respuesta del backend para una tarea completa.
 *
 * Mapea la respuesta JSON de los endpoints:
 * - `POST /api/v1/tasks` → 201 Created
 * - `GET /api/v1/tasks/{id}` → 200 OK
 *
 * Los campos de fecha llegan como `String` en formato ISO-8601 ("YYYY-MM-DD").
 * La conversión a tipos de dominio ([kotlinx.datetime.LocalDate], etc.)
 * se realiza en el [com.agusstkd.goodlife.data.repository.TaskRepositoryImpl].
 *
 * @property id Identificador único asignado por el backend.
 * @property userId Identificador del usuario propietario.
 * @property title Título de la tarea.
 * @property description Descripción opcional.
 * @property scheduledDate Fecha de ejecución única ("YYYY-MM-DD"). Nulo en modo RECURRENT.
 * @property scheduledTime Hora programada ("HH:mm:ss"). Opcional.
 * @property daysOfWeek Días de repetición como enteros 1-7. Nulo en modo ONCE.
 * @property startDate Inicio de vigencia ("YYYY-MM-DD"). Nulo si no aplica.
 * @property endDate Fin de vigencia ("YYYY-MM-DD"). Opcional.
 * @property isActive Indica si la tarea está activa (false = eliminada lógicamente).
 * @property createdAt Timestamp de creación en formato ISO-8601.
 * @property updatedAt Timestamp de última modificación en formato ISO-8601.
 */
@Serializable
data class TaskResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val scheduledDate: String?,
    val scheduledTime: String?,
    val daysOfWeek: List<Int>?,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

/**
 * Convierte el DTO de red a modelo de dominio [Task].
 *
 * Transforma:
 * - Strings ISO-8601 → [LocalDate] / [LocalTime] via `parse()`.
 * - `List<Int>?` (1-7) → `Set<DayOfWeek>` (vacío si null, para modo ONCE).
 * - `reminderEnabled` se inicializa en `false` porque el backend
 *   no gestiona recordatorios (son locales del dispositivo).
 *
 * @return Modelo de dominio [Task] listo para usar en UseCases y ViewModels.
 */
fun TaskResponse.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        scheduledDate = scheduledDate?.let { LocalDate.parse(it) },
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
        recurrentDays = daysOfWeek?.map { DayOfWeek(it) }?.toSet() ?: emptySet(),
        startDate = startDate?.let { LocalDate.parse(it) },
        endDate = endDate?.let { LocalDate.parse(it) },
        reminderEnabled = false,
    )
}
