package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

/**
 * Estado de UI para la pantalla Daily (tab de tareas diarias).
 *
 * ## Responsabilidades:
 * - Contener fecha actual seleccionada y todos sus campos formateados
 * - Proveer datos listos para renderizar (sin lógica en UI)
 * - Ser inmutable y estable para Compose recomposition
 *
 * ## Filosofía Clean Architecture:
 * ```
 * DailyTabViewModel
 *     ├── DateProvider.today() → LocalDate(2026, 2, 3)
 *     ├── Formateo con AppLanguage
 *     └── buildUiState() → DailyUiState(
 *             date = LocalDate(2026, 2, 3),
 *             dayNumber = 3,
 *             headerText = "Hoy",          ← Formateado aquí
 *             monthYear = "Febrero 2026",  ← Formateado aquí
 *             showFullDate = false
 *         )
 * 
 * DailyScreen(uiState)
 *     └── DateHeaderComponent(
 *             dayNumber = 3,
 *             headerText = "Hoy",          ← Solo renderiza
 *             monthYear = null
 *         )
 * ```
 *
 * ## Ventajas de este patrón:
 * - **UI pura:** DailyScreen y DateHeaderComponent NO conocen Clock/Language
 * - **Testeable:** Mock del UiState es trivial (solo strings)
 * - **Previews fáciles:** No necesitás inyectar Clock en Previews
 * - **Reusable:** DateHeaderComponent sirve para cualquier fecha
 *
 * @property date Fecha actual seleccionada (para lógica interna del ViewModel)
 * @property dayNumber Número del día para el icono de calendario (1-31)
 * @property headerText Texto principal: "Hoy" | "Ayer" | "Mañana" | "Lun, 08 feb"
 * @property monthYear Mes y año completos: "Febrero 2026" | "Octubre 2025"
 * @property showFullDate Si debe mostrarse mes/año completo (false para hoy/ayer/mañana)
 */
@Stable
data class DailyUiState(
    val date: LocalDate,
    val dayNumber: Int,
    val headerText: String,
    val monthYear: String,
    val showFullDate: Boolean
)
