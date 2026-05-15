package com.agusstkd.goodlife.presentation.screen.add.mealplan.model

import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Acciones que el usuario puede disparar en el wizard de creación de Plan de Comida.
 *
 * Cada acción representa exactamente un evento de UI — un toque, un cambio de texto,
 * o una selección. El [CreateMealPlanViewModel] recibe todas las acciones a través
 * de un único punto de entrada `onAction(action)`.
 *
 * Las acciones se agrupan por contexto del wizard:
 * - **Navegación**: avanzar/retroceder entre pasos
 * - **Paso 1A**: interacciones con el catálogo de meals existentes
 * - **Paso 1B**: formulario para crear una meal nueva
 * - **Paso 2**: gestión de ingredientes y macros en tiempo real
 * - **Paso 3**: configuración de días, fechas y hora del plan
 * - **Submit**: envío, manejo de error y éxito
 *
 * @see CreateMealPlanViewModel
 * @see CreateMealPlanUiState
 */
sealed interface CreateMealPlanUiAction {

    // ── Navegación del wizard ────────────────────────────────────────────

    /** Avanza al siguiente paso del wizard. Valida el paso actual antes de avanzar. */
    data object OnNextStep : CreateMealPlanUiAction

    /** Retrocede al paso anterior. No requiere validación. */
    data object OnPreviousStep : CreateMealPlanUiAction

    /** Cierra el wizard sin guardar cambios. */
    data object OnDismiss : CreateMealPlanUiAction

    // ── Paso 1A: Catálogo de meals ───────────────────────────────────────

    /**
     * Actualiza el texto de búsqueda en el catálogo de meals.
     * Dispara un nuevo fetch con debounce.
     * @param query Texto ingresado por el usuario.
     */
    data class OnMealSearchQueryChange(val query: String) : CreateMealPlanUiAction

    /**
     * Filtra el catálogo por tipo de comida.
     * @param mealType Tipo seleccionado, o `null` para mostrar todos.
     */
    data class OnMealTypeFilterChange(val mealType: MealType?) : CreateMealPlanUiAction

    /** Carga la siguiente página del catálogo de meals (scroll infinito). */
    data object OnLoadMoreMeals : CreateMealPlanUiAction

    /**
     * Selecciona una meal existente del catálogo y salta al Paso 3.
     * @param meal La meal elegida por el usuario.
     */
    data class OnSelectMeal(val meal: MealSummary) : CreateMealPlanUiAction

    // ── Paso 1B: Crear nueva meal ────────────────────────────────────────

    /** Activa el modo de creación de nueva meal (muestra el formulario del Paso 1B). */
    data object OnCreateNewMeal : CreateMealPlanUiAction

    /**
     * Actualiza el nombre de la nueva meal.
     * @param name Texto ingresado por el usuario.
     */
    data class OnNewMealNameChange(val name: String) : CreateMealPlanUiAction

    /**
     * Actualiza la descripción de la nueva meal.
     * @param description Texto ingresado por el usuario.
     */
    data class OnNewMealDescriptionChange(val description: String) : CreateMealPlanUiAction

    data class OnNewMealTypeChange(val newMealType: MealType) : CreateMealPlanUiAction

    // ── Paso 2: Ingredientes ─────────────────────────────────────────────

    /**
     * Actualiza el texto de búsqueda en el catálogo de ingredientes.
     * @param query Texto ingresado por el usuario.
     */
    data class OnIngredientSearchQueryChange(val query: String) : CreateMealPlanUiAction

    /** Carga la siguiente página del catálogo de ingredientes (scroll infinito). */
    data object OnLoadMoreIngredients : CreateMealPlanUiAction

    /**
     * Abre el bottom sheet para configurar la cantidad de un ingrediente.
     * @param ingredientId ID del ingrediente a configurar.
     */
    data class OnOpenQuantityBottomSheet(val ingredientId: Long) : CreateMealPlanUiAction

    /** Cierra el bottom sheet de cantidad sin guardar cambios. */
    data object OnDismissQuantityBottomSheet : CreateMealPlanUiAction

    /**
     * Confirma la cantidad y unidad de un ingrediente y lo agrega a la lista.
     * Recalcula los macros totales del preview.
     * @param ingredientId ID del ingrediente confirmado.
     * @param quantity Cantidad ingresada por el usuario (> 0).
     * @param unit Unidad seleccionada (ej: "g", "ml").
     */
    data class OnConfirmIngredientQuantity(
        val ingredientId: Long,
        val quantity: Double,
        val unit: String,
    ) : CreateMealPlanUiAction

    /**
     * Elimina un ingrediente de la lista y recalcula los macros del preview.
     * @param ingredientId ID del ingrediente a eliminar.
     */
    data class OnRemoveIngredient(val ingredientId: Long) : CreateMealPlanUiAction

    /** Abre el dialog para crear un ingrediente custom. */
    data object OnShowCreateIngredientDialog : CreateMealPlanUiAction

    /** Cierra el dialog de ingrediente custom sin guardar. */
    data object OnDismissCreateIngredientDialog : CreateMealPlanUiAction

    /**
     * Envía el formulario de creación de ingrediente custom al backend.
     * Si tiene éxito, el ingrediente nuevo aparece en el catálogo y se agrega a la meal.
     *
     * @param name Nombre del ingrediente.
     * @param brand Marca opcional.
     * @param servingSize Tamaño de la porción de referencia.
     * @param servingUnit Unidad de la porción (ej: "g", "ml").
     * @param calories Calorías por porción.
     * @param protein Proteínas en gramos por porción.
     * @param carbs Carbohidratos en gramos por porción.
     * @param fat Grasas en gramos por porción.
     */
    data class OnCreateCustomIngredient(
        val name: String,
        val brand: String?,
        val servingSize: Double,
        val servingUnit: String,
        val calories: Double,
        val protein: Double,
        val carbs: Double,
        val fat: Double,
    ) : CreateMealPlanUiAction

    // ── Paso 3: Planificación ────────────────────────────────────────────

    /**
     * Selecciona el tipo de comida para el plan.
     * @param mealType El tipo elegido (BREAKFAST, LUNCH, etc.).
     */
    data class OnMealTypeSelected(val mealType: MealType) : CreateMealPlanUiAction

    /**
     * Activa o desactiva un día de la semana en la selección.
     * @param day El día a togglear.
     */
    data class OnDayToggled(val day: DayOfWeek) : CreateMealPlanUiAction

    /** Activa o desactiva la fecha de inicio. Al desactivar limpia [CreateMealPlanUiState.startDate]. */
    data object OnHasStartDateToggle : CreateMealPlanUiAction

    /** Activa o desactiva la fecha de fin. Al desactivar limpia [CreateMealPlanUiState.endDate]. */
    data object OnHasEndDateToggle : CreateMealPlanUiAction

    /** Activa o desactiva la hora del plan. Al desactivar limpia [CreateMealPlanUiState.scheduledTime]. */
    data object OnHasTimeToggle : CreateMealPlanUiAction

    /**
     * Abre el date picker para el campo indicado.
     * @param field Indica si es fecha de inicio o fin.
     */
    data class OnDatePickerOpen(val field: MealDatePickerField) : CreateMealPlanUiAction

    /** Cierra el date picker sin aplicar cambios. */
    data object OnDatePickerDismiss : CreateMealPlanUiAction

    /**
     * Aplica la fecha seleccionada en el date picker.
     * @param field Indica a qué campo aplicar la fecha.
     * @param date La fecha elegida.
     */
    data class OnDateSelected(
        val field: MealDatePickerField,
        val date: LocalDate,
    ) : CreateMealPlanUiAction

    /** Abre el time picker. */
    data object OnTimePickerOpen : CreateMealPlanUiAction

    /** Cierra el time picker sin aplicar cambios. */
    data object OnTimePickerDismiss : CreateMealPlanUiAction

    /**
     * Aplica la hora seleccionada en el time picker.
     * @param time La hora elegida.
     */
    data class OnTimeSelected(val time: LocalTime) : CreateMealPlanUiAction

    // ── Submit ───────────────────────────────────────────────────────────

    /**
     * Inicia el flujo de submit: crea la meal si es nueva, luego crea el plan.
     * Valida que haya [CreateMealPlanUiState.mealType] y [CreateMealPlanUiState.selectedDays].
     */
    data object OnSubmit : CreateMealPlanUiAction

    /** Descarta el mensaje de error visible en pantalla. */
    data object OnErrorDismissed : CreateMealPlanUiAction

    /** Notifica que la animación de éxito terminó y el wizard puede cerrarse. */
    data object OnSuccessAnimationFinished : CreateMealPlanUiAction
}
