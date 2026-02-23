package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import kotlinx.datetime.LocalDate

import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus

/**
 * Estado de UI para la pantalla Daily (tab de tareas diarias).
 *
 * Define los diferentes estados posibles:
 * - [Loading]: Cargando daily log del backend
 * - [Success]: Daily log cargado correctamente
 * - [Error]: Error al cargar/actualizar datos
 *
 * ## Responsabilidades:
 * - Contener fecha actual seleccionada y todos sus campos formateados
 * - Proveer datos listos para renderizar (sin lógica en UI)
 * - Ser inmutable y estable para Compose recomposition
 */
@Stable
sealed interface DailyUiState {

    /**
     * Estado de carga inicial.
     * Muestra skeleton/shimmer mientras se carga el daily log.
     */
    data object Loading : DailyUiState

    /**
     * Estado principal con el daily log cargado.
     *
     * ## Filosofía Clean Architecture:
     * ```
     * DailyTabViewModel
     *     ├── getDailyItemsUseCase() → Result.Success(DailyLogResponse)
     *     ├── DateProvider.today() → LocalDate(2026, 2, 3)
     *     ├── Formateo con AppLanguage
     *     └── buildSuccessState() → DailyUiState.Success(
     *             date = LocalDate(2026, 2, 3),
     *             dayNumber = 3,
     *             headerText = "Hoy",          ← Formateado en ViewModel
     *             monthYear = "Febrero 2026",  ← Formateado en ViewModel
     *             items = [...],               ← Mapeado a UI models
     *             completionRate = 0.75
     *         )
     * ```
     *
     * @property date Fecha actual seleccionada (para lógica interna del ViewModel)
     * @property dayNumber Número del día para el icono de calendario (1-31)
     * @property headerText Texto principal: "Hoy" | "Ayer" | "Mañana" | "Lun, 08 feb"
     * @property monthYear Mes y año completos: "Febrero 2026"
     * @property showFullDate Si debe mostrarse mes/año completo (false para hoy/ayer/mañana)
     * @property completionRate Porcentaje de items completados (0.0 - 1.0)
     * @property items Lista de items del daily log (tasks, habits, workouts, meals)
     * @property isRefreshing Si está refrescando datos (pull-to-refresh)
     */
    data class Success(
        val date: LocalDate,
        val dayNumber: Int,
        val headerText: String,
        val monthYear: String,
        val showFullDate: Boolean,
        val completionRate: Double,
        val items: List<DailyItemUiModel>,
        val isRefreshing: Boolean = false
    ) : DailyUiState

    /**
     * Estado de error.
     * Se muestra cuando falla la carga del daily log.
     *
     * @property code Código HTTP si el error vino del backend (400, 401, 403, 404, 500)
     * @property message Mensaje de error para mostrar al usuario
     */
    data class Error(
        val code: Int?,
        val message: String
    ) : DailyUiState
}

/**
 * Modelo UI de un item del daily log.
 *
 * Contiene solo los datos necesarios para renderizar el item.
 * El mapping Response → UiModel se hace en el ViewModel.
 *
 * @property id ID del item
 * @property type Tipo de item (TASK, HABIT, WORKOUT, MEAL)
 * @property title Título del item
 * @property description Descripción opcional
 * @property scheduledTime Hora programada ("08:30") o null
 * @property status Status actual (COMPLETED, PENDING, SKIPPED)
 */
data class DailyItemUiModel(
    val id: Long,
    val type: DailyItemType,
    val typeLabel: String,
    val title: String,
    val description: String?,
    val scheduledTime: String?,
    val status: DailyItemStatus,
)

/**
 * Mapea DailyItem (Domain Model) → DailyItemUiModel (UI Model).
 *
 * El título y descripción ya vienen extraídos en el Domain Model
 * (el mapeo Response→Domain se hizo en el Repository).
 *
 * Este método solo formatea la hora para la UI.
 */
fun DailyItem.toUiModel(typeLabel: String): DailyItemUiModel {
    return DailyItemUiModel(
        id = id,
        type = type,
        title = title,
        description = description,
        scheduledTime = scheduledTime?.let {
            "${it.hour}:${it.minute.toString().padStart(2, '0')}"
        },
        status = status,
        typeLabel = typeLabel
    )
}
