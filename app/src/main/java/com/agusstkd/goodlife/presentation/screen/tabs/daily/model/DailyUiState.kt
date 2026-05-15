package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.core.extensions.toDisplayString
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate

/**
 * Estado de UI para la pantalla Daily (tab de tareas diarias).
 *
 * Define los diferentes estados posibles:
 * - [Loading]:  Cargando daily log del backend (muestra skeleton)
 * - [Empty]:    El backend respondió correctamente pero no hay datos para ese día
 * - [Success]:  Daily log cargado con al menos un item
 * - [Error]:    Error técnico al cargar o actualizar datos
 *
 * ## Responsabilidades:
 * - Contener fecha actual seleccionada y todos sus campos formateados
 * - Proveer datos listos para renderizar (sin lógica en UI)
 * - Ser inmutable y estable para Compose recomposition
 *
 * ## ¿Por qué [Empty] es un estado separado y no [Success] con lista vacía?
 * Semánticamente son distintos: [Success] con lista vacía nunca debería ocurrir
 * (el backend devuelve 404 si no hay items, no 200 con lista vacía).
 * Tener [Empty] explícito permite renderizar una UI diferente sin condicional en [Success].
 */
@Stable
sealed interface DailyUiState {

    /**
     * Estado de carga inicial.
     * Muestra skeleton/shimmer mientras se carga el daily log.
     */
    @Immutable
    data object Loading : DailyUiState

    /**
     * No hay items para la fecha seleccionada (el backend respondió 404).
     *
     * No es un error — simplemente no existe daily log para ese día.
     * La UI debe mostrar un mensaje invitando a agregar items.
     */
    @Immutable
    data object Empty : DailyUiState

    /**
     * Estado principal con el daily log cargado.
     *
     * ## Filosofía Clean Architecture:
     * ```
     * DailyTabViewModel
     *     ├── getDailyItemsUseCase() → GetDailyItemsResult.Success(DailyLog)
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
     * @property isRefreshing Si está refrescando datos (pull-to-refresh o actualización de item)
     */
    @Immutable
    data class Success(
        val date: LocalDate,
        val dayNumber: Int,
        val headerText: String,
        val monthYear: String,
        val showFullDate: Boolean,
        val completionRate: Double,
        val items: ImmutableList<DailyItemUiModel>,
        val isRefreshing: Boolean = false,
        val activeFilter: DailyFilter = DailyFilter.ALL,
    ) : DailyUiState

    /**
     * Estado de error técnico (red o servidor).
     *
     * El mensaje ya viene formateado desde el ViewModel usando [AppLanguage],
     * listo para mostrar al usuario.
     *
     * @property message Mensaje de error para mostrar al usuario.
     */
    @Immutable
    data class Error(val message: String) : DailyUiState
}

val DailyUiState.Success.filteredItems: ImmutableList<DailyItemUiModel>
    get() = when (activeFilter) {
        DailyFilter.ALL -> items
        else -> items.filter { it.type == activeFilter.toItemType() }.toImmutableList()
    }

/**
 * Modelo UI de un item del daily log.
 *
 * Contiene solo los datos necesarios para renderizar el item.
 * El mapping Domain → UiModel se hace en el ViewModel.
 *
 * @property id ID del item
 * @property type Tipo de item (TASK, HABIT, WORKOUT, MEAL)
 * @property typeLabel Label localizado del tipo (ej: "Tarea", "Hábito")
 * @property title Título del item
 * @property description Descripción opcional
 * @property scheduledTime Hora programada formateada ("08:30") o null
 * @property status Status actual (COMPLETED, PENDING, SKIPPED)
 */
@Immutable
data class DailyItemUiModel(
    val id: Long,
    val type: DailyItemType,
    val typeLabel: String,
    val title: String,
    val description: String?,
    val scheduledTime: String?,
    val status: DailyItemStatus,
    val highlight: DailyItemHighlight = DailyItemHighlight.NONE,
)

/**
 * Mapea [DailyItem][com.agusstkd.goodlife.domain.model.daily.DailyItem] (Domain Model)
 * → [DailyItemUiModel] (UI Model).
 *
 * El título y descripción ya vienen extraídos en el Domain Model
 * (el mapeo Response→Domain se hizo en el Repository).
 * Este método solo formatea la hora para la UI.
 */
fun DailyItem.toUiModel(typeLabel: String, highlight: DailyItemHighlight = DailyItemHighlight.NONE): DailyItemUiModel {
    return DailyItemUiModel(
        id = id,
        type = type,
        title = title,
        description = description,
        scheduledTime = scheduledTime?.toDisplayString(),
        status = status,
        typeLabel = typeLabel,
        highlight = highlight
    )
}

enum class DailyItemHighlight { NONE, NEXT_UP, IN_PROGRESS }
