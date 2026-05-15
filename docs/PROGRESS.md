# Progreso de Implementación - GoodLife Android

> Última actualización: 2026-05-14

**Inventario técnico:** [IMPLEMENTATION-STATUS.md](IMPLEMENTATION-STATUS.md)

## 📊 Estado General

| Feature | Estado | Progreso |
|---------|--------|----------|
| Splash Screen | ✅ Completado | 100% |
| Login Screen | ✅ Completado | 100% |
| Register Screen | ✅ Completado | 100% |
| Biometric Login (refactored) | ✅ Completado | 100% |
| DateProvider (SPEC-004) | ✅ Completado | 100% |
| Network Service (SPEC-005) | ✅ Completado | 100% |
| Localización AppLanguage (SPEC-007) | ✅ Completado | 100% |
| **FAB + modal creación (SPEC-009 core)** | ✅ Completado | ~90% |
| Main Scaffold (SPEC-003) | 🚧 En Progreso | ~90% |
| **Daily Tab — Arquitectura + SWR** | ✅ Completado | 100% |
| **Daily Tab — DailyItemCard** | ✅ Completado | 100% |
| **Create Task (SPEC-010)** | ✅ Completado | 100% |
| **SWR Fase 1 (SPEC-006)** | ✅ Completado | 100% |
| **Unit Tests** | ✅ En evolución | `app/src/test`: 12 archivos `.kt` (UseCases + ViewModels + auth) |
| **Infraestructura (EncryptedSP, ImmutableList, @Stable)** | ✅ Completado | 100% |
| **Create Habit** | ✅ Completado | 100% |
| **HabitLogSummaryDto fix + DTOs defensivos** | ✅ Completado | 100% |
| **Create Routine (SPEC-011)** | ✅ Completado | 100% |
| **Create Meal Plan (SPEC-012)** | ✅ Completado | 100% |
| Workouts Tab | ⏸️ Pendiente | 0% |
| Meals Tab | ⏸️ Pendiente | 0% |
| More/Settings Tab | ⏸️ Pendiente | 0% |
| **SWR Fase 2 — cola escritura offline (SPEC-006)** | 📝 Planificado | 0% |
| Notificaciones + Deep Links (SPEC-008) | 📝 Planificado | 0% |

---

## ✅ Sesión 2026-04-14 — Create Meal Plan Screen (SPEC-012)

### Resumen de la sesión

Implementación completa del wizard multi-paso para planificar comidas (SPEC-012). Incluye catálogo
paginado de ingredientes y meals, creación de ingredientes custom, cálculo de macros en tiempo real,
y scheduling con tipo de comida, días, fecha y hora.

### Archivos creados

#### Domain Layer
- `domain/model/nutrition/` — `Ingredient`, `IngredientEntry`, `Meal`, `MealSummary`, `MealPlan`, `MealPlanDraft`, `ScheduledMealDraft`, `PortionUnit`
- `domain/repository/` — `IngredientRepository`, `MealRepository`, `MealPlanRepository`
- `domain/usecase/nutrition/` — 5 UseCases: `GetIngredientCatalog`, `GetMealCatalog`, `CreateCustomIngredient`, `CreateCustomMeal`, `CreateMealPlan`
- `domain/usecase/nutrition/result/` — 5 Result types con `ServerError(val message: String)`

#### Data Layer
- `data/remote/api/nutrition/` — `IngredientApiService`, `MealApiService`, `MealPlanApiService`
- `data/remote/datasource/` — 3 DataSources
- `data/remote/dto/request/nutrition/` — 3 Request DTOs
- `data/remote/dto/response/nutrition/` — DTOs defensivos con mappers
- `data/repository/` — 3 `*RepositoryImpl`

#### Presentation Layer (12 archivos)
- `presentation/screen/add/mealplan/model/CreateMealPlanUiState.kt` — `@Stable`, todos los campos del wizard
- `presentation/screen/add/mealplan/model/CreateMealPlanUiAction.kt` — sealed interface
- `presentation/screen/add/mealplan/model/MealPlanWizardStep.kt` — 3 pasos del wizard
- `presentation/screen/add/mealplan/model/MealDatePickerField.kt` — enum START_DATE / END_DATE
- `presentation/screen/add/mealplan/CreateMealPlanViewModel.kt` — 5 UseCases, paginación, guards de validación, KDoc completo
- `presentation/screen/add/mealplan/CreateMealPlanScreen.kt` — AnimatedContent con slide horizontal entre pasos
- `presentation/screen/add/mealplan/CreateMealPlanScreenOwner.kt` — todos los overlays conectados
- `presentation/screen/add/mealplan/steps/SelectMealStep.kt` — catálogo de meals + nuevo meal
- `presentation/screen/add/mealplan/steps/MealIngredientsStep.kt` — macros summary + catálogo de ingredientes
- `presentation/screen/add/mealplan/steps/ScheduleStep.kt` — tipo de comida, días, fechas, hora
- `presentation/screen/add/mealplan/steps/PreviewHelpers.kt` — helpers para `@Preview`

#### Componentes de Nutrición (7 archivos en `presentation/components/nutrition/`)
- `CreateIngredientDialog.kt` — ModalBottomSheet con campos nombre/marca, selector unidad, cards de macros con `BasicTextField` + placeholder overlay
- `QuantityBottomSheet.kt` — selector de cantidad con cálculo de macros proporcional en tiempo real
- `MealCatalogItem.kt`, `IngredientCatalogSection.kt`, `MyIngredientsSection.kt`, `MacrosSummaryCard.kt`, `MealHeaderCard.kt`

#### DI + Navigation + Localización
- `di/NutritionModule.kt` — DataSources, Repositories, UseCases, ViewModel
- `NetworkModule.kt` — 3 nuevos ApiServices registrados
- `AppRoute.CreateMealPlan` + `AppGraph` con `CreateMealPlanScreenOwner`
- `MainScaffoldViewModel` — FAB Meal → CreateMealPlan
- `CreateMealPlanTexts` en `UiTexts.kt` + traducciones ES/EN/PT

### Decisiones técnicas

1. **Submit routing en Screen**: `WizardBottomBarComponent` despacha `OnSubmit` en paso SCHEDULE y `OnNextStep` en los demás; `isLoading` y `primaryEnabled` conectados al `uiState`
2. **Guards independientes en `submitMealPlan()`**: cada validación tiene su propio `if (...) { update; return }` — no acumuladores que se sobreescriban
3. **`it.copy(...)` en `_uiState.update {}`**: evita usar estado stale capturado antes de llamadas async
4. **`ServerError(val message: String)`**: los 3 result types corregidos de `data object` a `data class`; UseCases pasan `ex.message`
5. **`BasicTextField` + placeholder overlay en `CreateIngredientDialog`**: `OutlinedTextField` tiene borde visual no deseado; `BasicTextField` con `Box {}` condicional para placeholder da control total
6. **`PortionUnit.entries`** para mostrar las 3 unidades en `CreateIngredientDialog` (GRAM, MILLILITER, PIECE)

### Bugs críticos corregidos

- Submit nunca se ejecutaba: el botón del `WizardBottomBarComponent` siempre despachaba `OnNextStep` en todos los pasos
- `state.copy(...)` stale en `submitMealPlan()` → corregido a `it.copy(...)`
- Guards que se sobreescribían → guards independientes con `return`
- `ServerError` sin campo `message` en los 3 result types

---

## ✅ Sesión 2026-03-11 — Create Routine Screen (SPEC-011)

### Resumen de la sesión

Implementación completa del wizard multi-paso para crear rutinas de entrenamiento (SPEC-011).
Incluye catálogo paginado de ejercicios del backend, configuración de sets por ejercicio,
y activación opcional de rutina.

### Archivos creados

#### Domain Layer (8 archivos)
- `domain/model/training/DifficultyLevel.kt` — enum
- `domain/model/training/GoalType.kt` — enum
- `domain/model/training/MuscleGroup.kt` — modelo de grupo muscular
- `domain/model/training/ExerciseMaster.kt` — modelo de ejercicio del catálogo
- `domain/model/training/Routine.kt` — modelo completo con Workout/WorkoutExercise/WorkoutSet
- `domain/model/training/RoutineDraft.kt` — modelos draft (WorkoutDraft/ExerciseDraft/SetDraft)
- `domain/repository/TrainingCatalogRepository.kt` — interface catálogo
- `domain/repository/RoutineRepository.kt` — interface rutinas
- `domain/usecase/routine/` — 4 UseCases + 4 Result types
- `core/pagination/PageResult.kt` — genérico reutilizable para paginación

#### Data Layer (8 archivos)
- `data/remote/api/training/TrainingCatalogApiService.kt` — Retrofit GET endpoints
- `data/remote/api/training/RoutineApiService.kt` — Retrofit POST/PATCH endpoints
- `data/remote/dto/request/routine/CreateRoutineRequest.kt` — DTOs anidados de request
- `data/remote/dto/response/training/` — 4 archivos de DTOs defensivos con mappers
- `data/remote/datasource/TrainingCatalogRemoteDataSource.kt`
- `data/remote/datasource/RoutineRemoteDataSource.kt`
- `data/repository/TrainingCatalogRepositoryImpl.kt`
- `data/repository/RoutineRepositoryImpl.kt`

#### Presentation Layer (10 archivos)
- `presentation/screen/add/routine/model/CreateRoutineUiState.kt` — @Stable, 4 pasos wizard
- `presentation/screen/add/routine/model/CreateRoutineUiAction.kt` — sealed interface
- `presentation/screen/add/routine/CreateRoutineViewModel.kt` — 4 UseCases, paginación
- `presentation/screen/add/routine/CreateRoutineScreen.kt` — AnimatedContent orchestrador
- `presentation/screen/add/routine/CreateRoutineScreenOwner.kt` — DI + dialogs + bottom sheets
- `presentation/screen/add/routine/steps/RoutineInfoStep.kt` — Paso 1
- `presentation/screen/add/routine/steps/WorkoutsStep.kt` — Paso 2
- `presentation/screen/add/routine/steps/WorkoutExercisesStep.kt` — Paso 3 con scroll infinito
- `presentation/screen/add/routine/steps/RoutineSummaryStep.kt` — Paso 4
- `presentation/screen/add/routine/steps/PreviewHelpers.kt` — textos mock para @Preview

#### DI + Navigation + Localización
- `di/RoutineModule.kt` — DataSources, Repositories, UseCases, ViewModel
- `NetworkModule.kt` — TrainingCatalogApiService + RoutineApiService
- `AppRoute.CreateRoutine` — nueva ruta
- `AppGraph` — composable con CreateRoutineScreenOwner
- `MainScaffoldViewModel` — FAB Workout → CreateRoutine
- `CreateRoutineTexts` en UiTexts.kt + Spanish/English/Portuguese

#### Tests (4 archivos)
- `fake/FakeRoutineRepository.kt`
- `fake/FakeTrainingCatalogRepository.kt`
- `domain/usecase/routine/CreateRoutineUseCaseTest.kt` — 11 tests
- `presentation/screen/add/routine/CreateRoutineViewModelTest.kt` — 25 tests

### Decisiones técnicas

1. **Wizard con AnimatedContent**: Slide horizontal entre pasos, manejado por un único ViewModel
2. **DTOs defensivos**: Todos los campos nullable con defaults para evitar crashes por cambios del backend
3. **PageResult genérico**: Movido a `core/pagination/` para reutilización en WorkoutsTab y otros módulos
4. **Draft models**: WorkoutDraft/ExerciseDraft/SetDraft en domain — representan datos pre-persistencia
5. **Scroll infinito**: LazyColumn detecta fin de lista y carga más ejercicios automáticamente
6. **SetEditorBottomSheet**: Editor de sets con mutableStateListOf local antes de confirmar

### Pendiente (próxima iteración)

- Implementar lógica de `activateRoutineUseCase` tras creación exitosa cuando el toggle está activado
- Pantalla de detalle de item (UpdateItemStatus) para registrar progreso gradual de habits

---

## ✅ Sesión 2026-02-24 — Refactoring arquitectónico + Daily ItemCard

### Resumen de la sesión

Sesión enfocada en resolver deuda técnica identificada en el diagnóstico inicial, homogenizar
decisiones arquitectónicas a lo largo del proyecto, y completar el diseño visual de `DailyItemCard`.

---

### 1. Eliminación de `Result.Loading`

**Problema:** `Result.Loading` existía como rama en el sealed class `Result<T>`, lo cual no tiene
sentido semántico para `suspend fun` — una función suspend no puede estar "cargando".

**Decisión:** `Loading` es exclusivamente un estado de **UI** (`DailyUiState.Loading`, etc.).
El ViewModel lo setea *antes* de llamar al UseCase, no como resultado del mismo.

**Archivos modificados:**
- `core/result/Result.kt` — eliminado `data object Loading`, `val isLoading`, `inline fun onLoading`
- `domain/usecase/daily/GetDailyItemsUseCase.kt` — eliminada rama `is Result.Loading`
- `domain/usecase/daily/UpdateItemStatusUseCase.kt` — eliminada rama `is Result.Loading`
- `domain/usecase/login/LoginUseCase.kt` — eliminada rama `is Result.Loading`
- `domain/usecase/register/RegisterUseCase.kt` — eliminada rama `is Result.Loading`
- `data/repository/AuthRepositoryImpl.kt` — eliminadas ramas `is Result.Loading` en `login()` y `register()`
- `presentation/screen/home/HomeViewModel.kt` — eliminada rama `is Result.Loading`

---

### 2. Ownership del Dispatcher

**Decisión establecida:** El UseCase es dueño del `withContext(dispatcher.io)`.
El ViewModel solo usa `viewModelScope.launch` sin especificar dispatcher.

**Archivos modificados:**
- `LoginUseCase.kt` — añadido `dispatcher: DispatcherProvider` y `withContext(dispatcher.io)`
- `RegisterUseCase.kt` — añadido `dispatcher: DispatcherProvider` y `withContext(dispatcher.io)`
- `DailyTabViewModel.kt` — removido `dispatcherProvider` del constructor

---

### 3. Result Types — Option B (semánticos)

**Decisión:** Los UseCases retornan tipos de resultado específicos (no genéricos).
Permite exhaustividad en tiempo de compilación sin castear `ApiException` en el ViewModel.

**Archivos refactorizados:**

`GetDailyItemsResult`:
```kotlin
sealed interface GetDailyItemsResult {
    data class Success(val dailyLog: DailyLog) : GetDailyItemsResult
    data object NotFound : GetDailyItemsResult
    data class ServerError(val message: String) : GetDailyItemsResult
    data object NetworkError : GetDailyItemsResult
}
```

`UpdateItemStatusResult`:
```kotlin
sealed interface UpdateItemStatusResult {
    data class Success(val dailyLog: DailyLog) : UpdateItemStatusResult
    data object NotFound : UpdateItemStatusResult
    data class ServerError(val message: String) : UpdateItemStatusResult
    data object NetworkError : UpdateItemStatusResult
}
```

---

### 4. Split de GoodLifeApiService

**Problema:** `GoodLifeApiService` era monolítica y mezclaba Auth y Daily.

**Solución:**
- `data/remote/api/auth/AuthApiService.kt` — endpoints de autenticación
- `data/remote/api/daily/DailyApiService.kt` — endpoints del daily log
- `GoodLifeApiService.kt` — marcado como `@Deprecated`
- `NetworkModule.kt` — actualizado para registrar ambas interfaces
- `GoodLifeAuthenticator.kt` — actualizado a `Lazy<AuthApiService>`

---

### 5. Extracción de AuthModule / renombrado de AppModule

**Problema:** `AppModule` mezclaba bindings de Core con Auth, dificultando escalabilidad.

**Solución:**
- `di/AuthModule.kt` — nuevo módulo con todos los bindings de autenticación
- `di/AppModule.kt` — renombrado internamente a `coreModule`, reducido a dependencias transversales
- `GoodLifeApp.kt` — actualizado para cargar `coreModule` y `authModule`
- `AuthModule.kt` — `LoginUseCase` y `RegisterUseCase` inyectan `dispatcher = get()`

---

### 6. Extracción de BiometricLoginHandler

**Problema:** `LoginViewModel` manejaba directamente la lógica biométrica, violando SRP.

**Solución:**
- `domain/auth/BiometricLoginHandler.kt` — clase de dominio que encapsula la lógica biométrica
- `LoginViewModel.kt` — actualizado para delegar a `BiometricLoginHandler`

---

### 7. DailyUiState — `Empty` + `DailyItemHighlight`

**Cambios en `DailyUiState.kt`:**
- Agregado `data object Empty` — para cuando el backend responde 404 (no hay daily log para ese día)
- Agregado `enum class DailyItemHighlight { NONE, NEXT_UP, IN_PROGRESS }`
- `DailyItemUiModel` ahora incluye `val highlight: DailyItemHighlight`
- Extensión `toUiModel(typeLabel, highlight)` actualizada

**Cálculo de highlight en ViewModel:**
```kotlin
val nextUpId = dailyLog.items.firstOrNull { it.status == DailyItemStatus.PENDING }?.id
highlight = when {
    domainItem.status == DailyItemStatus.IN_PROGRESS -> DailyItemHighlight.IN_PROGRESS
    domainItem.id == nextUpId                        -> DailyItemHighlight.NEXT_UP
    else                                             -> DailyItemHighlight.NONE
}
```

---

### 8. DailyTexts — campos nextUp e inProgress

**Archivos modificados:**
- `UiTexts.kt` — añadidos `val nextUp: String` y `val inProgress: String` a `DailyTexts`
- `Spanish.kt` — `nextUp = "PRÓXIMO"`, `inProgress = "EN PROGRESO"`
- `English.kt` — `nextUp = "NEXT UP"`, `inProgress = "IN PROGRESS"`
- `Portuguese.kt` — `nextUp = "PRÓXIMO"`, `inProgress = "EM ANDAMENTO"`

---

### 9. DailyItemStyle — refactoring

**Cambios:**
- Renombrado `actionColor` → `accentColor`
- Renombrado `imageVector` → `icon`
- Añadido `val iconCircleBackground: Color` — para el círculo decorativo del ícono
- Colores actualizados según spec de diseño (fondos pastel más claros, acentos más saturados)
- Constantes de `Color.kt` actualizadas (`HabitBackground`, `WorkoutAccent`, etc.)
- `DailyItemStyle` ahora usa constantes de `Color.kt` como fuente de verdad

---

### 10. DailyItemCard — diseño completo

**Decisiones de diseño y técnicas:**

| Aspecto | Solución |
|---------|----------|
| Borde condicional | `Card` (M3) acepta `border: BorderStroke?` directamente |
| Badge de tipo (pill) | `Surface(shape = MaterialTheme.shapes.extraLarge)` |
| Badge highlight (NEXT UP / IN PROGRESS) | `HighlightBadge` composable privado, straddling el borde |
| Badge straddling el borde | `Box.padding(top = 12.dp)` + `badge.offset(y = -12.dp)` |
| Ícono decorativo | `Surface(shape = CircleShape, color = style.iconCircleBackground)` |
| Botón de status | `Surface(onClick, shape = CircleShape)` — toggle PENDING ↔ COMPLETED |
| Texto tachado en COMPLETED | `textDecoration = TextDecoration.LineThrough` |
| Hora condicional | `item.scheduledTime?.let { Row { Icon + Text } }` |
| Fuente única de colores | Todos los colores vienen de `DailyItemStyle` → `Color.kt` |

**Estructura del composable:**
```
Box (raíz — permite overlay del badge)
  Card (border condicional)
    Row (padding 16dp)
      Column (weight 1f) → badge tipo + título + descripción + hora
      Row → ícono decorativo + botón status
  HighlightBadge (align TopEnd, offset -12dp) — solo si highlight != NONE
```

---

### 11. SPEC-008 — Notificaciones + Deep Links

**Creado:** `front-end/Android/docs/specs/SPEC-008-notifications-deeplinks.md`

Documenta el esquema `goodlife://` para deep links, el mapeo de `NotificationType` a rutas,
la configuración del `AndroidManifest.xml`, y el guard de autenticación en `GoodLifeNavHost`.

---

### 12. Cursor Rules

Creados 6 archivos `.cursor/rules/*.mdc` para proveer contexto arquitectónico persistente al AI:

| Archivo | Contenido |
|---------|-----------|
| `android-architecture.mdc` | Capas, decisiones críticas, SessionEventBus, Koin modules |
| `android-navigation.mdc` | Type-safe routes, ComposeNavigationController, deep links |
| `android-data-layer.mdc` | Result<T>, ApiException, AuthApiService/DailyApiService split |
| `android-compose-patterns.mdc` | Owner Pattern, AppLanguage text flow, DailyItemStyle |
| `android-naming-conventions.mdc` | Convenciones de nombres, estructura de paquetes, KMP rules |
| `android-daily-module.mdc` | GetDailyItemsResult, DailyItemHighlight, buildSuccessState |

---

## ✅ SPEC-007: Sistema de Localización — Completado (2026-02-03)

Sistema de internacionalización KMP-ready. Todos los textos de la app provienen de `AppLanguage`.
Ver `PROGRESS.md` histórico o `SPEC-007` para detalles.

---

## ✅ SPEC-004: DateProvider — Completado

Abstracción timezone-safe para fechas. Habilita testing determinista.

---

## 🚧 SPEC-003: Main Scaffold — 90% Completado

### ✅ Completado

| Fase | Descripción | Estado |
|------|-------------|--------|
| 1. Modelos del Dominio | MealType, DailyItemType, ItemStatus | ✅ |
| 2. Sistema de Fechas | DateProvider, AppLanguage, DateFormats | ✅ |
| 3. Componentes de Header | DateHeaderComponent, CalendarDayIcon | ✅ |
| 4. Modal de Acciones | AddActionModalComponent | ✅ |
| 5. Bottom Navigation | BottomNavigationComponent | ✅ |
| 6. Design System | Shapes, Colors, Typography, DailyItemStyle | ✅ |
| 7. MainScaffold Screen | MainScaffoldViewModel, Screen, Owner | ✅ |
| 8a. Daily Tab | DailyTabViewModel, Screen, Owner, DailyItemCard | ✅ |
| Localización | Todos los textos localizados (SPEC-007) | ✅ |

### ❌ Pendiente

| Fase | Descripción |
|------|-------------|
| 8b. Workouts Tab | WorkoutsTabScreen + ViewModel |
| 8c. Meals Tab | MealsTabScreen + ViewModel |
| 8d. More/Settings Tab | MoreTabScreen + ViewModel |

---

## 🎨 Patrones Consolidados

### Patrón de pantalla

```
[Feature]Screen/
├── model/
│   ├── [Feature]UiState.kt       @Stable, strings ya formateados
│   └── [Feature]UiAction.kt      sealed interface
├── [Feature]ViewModel.kt          language: AppLanguage, dateProvider: DateProvider
│                                   NO dispatcherProvider — responsabilidad del UseCase
├── [Feature]Screen.kt             UI pura: recibe uiState + texts + onAction
└── [Feature]ScreenOwner.kt        Único koinViewModel(), pasa texts al Screen
```

### Patrón UseCase

```kotlin
class GetXxxUseCase(
    private val repository: XxxRepository,
    private val dispatcher: DispatcherProvider  // UseCase es dueño del IO dispatcher
) {
    suspend operator fun invoke(...): XxxResult {
        return withContext(dispatcher.io) {
            when (val result = repository.getXxx()) {
                is Result.Success -> XxxResult.Success(result.data)
                is Result.Error   -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> XxxResult.NotFound
                    is ApiException.ServerException   -> XxxResult.ServerError(ex.message ?: "")
                    else                              -> XxxResult.NetworkError
                }
            }
        }
    }
}
```

### Result Types (Option B)

```kotlin
sealed interface XxxResult {
    data class Success(val data: XxxDomain) : XxxResult
    data object NotFound : XxxResult
    data class ServerError(val message: String) : XxxResult
    data object NetworkError : XxxResult
    // NO hay Loading — Loading es estado de UI, no de resultado
}
```

---

## ✅ Sesión 2026-03-04 — Biometric Fixes, SWR, Tests, Optimizaciones

### Resumen

Sesión intensiva de mejoras de infraestructura y calidad de código. Se aplicaron todas las mejoras biométricas, se implementó SWR Fase 1 con Room, se crearon 60+ unit tests, y se optimizó Compose con ImmutableList y anotaciones de estabilidad.

### Mejoras Biométricas

| Mejora | Descripción |
|--------|-------------|
| Memory Leak Fix | Eliminado `setActivity()` de `AndroidBiometricAuthenticator`. Ahora recibe `platformContext: Any?` en `authenticate()` |
| Channel Events | `BiometricLoginHandler` migrado de `SharedFlow.tryEmit` a `Channel(BUFFERED).trySend` para eventos one-shot confiables |
| Suspend Init | `buildInitialState()` ahora es `suspend` para evitar bloqueo del main thread por `EncryptedSharedPreferences` |
| Dependency biometric | Downgrade de `1.2.0-alpha05` a `1.1.0` (stable) |

### SWR Fase 1 (SPEC-006)

- Creadas entidades Room: `DailyLogEntity`, `DailyItemEntity`, `DailyLogWithItems`
- Creado `DailyDao` con queries y transacciones atómicas
- Reescrito `DailyRepositoryImpl` con patrón SWR Backend-First
- Room v2 con nuevas tablas + migration
- SPEC-006 v2.0 escrito con Fase 1 (implementada) y Fase 2 (sync offline planificada)

### Unit Tests (60+ tests)

| Área | Tests |
|------|-------|
| `BiometricLoginHandlerTest` | buildInitialState, authenticate, handleToggle, saveCredentials, canShowPrompt |
| `CreateTaskUseCaseTest` | validaciones locales, creación exitosa, error mapping |
| `GetDailyItemsUseCaseTest` | success, NotFound, ServerError, NetworkError |
| `UpdateItemStatusUseCaseTest` | success, NotFound, ServerError, NetworkError |
| `LoginUseCaseTest` | credenciales, campos vacíos, formatos inválidos, errores de repo |
| `CreateTaskViewModelTest` | estado inicial, acciones UI, submit success/error, navegación |
| `DailyTabViewModelTest` | init, navigación de días, refresh, OnItemStatusChange |

Infraestructura de test: `TestDispatcherProvider`, `FakeAuthRepository`, `FakeDailyRepository`, `FakeTaskRepository`, `FakeBiometricAuthenticator`, `FakeSecureCredentialsStorage`, `FakeNavigationController`.

### Optimizaciones Compose

| Mejora | Descripción |
|--------|-------------|
| ImmutableList | `DailyUiState.Success.items` y `MainScaffoldUiState` usan `ImmutableList` con `persistentListOf()` |
| @Stable | Todos los UiStates llevan `@Stable` |
| @Immutable | Modelos de lista puros (`DailyItemUiModel`, `BottomNavItemModel`) llevan `@Immutable` |
| Extension Functions | `formatArgs()`, `toDisplayString()`, `parseLocalDateOrNull()`, `findFragmentActivity()` |
| Strings | Reducidos hardcoded strings → `AppLanguage` (`SplashTexts`, `AccessibilityTexts.close/appLogo`) |
| Private const | `DATABASE_NAME` y otras constantes internas marcadas como `private` |

---

## ✅ Sesión 2026-03-09 — Create Task + Componentes reutilizables

### Resumen

Implementación completa del flujo de creación de tareas (SPEC-010), desde el FAB hasta la persistencia en backend con feedback visual. Se crearon 7 componentes reutilizables siguiendo el patrón `*Params` + `*Component` del proyecto.

### Componentes creados

| Componente | Propósito |
|------------|-----------|
| `SwitchComponent` | Switch con label, thumb blanco fijo, track verde. Icono opcional |
| `DayChipComponent` | Chip circular para días de la semana |
| `DateSelectorComponent` | OutlinedCard para abrir date picker |
| `TimeSelectorComponent` | OutlinedCard para abrir time picker |
| `TimePickerDialogComponent` | Material3 TimePicker en AlertDialog |
| `SnackbarComponent` | Snackbar con variantes SUCCESS/ERROR/INFO |
| `SuccessDialog` | Dialog con Lottie animation |

### Decisiones técnicas

- **CreateTask como AppRoute** (no TabRoute): pantalla full-screen sobre el bottom nav, navegación via `ComposeNavigationController`
- **Daily refresh con LifecycleResumeEffect**: recarga al volver de crear tarea. Pragmático para v1, se puede migrar a navigation result si el re-fetch frecuente afecta performance
- **Sin isError en título**: el botón disabled comunica el estado, evitando UI noise innecesaria
- **DatePicker skipPartiallyExpanded**: resuelve el bug de bottom sheet cortado

---

## ✅ Sesión 2026-03-09 — Create Habit + Fixes + Unificación Task/Habit

### Resumen

Implementación completa del flujo de creación de hábitos siguiendo el mismo patrón de Create Task. Se crearon 2 componentes nuevos, se unificaron ambas pantallas, se corrigió un bug crítico en DailyScreen que impedía mostrar hábitos, y se hicieron defensivos todos los DTOs del daily log.

### Archivos creados (Create Habit)

| Capa | Archivos |
|------|----------|
| Domain | `Habit.kt`, `HabitCategory.kt`, `HabitRepository.kt`, `CreateHabitUseCase.kt`, `CreateHabitResult.kt` |
| Data | `HabitApiService.kt`, `HabitRemoteDataSource.kt`, `CreateHabitRequest.kt`, `HabitResponse.kt` + mapper |
| Presentation | `CreateHabitUiState.kt`, `CreateHabitUiAction.kt`, `CreateHabitViewModel.kt`, `CreateHabitScreen.kt`, `CreateHabitScreenOwner.kt` |
| DI + Nav | `HabitModule.kt`, `AppRoute.CreateHabit`, registro en `AppGraph`, conexión `QuickActionType.HABIT` en `MainScaffoldViewModel` |
| Localización | `HabitCategoryTexts` + `CreateHabitTexts` en `UiTexts.kt`, traducciones ES/EN/PT |
| Tests | `CreateHabitUseCaseTest.kt`, `CreateHabitViewModelTest.kt`, `FakeHabitRepository.kt` |

### Componentes nuevos

| Componente | Propósito |
|------------|-----------|
| `CategorySelectorComponent` | `ExposedDropdownMenuBox` (M3) con íconos por categoría. Inicialmente era FlowRow con 13 chips pero ocupaba demasiado espacio — se migró a dropdown |
| `NumericGoalComponent` | Row con campo numérico (targetValue) + campo de texto (unit). Ej: `[8] [vasos]` |

### Bug fix: DailyScreen no mostraba hábitos

**Causa raíz:** `HabitLogSummaryDto` esperaba campos planos (`habitName`, `unit`, `progress`) que no existían en la respuesta del backend. El backend devuelve estructura anidada con `habit: { name, unit, targetValue, category }`. La deserialización fallaba silenciosamente y el repositorio caía al fallback de Room (solo tasks en caché).

**Fix:** Reescrito `HabitLogSummaryDto` para coincidir con la respuesta real. Nuevo `HabitSummaryDto` como objeto anidado. Actualizado mapper en `DailyItemResponse.extractTitleAndDescription()`.

### DTOs defensivos (prevención de futuros crashes)

Se hicieron defensivos `WorkoutSummaryDto` y `MealSummaryDto` — todos los campos `nullable` con `default = null`. Combinado con `ignoreUnknownKeys = true` y `coerceInputValues = true` del Json config, los DTOs ahora resisten desalineaciones con el backend sin crashear.

### Unificación Task / Habit

| Aspecto | Cambio |
|---------|--------|
| `SectionLabel` + `SectionDivider` | Agregados a CreateTaskScreen (antes usaba Text inline sin estilo consistente) |
| Day chips | `Arrangement.SpaceEvenly` en ambas Screens (antes `spacedBy(6.dp)` no ocupaba el ancho completo) |
| `toggleDay` | Extraído como `Set<DayOfWeek>.toggleDay(day)` en `DateTimeExtensions.kt`, usado por ambos ViewModels |
| `endDate >= startDate` | Validación agregada a `CreateTaskUseCase` (Habit ya la tenía) |
| Firma de composables | Unificada: `uiState` primero, `modifier` último en ambas Screens |
| Spacer final | Agregado a CreateTaskScreen (Habit ya lo tenía) |
| @Preview | Agregados a ambas Screens, `CategorySelectorComponent`, `NumericGoalComponent` |

### Decisiones técnicas

- **Dropdown vs Chips para categorías**: 13 categorías con FlowRow ocupaban ~3 filas. `ExposedDropdownMenuBox` ocupa 1 línea cerrado, desplegable con íconos. 100% Compose, sin memory leaks
- **`HabitCategory.icon()` pública**: Se hizo pública (antes era private) para reutilizarla en futuros listados de hábitos
- **categoryPlaceholder**: Nuevo campo en `CreateHabitTexts` para el placeholder del dropdown (localizado en 3 idiomas)
- **No genéricos para DTOs**: Se evaluó `JsonElement` genérico pero pierde type safety. Mejor approach: DTOs tipados pero defensivos (nullable + defaults)

---

## 📋 Próximos Pasos

### Corto plazo

1. **Tabs restantes** — Workouts Tab, Meals Tab, More/Settings Tab
2. **Activate Routine + UpdateItemStatus** — lógica de activar rutina post-creación + pantalla de detalle/progreso de items
3. **Pantalla de detalle de item** — tocar card en DailyScreen abre detalle (para habits: registrar progreso parcial, para tasks: ver descripción completa + confirmar)

### Mediano plazo

4. Tabs restantes: Workouts, Meals, More/Settings
5. SWR Fase 2: Cola de sincronización offline (SPEC-006 Sección 4)
6. Backend: Rate limiting + Caching + Security Headers

### Largo plazo

7. Dark mode completo
8. Push Notifications + Deep Links (SPEC-008)
9. Estadísticas de hábitos (streaks, gráficos)
10. Migración a KMP (shared module) → iOS target

---

## 📚 Referencias

- [SPEC-003: Main Scaffold](./specs/SPEC-003-main-scaffold.md)
- [SPEC-004: DateProvider](./specs/SPEC-004-date-provider.md)
- [SPEC-007: AppLanguage](./specs/SPEC-007-app-language.md)
- [SPEC-008: Notificaciones + Deep Links](./specs/SPEC-008-notifications-deeplinks.md)
- [SPEC-010: Create Task Screen](./specs/SPEC-010-create-task-screen.md)
- [SPEC-011: Create Routine Screen](./specs/SPEC-011-create-routine-screen.md)
- [SPEC-012: Create Meal Plan Screen](./specs/SPEC-012-create-meal-plan-screen.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [DAILY-IMPLEMENTATION-PLAN](./plans/DAILY-IMPLEMENTATION-PLAN.md)
