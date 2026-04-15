package com.agusstkd.goodlife.presentation.screen.add.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.core.extensions.toggleDay
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.domain.model.nutrition.MealIngredientDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.model.nutrition.ScheduledMealDraft
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateCustomIngredientUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateCustomMealUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateMealPlanUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.SearchIngredientsUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.SearchMealsUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomIngredientResult
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomMealResult
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateMealPlanResult
import com.agusstkd.goodlife.domain.usecase.nutrition.result.SearchIngredientsResult
import com.agusstkd.goodlife.domain.usecase.nutrition.result.SearchMealsResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiAction
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.CreateMealPlanUiState
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealDatePickerField
import com.agusstkd.goodlife.presentation.screen.add.mealplan.model.MealPlanWizardStep
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del wizard de creación de Plan de Comida.
 *
 * Sigue el patrón MVI: expone un único [uiState] inmutable y recibe todas las
 * interacciones del usuario a través de [onAction]. Ningún componente de UI modifica
 * el estado directamente; todo pasa por una [CreateMealPlanUiAction].
 *
 * ---
 *
 * ### Flujo del wizard
 *
 * El wizard tiene 3 pasos representados por [MealPlanWizardStep]:
 *
 * 1. **SELECT_MEAL** — El usuario elige una meal del catálogo o activa la creación custom.
 *    - Si elige una existente ([OnSelectMeal]) salta directo al paso 3.
 *    - Si activa "crear nueva" ([OnCreateNewMeal]) avanza al paso 2.
 *
 * 2. **MEAL_INGREDIENTS** — Agrega ingredientes y ve los macros acumulados en tiempo real.
 *    Disponible solo en el camino "meal custom".
 *
 * 3. **SCHEDULE** — Configura tipo de comida, días de la semana, fechas opcionales y hora.
 *    El botón de submit dispara [OnSubmit].
 *
 * ---
 *
 * ### Responsabilidades principales
 *
 * - Validar cada paso antes de avanzar ([advanceWizardStep]).
 * - Gestionar la navegación hacia atrás respetando el camino tomado ([recoilWizardStep]).
 * - Buscar meals e ingredientes con debounce y paginación ([mealSharedFlow], [ingredientSharedFlow]).
 * - Recalcular macros del preview en tiempo real al agregar/quitar ingredientes.
 * - Coordinar el submit secuencial: crear meal custom si aplica, luego crear el plan ([submitMealPlan]).
 *
 * ---
 *
 * @param navigationController Controlador de navegación de Compose.
 * @param language Fuente de textos localizados y formatos de fecha de la app.
 * @param createMealPlanUseCase Persiste el [MealPlanDraft] completo en el backend.
 * @param createCustomMealUseCase Persiste una meal nueva en el catálogo antes de planificarla.
 * @param createCustomIngredientUseCase Persiste un ingrediente custom en el catálogo.
 * @param searchMealUseCase Busca meals por query con soporte de paginación.
 * @param searchIngredientUseCase Busca ingredientes por query con soporte de paginación.
 *
 * @see CreateMealPlanUiState
 * @see CreateMealPlanUiAction
 * @see MealPlanWizardStep
 */
class CreateMealPlanViewModel(
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
    private val createMealPlanUseCase: CreateMealPlanUseCase,
    private val createCustomMealUseCase: CreateCustomMealUseCase,
    private val createCustomIngredientUseCase: CreateCustomIngredientUseCase,
    private val searchMealUseCase: SearchMealsUseCase,
    private val searchIngredientUseCase: SearchIngredientsUseCase,
) : ViewModel() {

    /** Textos localizados específicos de esta pantalla. */
    val texts: CreateMealPlanTexts get() = language.createMealPlanTexts

    /** Textos localizados para el date picker (título, OK, cancelar, etc.). */
    val datePickerTexts get() = language.datePickerTexts

    /** Textos localizados compartidos entre pantallas de creación. */
    val sharedTexts: CreateItemSharedTexts get() = language.createItemSharedTexts

    /** Textos de accesibilidad (content descriptions) de la pantalla. */
    val accessibilityTexts get() = language.accessibilityTexts

    /** Nombres cortos de los días de la semana en el idioma activo (ej: "Lun", "Mar"…). */
    val dayNames: List<String> get() = language.dayNamesShort.names

    /**
     * Canal de búsqueda de meals con debounce.
     * Cada emisión representa una nueva query del usuario; el operador [debounce] en el
     * [init] descarta las intermedias y solo dispara [searchMeals] tras 300 ms de silencio.
     */
    private val mealSharedFlow = MutableSharedFlow<String>()

    /**
     * Canal de búsqueda de ingredientes con debounce.
     * Mismo mecanismo que [mealSharedFlow] pero para el catálogo de ingredientes.
     */
    private val ingredientSharedFlow = MutableSharedFlow<String>()

    /**
     * Lista de pares (tipo de comida → etiqueta localizada) para el selector del paso 3.
     * Se construye una sola vez de forma lazy porque los textos no cambian en el ciclo de vida.
     */
    val mealTypeEntries: List<Pair<MealType, String>> by lazy {
        val t = language.mealTypeTexts
        listOf(
            MealType.BREAKFAST to t.breakfast,
            MealType.LUNCH to t.lunch,
            MealType.DINNER to t.dinner,
            MealType.SNACK to t.snack,
            MealType.PRE_WORKOUT to t.preWorkout,
            MealType.POST_WORKOUT to t.postWorkout,
        )
    }

    private val _uiState = MutableStateFlow(CreateMealPlanUiState())

    /** Estado inmutable expuesto a la UI. Solo se modifica a través de [onAction]. */
    val uiState: StateFlow<CreateMealPlanUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            mealSharedFlow.debounce(300).collect { query -> searchMeals(query = query) }
        }
        viewModelScope.launch {
            ingredientSharedFlow.debounce(300).collect { query -> searchIngredients(query = query) }
        }
    }

    /**
     * Busca ingredientes en el catálogo y actualiza [CreateMealPlanUiState.ingredientCatalog].
     *
     * @param query Texto de búsqueda.
     * @param page Página a cargar. `0` reemplaza el catálogo; cualquier valor mayor acumula
     * los resultados al final (scroll infinito).
     */
    private suspend fun searchIngredients(query: String, page: Int = 0) {
        _uiState.update { it.copy(isLoadingIngredients = true) }
        when (val result = searchIngredientUseCase.execute(query = query, page = page)) {
            is SearchIngredientsResult.Success -> _uiState.update {
                it.copy(
                    ingredientCatalog = if (page == 0) result.data.items.toImmutableList()
                    else (it.ingredientCatalog + result.data.items).toImmutableList(),
                    ingredientPage = result.data.page,
                    ingredientHasMore = !result.data.isLastPage,
                    isLoadingIngredients = false
                )
            }

            SearchIngredientsResult.NetworkError -> _uiState.update {
                it.copy(isLoadingIngredients = false, errorMessage = texts.errorNetwork)
            }

            is SearchIngredientsResult.ServerError -> _uiState.update {
                it.copy(isLoadingIngredients = false, errorMessage = result.message)
            }
        }
    }

    /**
     * Busca meals en el catálogo y actualiza [CreateMealPlanUiState.mealCatalog].
     *
     * @param query Texto de búsqueda.
     * @param page Página a cargar. `0` reemplaza el catálogo; cualquier valor mayor acumula
     * los resultados al final (scroll infinito).
     */
    private suspend fun searchMeals(query: String, page: Int = 0) {
        _uiState.update { it.copy(isLoadingMeals = true) }
        when (val result = searchMealUseCase.execute(query = query, page = page)) {
            is SearchMealsResult.Success -> _uiState.update {
                // página 0 = búsqueda nueva → reemplazar; página > 0 = cargar más → acumular
                it.copy(
                    mealCatalog = if (page == 0) result.data.items.toImmutableList()
                    else (it.mealCatalog + result.data.items).toImmutableList(),
                    mealCatalogPage = result.data.page,
                    mealCatalogHasMore = !result.data.isLastPage,
                    isLoadingMeals = false,
                )
            }

            is SearchMealsResult.ServerError -> _uiState.update {
                it.copy(isLoadingMeals = false, errorMessage = result.message)
            }

            SearchMealsResult.NetworkError -> _uiState.update {
                it.copy(isLoadingMeals = false, errorMessage = texts.errorNetwork)
            }
        }
    }

    /**
     * Único punto de entrada para todas las interacciones del usuario (patrón MVI).
     * Cada [CreateMealPlanUiAction] mapea a un efecto sobre [_uiState] o a una
     * llamada de navegación; ningún componente de UI modifica el estado directamente.
     */
    fun onAction(action: CreateMealPlanUiAction) {
        when (action) {

            // ── Navegación del wizard ────────────────────────────────────────
            is CreateMealPlanUiAction.OnNextStep -> advanceWizardStep()
            is CreateMealPlanUiAction.OnPreviousStep -> recoilWizardStep()
            is CreateMealPlanUiAction.OnDismiss -> navigationController.navigateUp()

            // ── Paso 1A: Catálogo de meals ───────────────────────────────────
            is CreateMealPlanUiAction.OnSelectMeal -> {
                _uiState.update {
                    it.copy(
                        selectedMeal = action.meal,
                        currentStep = MealPlanWizardStep.SCHEDULE,
                    )
                }
            }

            is CreateMealPlanUiAction.OnMealTypeFilterChange -> {
                _uiState.update { it.copy(selectedMealTypeFilter = action.mealType) }
            }

            // ── Paso 1B: Nueva meal ──────────────────────────────────────────
            is CreateMealPlanUiAction.OnCreateNewMeal -> {
                _uiState.update { it.copy(isCreatingNewMeal = true) }
            }

            is CreateMealPlanUiAction.OnNewMealNameChange -> {
                _uiState.update { it.copy(newMealName = action.name) }
            }

            is CreateMealPlanUiAction.OnNewMealDescriptionChange -> {
                _uiState.update { it.copy(newMealDescription = action.description) }
            }

            is CreateMealPlanUiAction.OnNewMealTypeChange -> {
                _uiState.update { it.copy(newMealType = action.newMealType) }
            }

            // ── Paso 2: Ingredientes ─────────────────────────────────────────
            is CreateMealPlanUiAction.OnOpenQuantityBottomSheet -> {
                updateStatusQuantityBottomSheet(
                    ingredientId = action.ingredientId,
                    showQuantityBottomSheet = true,
                )
            }

            is CreateMealPlanUiAction.OnDismissQuantityBottomSheet -> {
                updateStatusQuantityBottomSheet(
                    ingredientId = null,
                    showQuantityBottomSheet = false,
                )
            }

            is CreateMealPlanUiAction.OnConfirmIngredientQuantity -> {
                updateIngredientQuantity(
                    ingredientId = action.ingredientId,
                    quantity = action.quantity,
                    unit = action.unit,
                )
            }

            is CreateMealPlanUiAction.OnRemoveIngredient -> {
                removeSelectedIngredient(ingredientId = action.ingredientId)
            }

            is CreateMealPlanUiAction.OnShowCreateIngredientDialog -> {
                showCreateIngredientDialog(showDialog = true)
            }

            is CreateMealPlanUiAction.OnDismissCreateIngredientDialog -> {
                showCreateIngredientDialog(showDialog = false)
            }

            // ── Paso 3: Planificación ────────────────────────────────────────
            is CreateMealPlanUiAction.OnMealTypeSelected -> {
                _uiState.update { it.copy(mealType = action.mealType) }
            }

            is CreateMealPlanUiAction.OnDayToggled -> {
                _uiState.update { it.copy(selectedDays = it.selectedDays.toggleDay(action.day)) }
            }

            is CreateMealPlanUiAction.OnTimeSelected -> {
                _uiState.update { it.copy(showTimePicker = false, scheduledTime = action.time) }
            }

            is CreateMealPlanUiAction.OnDatePickerOpen -> {
                _uiState.update { it.copy(activeDatePickerField = action.field) }
            }

            is CreateMealPlanUiAction.OnDatePickerDismiss -> {
                _uiState.update { it.copy(activeDatePickerField = null) }
            }

            is CreateMealPlanUiAction.OnTimePickerOpen -> {
                _uiState.update { it.copy(showTimePicker = true) }
            }

            is CreateMealPlanUiAction.OnTimePickerDismiss -> {
                _uiState.update { it.copy(showTimePicker = false) }
            }

            is CreateMealPlanUiAction.OnHasStartDateToggle -> {
                _uiState.update {
                    it.copy(
                        hasStartDate = !it.hasStartDate,
                        startDate = if (it.hasStartDate) null else it.startDate,
                        startDateDisplay = if (it.hasStartDate) "" else it.startDateDisplay,
                    )
                }
            }

            is CreateMealPlanUiAction.OnHasEndDateToggle -> {
                _uiState.update {
                    it.copy(
                        hasEndDate = !it.hasEndDate,
                        endDate = if (it.hasEndDate) null else it.endDate,
                        endDateDisplay = if (it.hasEndDate) "" else it.endDateDisplay,
                    )
                }
            }

            is CreateMealPlanUiAction.OnHasTimeToggle -> {
                _uiState.update {
                    it.copy(
                        hasTime = !it.hasTime,
                        scheduledTime = if (it.hasTime) null else it.scheduledTime
                    )
                }
            }

            is CreateMealPlanUiAction.OnDateSelected -> {
                val display = language.formats.dayMonth.format(action.date)
                _uiState.update {
                    when (action.field) {
                        MealDatePickerField.START_DATE ->
                            it.copy(
                                startDate = action.date,
                                activeDatePickerField = null,
                                startDateDisplay = display
                            )

                        MealDatePickerField.END_DATE ->
                            it.copy(
                                endDate = action.date,
                                activeDatePickerField = null,
                                endDateDisplay = display
                            )
                    }
                }
            }

            // ── Submit ───────────────────────────────────────────────────────
            is CreateMealPlanUiAction.OnSubmit -> {
                viewModelScope.launch { submitMealPlan() }
            }

            is CreateMealPlanUiAction.OnErrorDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            is CreateMealPlanUiAction.OnSuccessAnimationFinished -> {
                navigationController.navigateUp()
            }

            is CreateMealPlanUiAction.OnMealSearchQueryChange -> {
                _uiState.update { it.copy(mealSearchQuery = action.query) }
                viewModelScope.launch { mealSharedFlow.emit(action.query) }
            }

            is CreateMealPlanUiAction.OnIngredientSearchQueryChange -> {
                _uiState.update { it.copy(ingredientSearchQuery = action.query) }
                viewModelScope.launch { ingredientSharedFlow.emit(action.query) }
            }

            is CreateMealPlanUiAction.OnLoadMoreMeals -> {
                val state = _uiState.value
                if (state.isLoadingMeals || !state.mealCatalogHasMore) return
                viewModelScope.launch {
                    searchMeals(
                        query = state.mealSearchQuery,
                        page = state.mealCatalogPage + 1
                    )
                }

            }

            is CreateMealPlanUiAction.OnLoadMoreIngredients -> {
                val state = _uiState.value
                if (state.isLoadingIngredients || !state.ingredientHasMore) return
                viewModelScope.launch {
                    searchIngredients(
                        query = state.ingredientSearchQuery,
                        page = state.ingredientPage + 1
                    )
                }
            }

            is CreateMealPlanUiAction.OnCreateCustomIngredient -> {
                _uiState.update { it.copy(isLoading = true) }
                val dto = CreateCustomIngredientRequestDto(
                    name = action.name,
                    brand = action.brand,
                    servingSize = action.servingSize,
                    servingUnit = action.servingUnit,
                    calories = action.calories,
                    protein = action.protein,
                    carbs = action.carbs,
                    fat = action.fat,
                )
                viewModelScope.launch {
                    when (val result = createCustomIngredientUseCase.execute(request = dto)) {
                        is CreateCustomIngredientResult.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                showCreateIngredientDialog = false,
                                ingredientCatalog = (it.ingredientCatalog + result.ingredient).toImmutableList(),
                            )
                        }

                        is CreateCustomIngredientResult.ServerError -> _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.message)
                        }

                        CreateCustomIngredientResult.NetworkError -> _uiState.update {
                            it.copy(isLoading = false, errorMessage = texts.errorNetwork)
                        }

                        CreateCustomIngredientResult.ValidationError -> _uiState.update {
                            it.copy(isLoading = false, errorMessage = texts.errorIngredientNameEmpty)
                        }
                    }
                }
            }

        }
    }

    // ── Navegación entre pasos ──────────────────────────────────────────────

    /**
     * Avanza al siguiente paso del wizard validando el paso actual antes de continuar.
     *
     * Validaciones por paso:
     * - **SELECT_MEAL**: si el usuario está creando una meal nueva, el nombre no puede estar vacío.
     * - **MEAL_INGREDIENTS**: la lista de ingredientes no puede estar vacía.
     * - **SCHEDULE**: no avanza (es el último paso; el avance se hace con [submitMealPlan]).
     *
     * Si la validación falla, actualiza `errorMessage` y retorna sin cambiar el paso.
     */
    private fun advanceWizardStep() {
        val state = _uiState.value

        // Guard clauses: validamos primero, salimos en error
        when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL -> {
                if (state.isCreatingNewMeal && state.newMealName.isBlank()) {
                    _uiState.update { it.copy(errorMessage = texts.errorMealNameEmpty) }
                    return
                }
            }

            MealPlanWizardStep.MEAL_INGREDIENTS -> {
                if (state.ingredients.isEmpty()) {
                    _uiState.update { it.copy(errorMessage = texts.errorNoIngredients) }
                    return
                }
            }

            MealPlanWizardStep.SCHEDULE -> return
        }

        // when como expresión: el paso siguiente se asigna directo a un val
        val nextStep = when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL -> MealPlanWizardStep.MEAL_INGREDIENTS
            MealPlanWizardStep.MEAL_INGREDIENTS -> MealPlanWizardStep.SCHEDULE
            MealPlanWizardStep.SCHEDULE -> return
        }

        _uiState.update { it.copy(currentStep = nextStep) }
    }

    /**
     * Retrocede un paso en el wizard sin validar.
     *
     * Casos especiales:
     * - Desde **SELECT_MEAL**: cierra el wizard con [navigationController.navigateUp].
     * - Desde **SCHEDULE** con meal existente: salta al paso 1 (el paso 2 nunca se visitó).
     * - Desde **SCHEDULE** con meal nueva: vuelve al paso 2 (el usuario agregó ingredientes).
     */
    private fun recoilWizardStep() {
        val state = _uiState.value

        // Primer paso: cerrar el wizard
        if (state.currentStep == MealPlanWizardStep.SELECT_MEAL) {
            navigationController.navigateUp()
            return
        }

        val previousStep = when (state.currentStep) {
            MealPlanWizardStep.SELECT_MEAL -> return
            MealPlanWizardStep.MEAL_INGREDIENTS -> MealPlanWizardStep.SELECT_MEAL
            MealPlanWizardStep.SCHEDULE -> {
                // Si vino por meal existente (salteó Paso 2), vuelve al Paso 1
                if (state.isCreatingNewMeal) MealPlanWizardStep.MEAL_INGREDIENTS
                else MealPlanWizardStep.SELECT_MEAL
            }
        }

        _uiState.update { it.copy(currentStep = previousStep) }
    }

    // ── Submit ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta el flujo de submit del wizard en tres fases secuenciales.
     * Llamada dentro de un [viewModelScope.launch] desde la acción [CreateMealPlanUiAction.OnSubmit].
     *
     * **Fase 0 — Validación local**: verifica que [CreateMealPlanUiState.mealType] y
     * [CreateMealPlanUiState.selectedDays] estén completos antes de tocar la red.
     * Si alguna condición falla, actualiza `errorMessage` y retorna sin continuar.
     *
     * **Fase 1 — Crear meal custom** (solo si [CreateMealPlanUiState.isCreatingNewMeal]):
     * llama a [createCustomMealUseCase] con los macros acumulados del preview y serving
     * por defecto (`100 g`). Retorna temprano ante cualquier error dejando `isLoading = false`.
     * Si la meal es existente, se usa directamente [CreateMealPlanUiState.selectedMeal].
     *
     * **Fase 2 — Crear el plan**: construye un [ScheduledMealDraft] con la meal obtenida
     * en Fase 1 y los ingredientes del estado, lo envuelve en un [MealPlanDraft] y llama a
     * [createMealPlanUseCase]. En éxito activa [CreateMealPlanUiState.isSuccess], lo que
     * hace que la UI muestre la animación de éxito y luego dispare
     * [CreateMealPlanUiAction.OnSuccessAnimationFinished] para cerrar el wizard.
     */
    private suspend fun submitMealPlan() {
        val state = _uiState.value

        // Fase 0 — Guard clauses: cada validación es independiente
        if (state.mealType == null) {
            _uiState.update { it.copy(errorMessage = texts.errorNoMealType) }
            return
        }
        if (state.selectedDays.isEmpty()) {
            _uiState.update { it.copy(errorMessage = texts.errorNoDays) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        // Fase 1 — Si es meal nueva la persistimos para obtener su ID
        val mealForPlan: MealSummary? = if (state.isCreatingNewMeal) {
            val dto = CreateCustomMealRequestDto(
                name = state.newMealName,
                description = state.newMealDescription.ifBlank { null },
                servingSize = 100.0,
                servingUnit = "g",
                calories = state.previewCalories,
                protein = state.previewProtein,
                carbs = state.previewCarbs,
                fat = state.previewFat,
            )
            when (val result = createCustomMealUseCase.execute(request = dto)) {
                is CreateCustomMealResult.Success -> result.meal
                is CreateCustomMealResult.ServerError -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    return
                }
                CreateCustomMealResult.NetworkError -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = texts.errorNetwork) }
                    return
                }
                CreateCustomMealResult.ValidationError -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = texts.errorMealNameEmpty) }
                    return
                }
            }
        } else {
            state.selectedMeal
        }

        // Fase 2 — Construir el draft y crear el plan
        val scheduledMealDraft = ScheduledMealDraft(
            mealType = state.mealType,
            scheduledTime = state.scheduledTime,
            selectedMeal = mealForPlan,
            customIngredients = state.ingredients,
        )

        val mealPlanDraft = MealPlanDraft(
            name = if (state.isCreatingNewMeal) state.newMealName else state.selectedMeal?.name ?: "",
            description = if (state.isCreatingNewMeal) state.newMealDescription.ifBlank { null } else null,
            scheduledMeals = listOf(scheduledMealDraft),
        )

        when (val result = createMealPlanUseCase.execute(mealPlanDraft = mealPlanDraft)) {
            is CreateMealPlanResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            is CreateMealPlanResult.ServerError -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            CreateMealPlanResult.NetworkError -> _uiState.update { it.copy(isLoading = false, errorMessage = texts.errorNetwork) }
            CreateMealPlanResult.ValidationError -> _uiState.update { it.copy(isLoading = false, errorMessage = texts.errorMealNameEmpty) }
        }
    }

    // ── Helpers — Paso 2: Ingredientes ──────────────────────────────────────

    /** Muestra u oculta el dialog de creación de ingrediente custom. */
    private fun showCreateIngredientDialog(showDialog: Boolean) {
        _uiState.update { it.copy(showCreateIngredientDialog = showDialog) }
    }

    /**
     * Elimina un ingrediente de la lista y recalcula los macros del preview.
     * Si el ID no existe en la lista, no hace nada.
     */
    private fun removeSelectedIngredient(ingredientId: Long) {
        _uiState.update { state ->
            val updated = state.ingredients
                .filter { it.ingredient.id != ingredientId }
                .toImmutableList()
            state.copy(
                ingredients = updated,
                previewCalories = updated.sumOf { it.scaledCalories },
                previewProtein = updated.sumOf { it.scaledProtein },
                previewCarbs = updated.sumOf { it.scaledCarbs },
                previewFat = updated.sumOf { it.scaledFat },
            )
        }
    }

    /**
     * Agrega o actualiza un ingrediente en la lista con la cantidad y unidad confirmadas,
     * y recalcula los macros del preview en consecuencia.
     * Si el ingrediente no existe en el catálogo cargado, la función retorna sin efecto.
     */
    private fun updateIngredientQuantity(ingredientId: Long, quantity: Double, unit: String) {
        val ingredient = _uiState.value.ingredientCatalog.find { it.id == ingredientId } ?: return
        val draft = MealIngredientDraft(ingredient = ingredient, quantity = quantity, unit = unit)

        _uiState.update { state ->
            val updated = (state.ingredients + draft).toImmutableList()
            state.copy(
                ingredients = updated,
                previewCalories = updated.sumOf { it.scaledCalories },
                previewProtein = updated.sumOf { it.scaledProtein },
                previewCarbs = updated.sumOf { it.scaledCarbs },
                previewFat = updated.sumOf { it.scaledFat },
                showQuantityBottomSheet = false,
                editingIngredientId = null,
            )
        }
    }

    /**
     * Abre o cierra el bottom sheet de cantidad y registra qué ingrediente se está editando.
     * Al cerrar, [ingredientId] debe ser `null` para limpiar [CreateMealPlanUiState.editingIngredientId].
     */
    private fun updateStatusQuantityBottomSheet(
        ingredientId: Long?,
        showQuantityBottomSheet: Boolean
    ) {
        _uiState.update {
            it.copy(
                editingIngredientId = ingredientId,
                showQuantityBottomSheet = showQuantityBottomSheet,
            )
        }
    }
}
