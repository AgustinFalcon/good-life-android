# SPEC-012 — Create Meal Plan Screen (Android)

**Versión:** 1.0  
**Estado:** ✅ Completado  
**Última actualización:** 14 de Abril, 2026  
**Relacionado con:** backend `nutrition.spec.md`, `SPEC-011-create-routine-screen.md`

---

## 1. Descripción General

Wizard multi-paso para planificar comidas en el Daily Log.
Accesible desde el FAB (+) del `MainScaffold` → QuickAction "Comida".

El flujo tiene **dos etapas principales**:
1. **Seleccionar o crear una Meal** (receta con ingredientes y macros)
2. **Planificar esa Meal** (días, hora, tipo de comida, fechas)

Al crearse el plan, el backend genera items tipo **MEAL** en el Daily Log para los días configurados.

### Comparativa con otras pantallas de creación

| Feature | Create Task | Create Habit | Create Routine | Create Meal Plan |
|---------|-------------|--------------|----------------|-----------------|
| Formulario | 1 nivel | 1 nivel | 4 niveles anidados | 2 niveles anidados |
| Pasos wizard | 1 | 1 | 4 | 3 |
| Endpoints | 1 (POST) | 1 (POST) | 3 | 3 (GET catálogo + POST meal + POST plan) |
| Paginación | No | No | Sí (ejercicios) | Sí (ingredientes) |
| Macros automáticos | No | No | No | Sí (calcula backend) |
| Creación anidada | No | No | Sí (workouts/exercises/sets) | Sí (meal/ingredients) |

---

## 2. Backend API — Endpoints Consumidos

### 2.1 GET /api/v1/ingredients — Catálogo de ingredientes (paginado)

**Auth:** Bearer token  
**Query params:**

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `search` | `String?` | null | Búsqueda por nombre o marca |
| `page` | `Int` | 0 | Página (0-indexed) |
| `size` | `Int` | 50 | Items por página |

**Response:** `200 OK`
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Pechuga de pollo",
      "brand": null,
      "isGlobal": true,
      "servingSize": 100,
      "servingUnit": "g",
      "calories": 165,
      "protein": 31,
      "carbs": 0,
      "fat": 3.6
    },
    {
      "id": 100,
      "name": "Proteína en polvo",
      "brand": "ON Gold Standard",
      "isGlobal": false,
      "servingSize": 30,
      "servingUnit": "g",
      "calories": 120,
      "protein": 24,
      "carbs": 3,
      "fat": 1
    }
  ]
}
```

**Nota:** La API devuelve globales + custom del usuario mezclados. `isGlobal = false` indica ingrediente custom del usuario.

### 2.2 GET /api/v1/meals — Catálogo de meals (con búsqueda y filtro por tag)

**Auth:** Bearer token  
**Query params:**

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `search` | `String?` | null | Búsqueda por nombre |
| `tag` | `String?` | null | Filtrar por tag (almuerzo, snack, etc.) |

**Response:** `200 OK`
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Arroz con Pollo",
      "description": "Plato clásico latinoamericano",
      "isGlobal": true,
      "tags": ["almuerzo", "proteína"],
      "totalCalories": 450,
      "totalProtein": 35,
      "totalCarbs": 45,
      "totalFat": 12,
      "ingredients": [
        {
          "ingredientId": 1,
          "ingredientName": "Pechuga de pollo",
          "quantity": 150,
          "unit": "g",
          "calories": 247,
          "protein": 46,
          "carbs": 0,
          "fat": 0
        }
      ],
      "instructions": [
        { "step": 1, "description": "Cocinar el arroz" }
      ]
    }
  ]
}
```

### 2.3 POST /api/v1/meals — Crear meal custom

**Auth:** Bearer token  
**Response exitosa:** `201 Created`

**Request body:**
```json
{
  "name": "Smoothie de proteína",
  "description": "Batido post-entreno",
  "tags": ["snack", "proteína", "rápido"],
  "ingredients": [
    { "ingredientId": 100, "quantity": 1, "unit": "scoop" },
    { "ingredientId": 50,  "quantity": 200, "unit": "ml" },
    { "ingredientId": 51,  "quantity": 1, "unit": "unidad" }
  ],
  "instructions": [
    { "step": 1, "description": "Agregar todos los ingredientes a la licuadora" },
    { "step": 2, "description": "Licuar por 30 segundos" }
  ]
}
```

**Campos:**

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `name` | String | Sí | No vacío |
| `description` | String? | No | |
| `tags` | List\<String\>? | No | Ej: ["almuerzo", "keto"] |
| `ingredients` | List | Sí | Al menos 1 ingrediente |
| `ingredients[].ingredientId` | Long | Sí | Ref al catálogo |
| `ingredients[].quantity` | Double | Sí | > 0 |
| `ingredients[].unit` | String | Sí | Ej: "g", "ml", "scoop", "unidad" |
| `instructions` | List? | No | Pasos opcionales de preparación |
| `instructions[].step` | Int | Sí | Número de paso |
| `instructions[].description` | String | Sí | Descripción del paso |

**Response:** Meal creada con macros calculados automáticamente por el backend.

### 2.4 POST /api/v1/ingredients — Crear ingrediente custom

**Auth:** Bearer token  
**Response exitosa:** `201 Created`

```json
{
  "name": "Avena instantánea",
  "brand": "Quaker",
  "servingSize": 40,
  "servingUnit": "g",
  "calories": 152,
  "protein": 5.4,
  "carbs": 27.1,
  "fat": 2.7
}
```

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `name` | String | Sí | |
| `brand` | String? | No | |
| `servingSize` | Double | Sí | Por porción |
| `servingUnit` | String | Sí | "g", "ml", "unidad", etc. |
| `calories` | Double | Sí | Kcal por porción |
| `protein` | Double | Sí | Gramos |
| `carbs` | Double | Sí | Gramos |
| `fat` | Double | Sí | Gramos |

### 2.5 POST /api/v1/meal-plans — Planificar una meal

**Auth:** Bearer token  
**Response exitosa:** `201 Created`

```json
{
  "mealId": 1,
  "daysOfWeek": [1, 3, 5],
  "startDate": "2026-03-10",
  "endDate": null,
  "scheduledTime": "13:00:00",
  "mealType": "LUNCH"
}
```

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `mealId` | Long | Sí | ID de la meal seleccionada/creada |
| `daysOfWeek` | List\<Int\> | Sí | 1=Lunes ... 7=Domingo |
| `startDate` | LocalDate? | No | |
| `endDate` | LocalDate? | No | |
| `scheduledTime` | LocalTime? | No | Hora de la comida |
| `mealType` | Enum | Sí | BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT |

**Response:**
```json
{
  "code": 201,
  "data": {
    "id": 1,
    "mealId": 1,
    "mealName": "Arroz con Pollo",
    "daysOfWeek": [1, 3, 5],
    "scheduledTime": "13:00:00",
    "mealType": "LUNCH",
    "snapshotCalories": 450,
    "snapshotProtein": 35,
    "snapshotCarbs": 45,
    "snapshotFat": 12,
    "isActive": true
  }
}
```

**Dato clave:** El backend guarda un **snapshot** de los macros al momento de crear el plan. Si la meal se edita después, los macros del plan no cambian.

---

## 3. Enums del Backend

### MealType
| Valor | Texto ES | Texto EN | Texto PT |
|-------|----------|----------|----------|
| BREAKFAST | Desayuno | Breakfast | Café da manhã |
| LUNCH | Almuerzo | Lunch | Almoço |
| DINNER | Cena | Dinner | Jantar |
| SNACK | Snack / Colación | Snack | Lanche |
| PRE_WORKOUT | Pre-entreno | Pre-workout | Pré-treino |
| POST_WORKOUT | Post-entreno | Post-workout | Pós-treino |

---

## 4. Flujo de Pantallas (Wizard 3 pasos)

### Diagrama de navegación

```
FAB (+) → QuickAction "Comida"
    │
    ▼
┌──────────────────────────────────────┐
│  PASO 1 — Seleccionar o crear Meal   │
│                                      │
│  Opción A: Buscar en catálogo        │
│  Opción B: Crear nueva meal          │
│  (con ingredientes del catálogo)     │
│                        [Siguiente →] │
└──────────────┬───────────────────────┘
               │ (meal seleccionada)
               ▼
┌──────────────────────────────────────┐
│  PASO 2 — Ingredientes de la Meal    │
│  (solo si creando meal nueva)        │
│                                      │
│  Buscar ingredientes (paginado)      │
│  Configurar cantidad + unidad        │
│  Ver macros calculados en tiempo     │
│  real (localmente con fórmula)       │
│                        [Siguiente →] │
└──────────────┬───────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  PASO 3 — Planificar                 │
│                                      │
│  Tipo de comida (MealType)           │
│  Días de la semana                   │
│  Fecha inicio / Fecha fin (opcional) │
│  Hora (opcional)                     │
│  Vista previa de macros del plan     │
│                      [Crear plan ✓]  │
└──────────────────────────────────────┘
```

**Nota sobre el Paso 2:** Si el usuario selecciona una meal existente del catálogo (Opción A del Paso 1), el Paso 2 se **salta automáticamente** y se va directo al Paso 3. Solo se muestra el Paso 2 cuando el usuario elige **crear una nueva meal** (Opción B).

---

## 5. Wireframes por Paso

### Paso 1A — Catálogo de Meals (buscar y seleccionar)

```
┌──────────────────────────────────────────┐
│  [×]   Nueva Comida        [COMIDA]      │
│  Paso 1 de 3              ████░░░░  33%  │
├──────────────────────────────────────────┤
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🔍 Buscar comida...                 ││
│  └──────────────────────────────────────┘│
│                                          │
│  [Todos] [Desayuno] [Almuerzo] [Cena]    │
│  [Snack] [Pre/Post-entreno]              │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🍽 Arroz con Pollo       [Elegir]  ││
│  │ 450 kcal • 35g P • 45g C • 12g F   ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ 🍳 Omelette de claras     [Elegir] ││
│  │ 210 kcal • 28g P • 2g C • 9g F     ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ 🥤 Smoothie de proteína   [Elegir] ││
│  │ 320 kcal • 30g P • 38g C • 4g F    ││
│  └──────────────────────────────────────┘│
│  ... (scroll / load more) ...            │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │  ✚  Crear nueva comida               ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Descripción:** Catálogo paginado de meals (globales + propias del usuario). Búsqueda por nombre + filtros por MealType como chips. Al tocar "Elegir" en una meal: salta al Paso 3. Al tocar "Crear nueva comida": va al Paso 1B → Paso 2.

---

### Paso 1B — Crear Nueva Meal

```
┌──────────────────────────────────────────┐
│  [←]   Nueva Comida        [COMIDA]      │
│  Paso 1 de 3              ████░░░░  33%  │
├──────────────────────────────────────────┤
│                                          │
│  Nombre de la comida *                   │
│  ┌──────────────────────────────────────┐│
│  │ Ej: Smoothie de proteína            ││
│  └──────────────────────────────────────┘│
│                                          │
│  Descripción (opcional)                  │
│  ┌──────────────────────────────────────┐│
│  │ Ej: Batido post-entreno...          ││
│  └──────────────────────────────────────┘│
│                                          │
│  Tipo de comida *                        │
│  [Desayuno] [Almuerzo] [Cena]            │
│  [Snack] [Pre-entreno] [Post-entreno]    │
│                                          │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │         SIGUIENTE →                  ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Descripción:** Formulario básico de nombre + descripción + tipo. Los ingredientes se agregan en el Paso 2.

---

### Paso 2 — Ingredientes + Macros en tiempo real

```
┌──────────────────────────────────────────┐
│  [←]  Ingredientes         [COMIDA]      │
│  Paso 2 de 3              ████████  66%  │
├──────────────────────────────────────────┤
│                                          │
│  ┌──── Macros totales ─────────────────┐ │
│  │  🔥 420 kcal                        │ │
│  │  💪 35g P  🍞 40g C  🧈 12g G      │ │
│  └─────────────────────────────────────┘ │
│                                          │
│  Mis ingredientes (3)                    │
│  ┌──────────────────────────────────────┐│
│  │ Pechuga de pollo  150g      🗑      ││
│  │ 247 kcal • 46g P • 0g C • 4g G     ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ Arroz blanco      100g      🗑      ││
│  │ 130 kcal • 3g P • 28g C • 0g G     ││
│  └──────────────────────────────────────┘│
│                                          │
│  ── Catálogo de ingredientes ─────────── │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🔍 Buscar ingredientes...           ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ Proteína en polvo (ON)  [Agregar]  ││
│  │ 120 kcal / 30g          custom 👤  ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ Leche descremada        [Agregar]  ││
│  │ 35 kcal / 100ml                     ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐ │
│  │  ✚  Crear ingrediente custom        │ │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘ │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │         SIGUIENTE →                  ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Descripción:**
- Header sticky con macros totales calculados localmente en tiempo real a medida que se agregan ingredientes.
- Lista de ingredientes ya agregados con cantidades y sus macros individuales.
- Catálogo paginado con scroll infinito (igual que ejercicios en Routine).
- Al tocar "Agregar": abre `QuantityBottomSheet` para configurar cantidad y unidad.
- Opción de crear ingrediente custom (dialog o pantalla inline).

---

### Bottom Sheet — Configurar cantidad del ingrediente

```
┌──────────────────────────────────────────┐
│  ──── (drag handle) ────                 │
│                                          │
│  🥦 Brócoli                              │
│     34 kcal / 100g                       │
│                                          │
│  Cantidad                                │
│  ┌──────────────────────────────────────┐│
│  │            100                       ││
│  └──────────────────────────────────────┘│
│                                          │
│  Unidad                                  │
│  [g] [kg] [ml] [L] [taza] [cda] [unidad] │
│                                          │
│  ┌ ─ Preview ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐ │
│  │  34 kcal • 2.8g P • 7g C • 0.4g G  │ │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘ │
│                                          │
│  ┌────────────┐  ┌──────────────────────┐│
│  │  Cancelar  │  │     CONFIRMAR        ││
│  └────────────┘  └──────────────────────┘│
└──────────────────────────────────────────┘
```

**Descripción:** Input numérico para cantidad + chips de unidad + preview de macros escalados en tiempo real según la cantidad ingresada.

---

### Dialog — Crear ingrediente custom

```
┌──────────────────────────────────────────┐
│  Nuevo Ingrediente                       │
├──────────────────────────────────────────┤
│                                          │
│  Nombre *                                │
│  ┌──────────────────────────────────────┐│
│  │ Ej: Avena instantánea               ││
│  └──────────────────────────────────────┘│
│                                          │
│  Marca (opcional)                        │
│  ┌──────────────────────────────────────┐│
│  │ Ej: Quaker                          ││
│  └──────────────────────────────────────┘│
│                                          │
│  Porción de referencia                   │
│  ┌────────────┐  [g] [ml] [unidad]       │
│  │    100     │                          │
│  └────────────┘                          │
│                                          │
│  Macros por porción                      │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐ │
│  │ 152  │  │ 5.4  │  │ 27.1 │  │ 2.7  │ │
│  │ kcal │  │  P   │  │  C   │  │  G   │ │
│  └──────┘  └──────┘  └──────┘  └──────┘ │
│                                          │
│  ┌────────────┐  ┌──────────────────────┐│
│  │  Cancelar  │  │      GUARDAR         ││
│  └────────────┘  └──────────────────────┘│
└──────────────────────────────────────────┘
```

---

### Paso 3 — Planificar

```
┌──────────────────────────────────────────┐
│  [←]   Planificar           [COMIDA]     │
│  Paso 3 de 3              ████████████ ✓ │
├──────────────────────────────────────────┤
│                                          │
│  ┌──── Resumen de la comida ───────────┐ │
│  │  🍽 Arroz con Pollo                 │ │
│  │  450 kcal • 35g P • 45g C • 12g G  │ │
│  └─────────────────────────────────────┘ │
│                                          │
│  Tipo de comida *                        │
│  [Desayuno] [Almuerzo✓] [Cena]           │
│  [Snack] [Pre-entreno] [Post-entreno]    │
│                                          │
│  Días de la semana *                     │
│  ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐   │
│  │L │ │M │ │X │ │J │ │V │ │S │ │D │   │
│  └──┘ └──┘ └──┘ └──┘ └──┘ └──┘ └──┘   │
│                                          │
│  Fecha de inicio                         │
│  ┌──────────────────────────────────────┐│
│  │ 📅 10 de marzo, 2026                ││
│  └──────────────────────────────────────┘│
│                                          │
│  Fecha de fin         [toggle sin fin]   │
│                                          │
│  Hora                 [toggle sin hora]  │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │           CREAR PLAN ✓               ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

---

## 6. Estado UI — UiState

### CreateMealPlanUiState

```kotlin
@Stable
data class CreateMealPlanUiState(
    val currentStep: MealPlanWizardStep = MealPlanWizardStep.SELECT_MEAL,

    // ── Paso 1A: Catálogo de meals ──
    val mealCatalog: List<MealSummary> = emptyList(),
    val mealSearchQuery: String = "",
    val selectedMealTypeFilter: MealType? = null,
    val isLoadingMeals: Boolean = false,
    val mealCatalogPage: Int = 0,
    val mealCatalogHasMore: Boolean = true,

    // ── Paso 1B: Crear nueva meal ──
    val isCreatingNewMeal: Boolean = false,
    val newMealName: String = "",
    val newMealDescription: String = "",

    // ── Meal seleccionada/creada ──
    val selectedMeal: MealSummary? = null,  // si eligió del catálogo
    val selectedMealId: Long? = null,        // se llena después del POST /meals

    // ── Paso 2: Ingredientes ──
    val ingredients: List<MealIngredientDraft> = emptyList(),
    val ingredientCatalog: List<IngredientMaster> = emptyList(),
    val ingredientSearchQuery: String = "",
    val isLoadingIngredients: Boolean = false,
    val ingredientPage: Int = 0,
    val ingredientHasMore: Boolean = true,

    // Macros calculados localmente (sin esperar al backend)
    val previewCalories: Double = 0.0,
    val previewProtein: Double = 0.0,
    val previewCarbs: Double = 0.0,
    val previewFat: Double = 0.0,

    // ── Paso 3: Planificación ──
    val mealType: MealType? = null,
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val hasStartDate: Boolean = false,
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,

    // ── Estado general ──
    val showQuantityBottomSheet: Boolean = false,
    val editingIngredientId: Long? = null,  // ingrediente que se está configurando
    val showCreateIngredientDialog: Boolean = false,
    val showTimePicker: Boolean = false,
    val activeDatePickerField: MealDatePickerField? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

enum class MealPlanWizardStep { SELECT_MEAL, MEAL_INGREDIENTS, SCHEDULE }
enum class MealDatePickerField { START_DATE, END_DATE }
```

### MealIngredientDraft (modelo en memoria)

```kotlin
data class MealIngredientDraft(
    val ingredientId: Long,
    val ingredientName: String,
    val brand: String?,
    val servingSize: Double,       // porción de referencia del catálogo
    val servingUnit: String,       // unidad de la porción de referencia
    val caloriesPerServing: Double,
    val proteinPerServing: Double,
    val carbsPerServing: Double,
    val fatPerServing: Double,

    // Lo que el usuario configura
    val quantity: Double,           // cantidad que el usuario ingresa
    val unit: String,               // unidad que el usuario selecciona

    // Macros calculados localmente: (quantity / servingSize) * macrosPerServing
    val calculatedCalories: Double,
    val calculatedProtein: Double,
    val calculatedCarbs: Double,
    val calculatedFat: Double,
)
```

---

## 7. Acciones UI

```kotlin
sealed interface CreateMealPlanUiAction {
    // ── Navegación del wizard ──
    data object OnNextStep : CreateMealPlanUiAction
    data object OnPreviousStep : CreateMealPlanUiAction
    data object OnDismiss : CreateMealPlanUiAction

    // ── Paso 1A: Catálogo de meals ──
    data class OnMealSearchQueryChange(val query: String) : CreateMealPlanUiAction
    data class OnMealTypeFilter(val mealType: MealType?) : CreateMealPlanUiAction
    data object OnLoadMoreMeals : CreateMealPlanUiAction
    data class OnSelectMeal(val meal: MealSummary) : CreateMealPlanUiAction

    // ── Paso 1B: Crear nueva meal ──
    data object OnCreateNewMeal : CreateMealPlanUiAction
    data class OnNewMealNameChange(val name: String) : CreateMealPlanUiAction
    data class OnNewMealDescriptionChange(val desc: String) : CreateMealPlanUiAction

    // ── Paso 2: Ingredientes ──
    data class OnIngredientSearchQueryChange(val query: String) : CreateMealPlanUiAction
    data object OnLoadMoreIngredients : CreateMealPlanUiAction
    data class OnOpenQuantityBottomSheet(val ingredientId: Long) : CreateMealPlanUiAction
    data object OnDismissQuantityBottomSheet : CreateMealPlanUiAction
    data class OnConfirmIngredientQuantity(
        val ingredientId: Long,
        val quantity: Double,
        val unit: String,
    ) : CreateMealPlanUiAction
    data class OnRemoveIngredient(val ingredientId: Long) : CreateMealPlanUiAction
    data object OnShowCreateIngredientDialog : CreateMealPlanUiAction
    data object OnDismissCreateIngredientDialog : CreateMealPlanUiAction
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

    // ── Paso 3: Planificación ──
    data class OnMealTypeSelected(val mealType: MealType) : CreateMealPlanUiAction
    data class OnDayToggled(val day: DayOfWeek) : CreateMealPlanUiAction
    data object OnHasStartDateToggle : CreateMealPlanUiAction
    data object OnHasEndDateToggle : CreateMealPlanUiAction
    data object OnHasTimeToggle : CreateMealPlanUiAction
    data class OnDatePickerOpen(val field: MealDatePickerField) : CreateMealPlanUiAction
    data object OnDatePickerDismiss : CreateMealPlanUiAction
    data class OnDateSelected(val field: MealDatePickerField, val date: LocalDate) : CreateMealPlanUiAction
    data object OnTimePickerOpen : CreateMealPlanUiAction
    data object OnTimePickerDismiss : CreateMealPlanUiAction
    data class OnTimeSelected(val time: LocalTime) : CreateMealPlanUiAction

    // ── Submit ──
    data object OnSubmit : CreateMealPlanUiAction
    data object OnErrorDismissed : CreateMealPlanUiAction
    data object OnSuccessAnimationFinished : CreateMealPlanUiAction
}
```

---

## 8. Validaciones Frontend

### Paso 1A → Elegir meal del catálogo
| Regla | Condición |
|-------|-----------|
| Meal seleccionada | `selectedMeal != null` |

### Paso 1B → Crear nueva meal (nombre)
| Regla | Condición |
|-------|-----------|
| Nombre requerido | `newMealName.isNotBlank()` |

### Paso 2 → Siguiente (ingredientes)
| Regla | Condición |
|-------|-----------|
| Al menos 1 ingrediente | `ingredients.isNotEmpty()` |
| Cada ingrediente con cantidad > 0 | `ingredients.all { it.quantity > 0 }` |

### QuantityBottomSheet → Confirmar
| Regla | Condición |
|-------|-----------|
| Cantidad > 0 | `quantity > 0` |
| Unidad no vacía | `unit.isNotBlank()` |

### Paso 3 → Crear plan
| Regla | Condición |
|-------|-----------|
| Tipo de comida seleccionado | `mealType != null` |
| Al menos 1 día | `selectedDays.isNotEmpty()` |
| endDate >= startDate | `!hasEndDate \|\| endDate >= startDate` |

---

## 9. Lógica de Macros en Tiempo Real

Los macros NO se calculan pidiendo al backend — se calculan **localmente** usando la misma fórmula que el backend:

```kotlin
// Al agregar un ingrediente con cantidad
fun calculateIngredientMacros(
    ingredient: IngredientMaster,
    quantity: Double,
    unit: String,
): MacroResult {
    // Escalar en base a la porción de referencia del catálogo
    val multiplier = quantity / ingredient.servingSize
    return MacroResult(
        calories = (ingredient.calories * multiplier).roundTo(1),
        protein  = (ingredient.protein  * multiplier).roundTo(1),
        carbs    = (ingredient.carbs    * multiplier).roundTo(1),
        fat      = (ingredient.fat      * multiplier).roundTo(1),
    )
}

// Total del meal = suma de todos los ingredientes
val totalCalories = ingredients.sumOf { it.calculatedCalories }
val totalProtein  = ingredients.sumOf { it.calculatedProtein }
val totalCarbs    = ingredients.sumOf { it.calculatedCarbs }
val totalFat      = ingredients.sumOf { it.calculatedFat }
```

**Nota:** La unidad que el usuario selecciona debe ser la **misma que la porción de referencia del ingrediente** para que el cálculo sea correcto. Si el ingrediente tiene `servingUnit = "g"` y el usuario quiere poner en "kg", hay que hacer conversión. Para v1.0 se usará la misma unidad que trae el catálogo, sin conversión entre unidades.

---

## 10. Flujo de Submit

```
Usuario toca "Crear Plan"
       ↓
Validar: mealType, selectedDays no vacíos
       ↓
if (isCreatingNewMeal):
    ① POST /api/v1/meals  →  obtener mealId
    ↓ si falla → mostrar error, no continuar
else:
    mealId = selectedMeal.id
       ↓
② POST /api/v1/meal-plans  con mealId + schedule
       ↓
isSuccess = true → SuccessDialog → navigateUp()
```

---

## 11. Arquitectura de Archivos

```
domain/model/nutrition/
    MealType.kt                          enum (BREAKFAST, LUNCH, etc.)
    MealSummary.kt                       modelo del catálogo (con macros totales)
    MealDetail.kt                        modelo completo (con ingredientes)
    IngredientMaster.kt                  modelo de ingrediente del catálogo
    MealIngredientDraft.kt               modelo en memoria durante creación
    MealPlan.kt                          modelo del plan creado

domain/repository/
    NutritionRepository.kt              interface (meals + meal-plans)
    IngredientRepository.kt             interface (ingredientes)

domain/usecase/nutrition/
    GetMealsUseCase.kt                  GET /api/v1/meals (paginado/búsqueda)
    CreateMealUseCase.kt                POST /api/v1/meals (validaciones)
    CreateMealPlanUseCase.kt            POST /api/v1/meal-plans (validaciones)
    GetIngredientsUseCase.kt            GET /api/v1/ingredients (paginado)
    CreateIngredientUseCase.kt          POST /api/v1/ingredients
    result/
        CreateMealResult.kt
        CreateMealPlanResult.kt
        GetMealsResult.kt
        GetIngredientsResult.kt
        CreateIngredientResult.kt

data/remote/api/nutrition/
    NutritionApiService.kt              todos los endpoints de meals + meal-plans
    IngredientApiService.kt             endpoints de ingredientes

data/remote/dto/request/nutrition/
    CreateMealRequest.kt
    CreateMealPlanRequest.kt
    CreateIngredientRequest.kt

data/remote/dto/response/nutrition/
    IngredientResponse.kt
    MealSummaryResponse.kt
    MealDetailResponse.kt               con MealIngredientResponse + MealInstructionResponse
    MealPlanResponse.kt

data/remote/datasource/
    NutritionRemoteDataSource.kt
    IngredientRemoteDataSource.kt

data/repository/
    NutritionRepositoryImpl.kt
    IngredientRepositoryImpl.kt

presentation/screen/add/mealplan/
    model/
        CreateMealPlanUiState.kt
        CreateMealPlanUiAction.kt
    CreateMealPlanViewModel.kt
    steps/
        SelectMealStep.kt               Paso 1A (catálogo) + 1B (crear nueva)
        MealIngredientsStep.kt          Paso 2 (ingredientes + macros)
        ScheduleMealStep.kt             Paso 3 (planificación)
        PreviewHelpers.kt               mocks para @Preview
    CreateMealPlanScreen.kt             Orchestrador de pasos
    CreateMealPlanScreenOwner.kt        VM + dialogs + bottom sheets

di/
    NutritionModule.kt                  Koin module
```

---

## 12. Colores y Tokens de Diseño

El módulo de nutrición usa tokens específicos distintos a los de Workout (verde) y Habit (azul):

| Elemento | Token sugerido | Hex sugerido |
|----------|---------------|--------------|
| Badge "COMIDA" fondo | `NutritionBackground` | `#FFF3E0` (naranja muy suave) |
| Badge "COMIDA" texto | `NutritionAccent` | `#F57C00` (naranja) |
| Chips MealType seleccionado | `NutritionAccent` | `#F57C00` |
| Barra de progreso wizard | `NutritionAccent` | `#F57C00` |
| Card macros (calorías) | `CaloriesColor` | `#FF7043` |
| Card macros (proteína) | `ProteinColor` | `#42A5F5` (azul) |
| Card macros (carbos) | `CarbsColor` | `#FFCA28` (amarillo) |
| Card macros (grasas) | `FatColor` | `#AB47BC` (violeta) |

**Nota:** Estos tokens deben agregarse a `Color.kt` antes de implementar.

---

## 13. Paginación

La paginación de ingredientes y meals sigue el **mismo patrón manual** que el catálogo de ejercicios en SPEC-011 (Sección 11). Se usa `derivedStateOf` + `LaunchedEffect` para detectar el final de la lista y disparar `OnLoadMoreIngredients` / `OnLoadMoreMeals`. Ver SPEC-011 Sección 11.2 para el detalle técnico completo.

---

## 14. Koin Module

```kotlin
val nutritionModule = module {
    // Data Sources
    factory { NutritionRemoteDataSource(apiService = get()) }
    factory { IngredientRemoteDataSource(apiService = get()) }

    // Repositories
    single<NutritionRepository> { NutritionRepositoryImpl(remoteDataSource = get()) }
    single<IngredientRepository> { IngredientRepositoryImpl(remoteDataSource = get()) }

    // UseCases
    factory { GetMealsUseCase(repository = get(), dispatcher = get()) }
    factory { CreateMealUseCase(repository = get(), dispatcher = get()) }
    factory { CreateMealPlanUseCase(repository = get(), dispatcher = get()) }
    factory { GetIngredientsUseCase(repository = get(), dispatcher = get()) }
    factory { CreateIngredientUseCase(repository = get(), dispatcher = get()) }

    // ViewModel
    viewModel {
        CreateMealPlanViewModel(
            navigationController = get(),
            language = get(),
            getMealsUseCase = get(),
            createMealUseCase = get(),
            createMealPlanUseCase = get(),
            getIngredientsUseCase = get(),
            createIngredientUseCase = get(),
        )
    }
}
```

---

## 15. Orden de Implementación Sugerido

| Fase | Archivos | Descripción |
|------|----------|-------------|
| 1 | Domain models + enums | MealType, MealSummary, IngredientMaster, MealIngredientDraft, MealPlan |
| 2 | Domain repos + use cases | Interfaces + 5 UseCases + 5 Result types |
| 3 | Data DTOs | Request/Response para meals + ingredients + meal-plans |
| 4 | Data layer | ApiServices, DataSources, RepositoryImpls |
| 5 | Tokens de color | NutritionAccent, NutritionBackground, CaloriesColor, ProteinColor, CarbsColor, FatColor en Color.kt |
| 6 | Localización | CreateMealPlanTexts, MealTypeTexts en 3 idiomas |
| 7 | Presentation models | UiState, UiAction, MealIngredientDraft |
| 8 | ViewModel | Wizard logic, catálogos paginados, cálculo de macros local, submit |
| 9 | Steps (3 pasos) | SelectMealStep, MealIngredientsStep, ScheduleMealStep |
| 10 | Bottom Sheet + Dialog | QuantityBottomSheet, CreateIngredientDialog |
| 11 | Screen + Owner | CreateMealPlanScreen (orchestrador) + Owner (dialogs/pickers) |
| 12 | DI + Navigation | NutritionModule, AppRoute.CreateMealPlan, AppGraph, QuickAction |
| 13 | Tests | UseCases + ViewModel tests |

---

## 16. Edge Cases y Decisiones de Diseño

| Caso | Decisión |
|------|----------|
| Usuario selecciona meal existente y va al Paso 3 | Saltear Paso 2. `isCreatingNewMeal = false` |
| Misma unidad que porción de referencia | v1.0: solo mostrar la unidad del catálogo. Conversión de unidades en v2.0 |
| Ingrediente ya agregado al volver a buscarlo | Mostrar ✓ en lugar de "Agregar" (igual que ejercicios en Routine) |
| Backend falla al crear la meal pero el plan sí | No debería ocurrir (el plan necesita el mealId). Si falla la meal, no crear el plan |
| Macros del catálogo en valor 0 | Mostrar igualmente, es válido (ej: agua = 0 calorías) |
| Tags en las meals | Son strings libres, se muestran como chips de filtro. No hay enum predefinido |

---

*Spec creado el 09/03/2026.*

---

## 17. Mejoras Futuras

### 17.1 — "Usar como plantilla" al seleccionar una meal del catálogo

**Idea:** Al seleccionar una meal existente del catálogo (Paso 1A), en lugar de ir directo al Paso 3, el usuario podría elegir **"Usar como base"** para editarla (modificar ingredientes, imagen, descripción) y guardarla como una nueva meal custom propia, usando la original como punto de partida.

**Flujo propuesto:**
```
Paso 1A — Usuario toca "Elegir" en una meal del catálogo
    │
    ├─ [Planificar tal como está]  →  saltar al Paso 3 (flujo actual)
    │
    └─ [Usar como base / Personalizar]
            │
            ▼
        Paso 1B (precargado con nombre, descripción y tipo de la meal original)
            │
            ▼
        Paso 2 (ingredientes precargados de la meal original, editables)
            │
            ▼
        Al confirmar: se crea una nueva meal custom del usuario
            │
            ▼
        Paso 3 — planificar la nueva meal
```

**Impacto backend:** ninguno — sigue usando `POST /api/v1/meals` con los datos modificados.

**Impacto Android:**
- `UiState` necesita `templateMeal: MealDetail?` para precargar el form (requiere `GET /api/v1/meals/{id}`)
- El diálogo de elección puede ser un simple `AlertDialog` o un `BottomSheet` con 2 opciones
- Agregar acción `OnUseAsTemplate(meal: MealSummary)` al `CreateMealPlanUiAction`

**Prioridad:** Media — añade valor UX sin complejidad de backend. Implementar en v1.1 una vez el flujo base esté estable.
