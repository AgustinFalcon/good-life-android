package com.agusstkd.goodlife.presentation.screen.tabs.meals.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

/**
 * Estado de UI para la pantalla Meals (tab de nutrición diaria).
 *
 * Define los diferentes estados posibles:
 * - [Loading]:  Cargando planes del backend (muestra skeleton)
 * - [Empty]:    El backend respondió pero no hay planes para ese día
 * - [Success]:  Planes cargados con al menos un item
 * - [Error]:    Error técnico al cargar datos
 *
 * ## Filosofía:
 * [Empty] es un estado separado de [Success] con lista vacía porque
 * son semánticamente distintos: el backend devuelve 404 o lista vacía
 * si no hay planes, no un 200 con lista vacía. Tener [Empty] explícito
 * permite renderizar UI diferenciada sin condicionales en [Success].
 */
@Stable
sealed interface MealsUiState {

    /**
     * Cargando planes del backend. Muestra skeleton/shimmer.
     */
    @Immutable
    data object Loading : MealsUiState

    /**
     * No hay planes de comida para la fecha seleccionada.
     *
     * No es un error — simplemente no existe plan para ese día.
     * La UI muestra un mensaje invitando a crear el primer plan.
     */
    @Immutable
    data object Empty : MealsUiState

    /**
     * Estado principal con los planes de comida del día cargados.
     *
     * Todos los campos ya vienen formateados desde el ViewModel,
     * la UI solo renderiza sin lógica de negocio.
     *
     * @property date Fecha seleccionada (para uso interno del ViewModel).
     * @property dayNumber Número del día (1-31) para el icono de calendario.
     * @property headerText Texto principal: "Hoy" | "Ayer" | "Mañana" | "Lun, 08 feb".
     * @property monthYear Mes y año completos, null si es fecha relativa.
     * @property showFullDate Si mostrar mes/año (false para hoy/ayer/mañana).
     * @property mealPlans Lista de planes del día listos para renderizar.
     * @property totalCalories Suma de calorías de todos los planes.
     * @property totalProtein Suma de proteínas de todos los planes en gramos.
     * @property totalCarbs Suma de carbohidratos de todos los planes en gramos.
     * @property totalFat Suma de grasas de todos los planes en gramos.
     * @property isRefreshing Si está refrescando datos (sin mostrar loading completo).
     */
    @Immutable
    data class Success(
        val date: LocalDate,
        val dayNumber: Int,
        val headerText: String,
        val monthYear: String,
        val showFullDate: Boolean,
        val mealPlans: List<MealPlanUiModel>,
        val totalCalories: Double,
        val totalProtein: Double,
        val totalCarbs: Double,
        val totalFat: Double,
        val isRefreshing: Boolean = false,
    ) : MealsUiState

    /**
     * Error técnico (red o servidor).
     *
     * El mensaje ya viene formateado desde el ViewModel usando [AppLanguage].
     *
     * @property message Mensaje de error listo para mostrar al usuario.
     */
    @Immutable
    data class Error(val message: String) : MealsUiState
}
