# Changelog

Todos los cambios notables de este proyecto se documentan en este archivo. El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y el proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

### Security
- El interceptor de autenticación ya no registra URL, headers, tokens ni cuerpos de request. El logging HTTP es `BASIC` solo en debug y `NONE` en release.
- Se deshabilitó el backup de aplicación para impedir que datos de sesión o credenciales entren en backups del sistema.
- Logout solicita revocación remota y siempre borra tokens, credenciales biométricas, usuario y cache Daily locales.

### Fixed
- Login obtiene el perfil canónico con `GET /api/v1/me`; ya no deriva el usuario del JWT ni de la entrada de login.
- Settings redirige a Login y limpia el back stack después de cerrar sesión.
- Profile combina el usuario Room y el estado de carga/error para recomponer correctamente.
- La cache Daily se limpia al cerrar sesión, evitando exposición offline entre cuentas del mismo dispositivo.
- Login conserva usuario y contraseña ante un error remoto y muestra el mensaje general, en lugar de marcar ambos campos como inválidos.

- Daily y Meals descartan respuestas tardías al navegar fechas: cada carga captura su fecha y cancela la carga anterior.

### Quality and documentation
- `:app:testDebugUnitTest`, `:app:logicDebugUnitTestCoverageVerification` y `:app:lintDebug` pasan el 2026-09-10: 167 tests, 0 fallos/errores; cobertura JaCoCo de líneas en lógica elegible 80,73% (1.496/1.853).
- El gate excluye Compose/UI, manifest, Room/DAO, DI y pruebas instrumentadas; no se los declara cubiertos. El smoke en dispositivo contra la API canónica sigue pendiente.
- Se reconciliaron los documentos de revival y se marcaron los planes históricos como no canónicos.

---

## Histórico
## [Unreleased] - 2026-07-24 — P0 Quality Baseline

### Fixed
- Implementado `getActiveRoutine()` en `FakeRoutineRepository` para mantener compatible el contrato de tests con `RoutineRepository`.
- Inicializacion eager del `StateFlow` de `DailyTabViewModel` para que la carga inicial sea deterministica en tests unitarios.

### CI
- Agregado workflow `Android CI` para ejecutar `:app:testDebugUnitTest` en push y PR contra `master`.

---
## [Unreleased] - 2026-05-15 — MEJORAs aplicadas

### ✨ Features
- **MEJORA-001** `GET api/v1/me` client — `AuthApiService.getMe()` + `UserMeResponse` DTO con `toDomain()`
- **MEJORA-012 (Android)** Cloudinary upload flow:
  - `MediaApiService`, `CloudinaryUploadResultDto`, `MediaRepository`, `MediaRepositoryImpl`
  - `UploadProfilePhotoUseCase`, `MediaModule` (Koin), registrado en `GoodLifeApp`
  - `ProfileScreenOwner` + `ProfileViewModel` — reemplaza el placeholder de `/profile`
  - Selector de galería con `ActivityResultContracts.GetContent`
  - `AsyncImage` (Coil) para mostrar foto actual
  - Guarda URL en Room `UserEntity.profileImageUrl` tras upload exitoso
- **MEJORA-013** `Ingredient.availableUnits: List<String>` — DTO y domain model actualizados
- **MEJORA-011 (Android)** Real logout — `AuthApiService.logout()`, `AuthRemoteDataSource.logout()`, `AuthRepositoryImpl` notifica al backend antes de limpiar storage local

### 🔒 Security confirmada
- `GoodLifeAuthenticator` (OkHttp `Authenticator`): refresh automático al 401 con Mutex anti-race-condition — ya estaba implementado
- `TokenManager`: `EncryptedSharedPreferences` (AES256-GCM) — ya estaba implementado

---
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

---

## [Unreleased] - 2026-05-15

### Merge: feature/daily_workflow → master

#### ✅ Seguridad de tokens — ya implementados (aclaración)

Los siguientes ítems figuraban como pendientes en documentación interna pero ya estaban implementados en la rama:

- **`GoodLifeAuthenticator`** — refresh automático al 401 con `Mutex` para prevenir race conditions en requests concurrentes. Registrado en `NetworkModule` via `inject()` (Lazy) para romper la dependencia circular OkHttpClient ↔ AuthApiService.
- **`TokenManager`** — ya usa `EncryptedSharedPreferences` con `MasterKey` (AES256-GCM) respaldada por Android Keystore. Los JWT nunca se guardan en texto plano.

#### ✅ Daily workflow completo integrado a master

Ver sección anterior (2026-04-14) para detalle completo de cambios de la rama.

---

## [Unreleased] - 2026-04-14

### Added

#### ✅ Módulo Create Meal Plan — Wizard multi-paso de planificación de comidas (SPEC-012)

- **Flujo wizard de 3 pasos** para crear planes de comida:
  - **Paso 1 — Seleccionar Meal**: catálogo paginado de meals con búsqueda en tiempo real y filtros por tipo de comida; opción alternativa para crear una meal nueva directamente desde el wizard
  - **Paso 2 — Ingredientes**: resumen de macros (calorías, proteínas, carbohidratos, grasas) con visualización en chip pills; catálogo paginado de ingredientes agrupados en "Mis ingredientes" y "Catálogo global"; modal para agregar cantidad con selector de unidad (g, ml, unidad)
  - **Paso 3 — Scheduling**: selector de tipo de comida (Desayuno, Almuerzo, Cena, Snack), selector de días de la semana, DatePicker de fecha de inicio, TimePicker de hora

- **Resultado final**: `POST /api/v1/meal-plans` crea el plan; el backend genera items `MEAL` en el Daily Log para los días configurados

- **Result types semánticos** corregidos en toda la feature:
  - `CreateCustomIngredientResult.ServerError(val message: String)` (era `data object`)
  - `CreateCustomMealResult.ServerError(val message: String)` (era `data object`)
  - `CreateMealPlanResult.ServerError(val message: String)` (era `data object`)
  - Todos los UseCases pasan `ex.message` al construir `ServerError`

- **Componente CreateIngredientDialog**: bottom sheet glassmorphic con:
  - Campos Nombre y Marca con estilo filled (`NutritionBackground`, sin border en reposo, borde `NutritionAccent` al foco)
  - Selector de unidad de porción con `FilterChip` (g / ml / unidad)
  - Cards de macros con `BasicTextField` + placeholder overlay (sin `OutlinedTextField` para evitar borde visual)
  - Botón Guardar habilitado solo si nombre no está vacío y porción > 0

- **Componente QuantityBottomSheet**: selector de cantidad para agregar un ingrediente al plan, con cálculo de macros proporcional en tiempo real

- **`CreateMealPlanViewModel`**: único ViewModel para todo el wizard:
  - Paginación de ingredientes (scroll infinito) y catálogo de meals
  - `OnCreateCustomIngredient` → llama `CreateCustomIngredientUseCase` y refleja el nuevo ingrediente en la lista
  - `submitMealPlan()`: guarda completo con guards independientes (mealType, selectedDays), crea meal custom si `isCreatingNewMeal`, construye `ScheduledMealDraft` + `MealPlanDraft` y llama `CreateMealPlanUseCase`
  - `viewModelScope.launch {}` sin `Dispatchers.IO` — ownership del dispatcher delegado al UseCase

- **`CreateMealPlanScreen`**: bug fix crítico — el botón primario del `WizardBottomBarComponent` ahora despacha `OnSubmit` en el paso SCHEDULE y `OnNextStep` en los demás pasos; `isLoading` y `primaryEnabled` conectados al `uiState`

- **`CreateMealPlanScreenOwner`**: orquesta todos los overlays:
  - `DatePickerBottomSheetComponent` para `START_DATE` y `END_DATE`
  - `TimePickerDialogComponent`
  - `CreateIngredientDialog`
  - `QuantityBottomSheet`
  - `PopupResultComponent` (LOADING / ERROR)
  - `SuccessDialog` con Lottie

- **Navegación integrada**:
  - `AppRoute.CreateMealPlan` registrado en `AppGraph`
  - FAB "+" del MainScaffold con `QuickActionType.MEAL` navega a `CreateMealPlanScreenOwner`

- **Localización completa** (`CreateMealPlanTexts`): título, badge, pasos, labels de campos, placeholders, botones, mensajes de error — en Español, English, Português

#### 📁 Nuevos Archivos

**Domain:**
- `domain/model/nutrition/Ingredient.kt`
- `domain/model/nutrition/IngredientEntry.kt`
- `domain/model/nutrition/Meal.kt`
- `domain/model/nutrition/MealSummary.kt`
- `domain/model/nutrition/MealPlan.kt`
- `domain/model/nutrition/MealPlanDraft.kt`
- `domain/model/nutrition/ScheduledMealDraft.kt`
- `domain/model/nutrition/PortionUnit.kt`
- `domain/repository/IngredientRepository.kt`
- `domain/repository/MealRepository.kt`
- `domain/repository/MealPlanRepository.kt`
- `domain/usecase/nutrition/GetIngredientCatalogUseCase.kt`
- `domain/usecase/nutrition/GetMealCatalogUseCase.kt`
- `domain/usecase/nutrition/CreateCustomIngredientUseCase.kt`
- `domain/usecase/nutrition/CreateCustomMealUseCase.kt`
- `domain/usecase/nutrition/CreateMealPlanUseCase.kt`
- `domain/usecase/nutrition/result/GetIngredientCatalogResult.kt`
- `domain/usecase/nutrition/result/GetMealCatalogResult.kt`
- `domain/usecase/nutrition/result/CreateCustomIngredientResult.kt`
- `domain/usecase/nutrition/result/CreateCustomMealResult.kt`
- `domain/usecase/nutrition/result/CreateMealPlanResult.kt`

**Data:**
- `data/remote/api/nutrition/IngredientApiService.kt`
- `data/remote/api/nutrition/MealApiService.kt`
- `data/remote/api/nutrition/MealPlanApiService.kt`
- `data/remote/datasource/IngredientRemoteDataSource.kt`
- `data/remote/datasource/MealRemoteDataSource.kt`
- `data/remote/datasource/MealPlanRemoteDataSource.kt`
- `data/remote/dto/request/nutrition/CreateCustomIngredientRequest.kt`
- `data/remote/dto/request/nutrition/CreateCustomMealRequest.kt`
- `data/remote/dto/request/nutrition/CreateMealPlanRequest.kt`
- `data/remote/dto/response/nutrition/` — DTOs defensivos con mappers
- `data/repository/IngredientRepositoryImpl.kt`
- `data/repository/MealRepositoryImpl.kt`
- `data/repository/MealPlanRepositoryImpl.kt`

**Presentation:**
- `presentation/screen/add/mealplan/model/CreateMealPlanUiState.kt`
- `presentation/screen/add/mealplan/model/CreateMealPlanUiAction.kt`
- `presentation/screen/add/mealplan/model/MealPlanWizardStep.kt`
- `presentation/screen/add/mealplan/model/MealDatePickerField.kt`
- `presentation/screen/add/mealplan/CreateMealPlanViewModel.kt`
- `presentation/screen/add/mealplan/CreateMealPlanScreen.kt`
- `presentation/screen/add/mealplan/CreateMealPlanScreenOwner.kt`
- `presentation/screen/add/mealplan/steps/SelectMealStep.kt`
- `presentation/screen/add/mealplan/steps/MealIngredientsStep.kt`
- `presentation/screen/add/mealplan/steps/ScheduleStep.kt`
- `presentation/screen/add/mealplan/steps/PreviewHelpers.kt`
- `presentation/components/nutrition/CreateIngredientDialog.kt`
- `presentation/components/nutrition/QuantityBottomSheet.kt`
- `presentation/components/nutrition/MealCatalogItem.kt`
- `presentation/components/nutrition/IngredientCatalogSection.kt`
- `presentation/components/nutrition/MyIngredientsSection.kt`
- `presentation/components/nutrition/MacrosSummaryCard.kt`
- `presentation/components/nutrition/MealHeaderCard.kt`

**DI + Navigation:**
- `di/NutritionModule.kt`

### Fixed

- **`CreateMealPlanScreen` — submit nunca se ejecutaba**: `WizardBottomBarComponent` siempre despachaba `OnNextStep` en todos los pasos; en el paso SCHEDULE `advanceWizardStep()` retorna inmediatamente sin efecto. Corregido para despachar `OnSubmit` cuando el paso actual es `SCHEDULE`
- **`ServerError` sin mensaje**: los tres result types de nutrición tenían `data object ServerError` sin campo `message`; cambiados a `data class ServerError(val message: String)` y los UseCases actualizados para pasar `ex.message`
- **`_uiState.update { state.copy(...) }` → `it.copy(...)`**: estado capturado antes de llamada async era stale; corregido en `submitMealPlan()`
- **Guards de validación sobreescribiéndose**: user bug donde `var errorMessage` era asignado dos veces y la segunda sobreescribía la primera; corregido a guards independientes con `return`

---

## [Unreleased] - 2026-03-11

### Added

#### ✅ Módulo Create Routine — Wizard multi-paso de creación de rutinas (SPEC-011)

- **Flujo wizard de 4 pasos**: Información de rutina → Agregar workouts → Agregar ejercicios con sets → Resumen y confirmación
- **Catálogo paginado de ejercicios** con scroll infinito por grupo muscular
- **`SetEditorBottomSheet`**: editor de sets con `mutableStateListOf` local antes de confirmar
- **`PageResult<T>`** genérico movido a `core/pagination/` para reutilización
- **Draft models** en domain: `WorkoutDraft`, `ExerciseDraft`, `SetDraft`
- **4 UseCases** con result types semánticos + **4 archivos Result**
- **Tests**: `CreateRoutineUseCaseTest` (11 tests) + `CreateRoutineViewModelTest` (25 tests)
- `AppRoute.CreateRoutine` + registro en `AppGraph`; FAB `QuickActionType.WORKOUT` navega a `CreateRoutine`
- `CreateRoutineTexts` en `UiTexts.kt` + traducciones ES/EN/PT

---

## [Unreleased] - 2026-03-09

### Added

#### ✅ Módulo Create Task — Pantalla completa de creación de tareas (SPEC-010)

- **Flujo completo** de creación de tareas con dos modos de scheduling:
  - **Una vez (ONCE)**: fecha puntual + hora opcional
  - **Se repite (RECURRENT)**: días de la semana + rango de fechas + hora opcional

- **Navegación integrada**:
  - `AppRoute.CreateTask` registrado en `AppGraph` como ruta de nivel app (full-screen)
  - FAB "+" del `MainScaffold` navega a `CreateTask` via `QuickActionType.TASK`
  - `ComposeNavigationController` inyectado en `CreateTaskViewModel` para `navigateUp()`

- **QuickActionType actualizado**:
  - Reemplazados tipos genéricos (ROUTINE, NUTRITION, WEIGHT, SUPPLEMENTS, ACTIVITY)
  - Nuevos tipos: `TASK`, `HABIT`, `WORKOUT`, `MEAL` + `OTHER`

- **Componentes reutilizables creados**:
  - `SwitchComponent` — Switch con label, thumb blanco fijo sobre track verde, soporte para icono opcional. Thumb en `Box` con tamaño fijo para evitar el bug de Material3 donde se achica al cambiar de estado
  - `DayChipComponent` — Chip circular (42dp) para selección de días. Selected: fondo `LightGreen` con texto blanco bold. Unselected: borde gris con fondo transparente
  - `DateSelectorComponent` — OutlinedCard para selección de fecha con icono de calendario
  - `TimeSelectorComponent` — OutlinedCard para selección de hora con icono de reloj
  - `TimePickerDialogComponent` — Material3 TimePicker en AlertDialog reutilizable
  - `DatePickerBottomSheetComponent` — Renombrado de `DatePickerBottomSheet` + `skipPartiallyExpanded = true` para que se abra completo sin necesidad de arrastrar
  - `SnackbarComponent` — Snackbar con 3 variantes (SUCCESS, ERROR, INFO), animación slide+fade, auto-dismiss configurable, botón de acción opcional
  - `SuccessDialog` — Dialog con animación Lottie para feedback de éxito

- **Integración backend**:
  - `POST /api/v1/tasks` → 201 Created (probado en emulador)
  - `CreateTaskUseCase` con result types semánticos (`Success`, `ValidationError`, `ServerError`, `NetworkError`)
  - `TaskApiService`, `TaskRemoteDataSource`, `TaskRepositoryImpl`, `TaskModule`

- **Localización**:
  - `CreateTaskTexts` — strings de la pantalla (título, badge, botón guardar, éxito)
  - `CreateItemSharedTexts` — strings compartidos entre formularios de creación (labels de campos, modos, fechas, toggles, confirm/cancel/retry)
  - Traducciones en Español, English, Português

- **Daily refresh al volver**:
  - `LifecycleResumeEffect` en `DailyScreenOwner` llama `viewModel.refresh()` cuando la pantalla vuelve a ser visible
  - `DailyTabViewModel.refresh()` expuesto como función pública

- **Dependencia Lottie**:
  - `lottie-compose` agregado en `libs.versions.toml` (reemplaza `lottie` para Views)

#### 📁 Nuevos Archivos

**Presentation — Componentes:**
- `presentation/components/common/SwitchComponent.kt`
- `presentation/components/common/DayChipComponent.kt`
- `presentation/components/common/DateSelectorComponent.kt`
- `presentation/components/common/TimeSelectorComponent.kt`
- `presentation/components/common/SnackbarComponent.kt`
- `presentation/components/dialog/SuccessDialog.kt`
- `presentation/components/dialog/TimePickerDialogComponent.kt`

**Presentation — Pantalla:**
- `presentation/screen/add/task/CreateTaskScreen.kt`
- `presentation/screen/add/task/CreateTaskScreenOwner.kt`
- `presentation/screen/add/task/CreateTaskViewModel.kt`
- `presentation/screen/add/task/model/CreateTaskUiState.kt`
- `presentation/screen/add/task/model/CreateTaskUiAction.kt`

**Domain:**
- `domain/usecase/task/CreateTaskUseCase.kt`
- `domain/usecase/task/result/CreateTaskResult.kt`
- `domain/repository/TaskRepository.kt`

**Data:**
- `data/remote/api/task/TaskApiService.kt`
- `data/remote/datasource/TaskRemoteDataSource.kt`
- `data/remote/dto/request/CreateTaskRequest.kt`
- `data/repository/TaskRepositoryImpl.kt`

**DI:**
- `di/TaskModule.kt`

**Resources:**
- `res/raw/success_animation.json` (Lottie)

### Changed

- **MainScaffoldViewModel**: Inyecta `ComposeNavigationController`, navega a `CreateTask` via quick actions
- **MainScaffoldUiState**: Quick actions actualizados con nuevos `QuickActionType`
- **DailyScreenOwner**: Agrega `LifecycleResumeEffect` para refresh automático al volver
- **DailyTabViewModel**: Expone `refresh()` público
- **DatePickerBottomSheetComponent**: Renombrado + `skipPartiallyExpanded = true`
- **AppRoute.kt**: Agregado `CreateTask`
- **AppGraph.kt**: Registrado `CreateTaskScreenOwner`
- **AuthModule.kt**: Limpieza de referencias a `HomeViewModel` inexistente
- **libs.versions.toml**: `lottie` → `lottie-compose`

### Fixed

- **DatePicker cortado**: Bottom sheet se abría parcialmente y requería arrastrar manualmente. Solucionado con `skipPartiallyExpanded = true`
- **Referencias rotas**: Eliminadas imports de `HomeViewModel` y `HomeScreenOwner` que ya no existían en `AuthModule.kt` y `AppGraph.kt`

---

## [Unreleased] - 2026-02-03

### Added

#### 🔒 Autenticación automática — Refresh de token con sincronización segura

- **`GoodLifeAuthenticator`** — Implementación de `okhttp3.Authenticator` para renovar el
  access token de forma transparente cuando cualquier endpoint responde `401 Unauthorized`:
  - `Mutex` para serializar el refresh: solo una corrutina ejecuta el request de renovación;
    las restantes esperan suspendidas y reutilizan el token ya obtenido sin volver a llamar al
    backend.
  - **Guard de recursión** en el endpoint de token (`/api/v1/token`): si el propio refresh
    devuelve 401, se limpian los tokens y se emite `SessionEventBus.SessionExpired`
    inmediatamente, evitando un bucle infinito.
  - **Guard de reintento** (`X-Retry-After-Refresh`): si la solicitud ya fue reintentada con el
    nuevo token y vuelve a fallar, fuerza logout sin otro ciclo.
  - Re-uso del token ya renovado: si al adquirir el Mutex el token del request original ya
    difiere del token actual, se construye la solicitud con el token nuevo sin hacer otra llamada
    de refresh.
  - `try/catch` global en el `runBlocking` para evitar crashes ante fallos de red o de parseo.
  - Constantes para todos los literales (`TAG`, `HEADER_AUTH`, `BEARER_PREFIX`,
    `HEADER_RETRY`, `GRANT_TYPE_REFRESH`, `ACCESS_TOKEN_DURATION_MS`, `TOKEN_ENDPOINT`).

#### 🌐 Sistema de Localización KMP-Ready (SPEC-007)

- **AppLanguage sealed interface** - Sistema completo de internacionalización
  - 107+ strings organizados en 9 grupos de textos
  - 3 idiomas: Español, English, Português
  - Detección automática de locale del dispositivo
  - 100% Kotlin puro — sin Android Context, sin `stringResource()`, sin `R.string`
  - Exhaustividad garantizada por compilador (sealed interface)

- **Grupos de textos (UiTexts.kt)**:
  - `AuthTexts` (24 strings) — Login, Register, biometric prompt
  - `ValidationTexts` (20 strings) — UseCases de validación
  - `ErrorTexts` (12 strings) — Mensajes de error genéricos
  - `DailyTexts` (11 strings) — Daily tab (progreso, errores)
  - `MainScaffoldTexts` (16 strings) — BottomNav, modal, tabs
  - `HomeTexts` (5 strings) — Home screen
  - `AccessibilityTexts` (12 strings) — Content descriptions
  - `DailyItemLabels` (4 strings) — Etiquetas de tipo de item
  - `RelativeDateTexts` (3 strings) — Hoy/Ayer/Mañana

- **Screen Wrappers**:
  - `AuthScreenTexts` — Agrupa `AuthTexts` + `AccessibilityTexts` para Login/Register

- **Params con textos integrados**:
  - `TextFieldParams` — `passwordToggleHide`, `passwordToggleShow`
  - `BottomNavigationParams` — `fabContentDescription`
  - `DateHeaderParams` (nuevo) — `openCalendarLabel`, `previousDayLabel`, `nextDayLabel`, `notificationsLabel`
  - `WorkoutTabHeaderParams` (nuevo) — `filterContentDescription`

#### 📁 Nuevos Archivos

**Core Layer:**
- `core/datetime/language/UiTexts.kt` - Todos los data class de textos + screen wrappers

**Documentación:**
- `docs/specs/SPEC-007-app-language.md` - Especificación completa

#### 🌐 Offline-First Architecture + SWR (SPEC-006)

- **Stale-While-Revalidate Pattern** - Patrón profesional para cache + sincronización
  - Cache-first: UI renderiza en 0ms
  - Background revalidation: Backend siempre consulta
  - Fallback offline: App funciona sin internet
  - Optimistic UI: Updates instantáneos

- **Documentación completa**:
  - `docs/specs/SPEC-006-offline-first-swr.md` - Especificación completa (800+ líneas)
  - Comparación TTL vs SWR
  - Implementación de Repository con SWR
  - Optimistic UI en ViewModels
  - Flujos de usuario (online/offline)
  - Optimizaciones avanzadas
  - Aplicación en todos los módulos (Daily, Tasks, Habits, Workouts, Meals)

- **Plan actualizado**:
  - `docs/plans/DAILY-IMPLEMENTATION-PLAN.md` - Actualizado con SWR
  - Eliminado `cachedAt` y `isFresh()` de entities
  - Repository con `fetchDailyLogWithSWR()`
  - Optimistic UI en `DailyTabViewModel`

#### 📅 DateProvider Pattern (SPEC-004)

- **DateProvider interface** - Abstracción para manejo de fechas (KMP-ready)
  - `today()`: Fecha actual en timezone local
  - `yesterday()`, `tomorrow()`: Métodos de conveniencia
  - `now()`: Timestamp UTC actual

- **RealDateProvider** - Implementación para producción
  - Usa `Clock.System` + `TimeZone.currentSystemDefault()`
  - Cachea TimeZone para optimizar performance
  - Encapsula `@OptIn(ExperimentalTime)` en un solo lugar

- **FakeDateProvider** - Implementación para tests
  - Fecha fija inyectada en constructor
  - Habilita tests deterministas sin depender del reloj del sistema

#### 📁 Nuevos Archivos

**Core Layer:**
- `core/datetime/DateProvider.kt` - Interface
- `core/datetime/RealDateProvider.kt` - Implementación Android
- `core/datetime/FakeDateProvider.kt` - Implementación para tests

**Documentación:**
- `docs/specs/SPEC-004-date-provider.md` - Especificación completa (1000+ líneas)
- `docs/archive/` - Documentación de proceso preservada
- `docs/README.md` - Índice actualizado

### Changed

#### 🏗️ Refactoring de Localización (SPEC-007)

- **LoginViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `screenTexts: AuthScreenTexts`
  - Biometric prompt texts desde `AuthTexts` (ya no desde `R.string`)

- **RegisterViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `screenTexts: AuthScreenTexts`

- **HomeViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `homeTexts: HomeTexts`

- **DailyTabViewModel**: Expone `dailyTexts` y `accessibilityTexts`

- **MainScaffoldViewModel**: `fabContentDescription` ahora es parte de `MainScaffoldUiState`
  - Inicializado desde `language.accessibilityTexts.add`

- **MainScaffoldUiState**: Agregado `fabContentDescription: String`

- **LoginScreen / RegisterScreen**: Reciben `texts: AuthScreenTexts` como parámetro
  - Eliminado `koinInject<AppLanguage>()`

- **HomeScreen**: Recibe `homeTexts: HomeTexts` como parámetro

- **DailyScreen**: Recibe `dateHeaderParams: DateHeaderParams` en lugar de `accessibilityTexts` directo

- **LoginScreenOwner**: Usa `viewModel.screenTexts` para biometric prompt strings
  - Eliminado `stringResource()` y `R.string` para biometric prompt

- **DailyScreenOwner**: Construye `DateHeaderParams` desde `viewModel.accessibilityTexts`

- **TextFieldComponent**: `passwordToggleHide/Show` movidos dentro de `TextFieldParams`

- **BottomNavigationComponent**: `fabContentDescription` movido dentro de `BottomNavigationParams`

- **DateHeaderComponent**: Refactorizado a recibir `DateHeaderParams`

- **TabRowHeaderComponent**: Refactorizado a recibir `WorkoutTabHeaderParams`

- **TitleComponent**: Ahora solo acepta `text: String` (eliminado `textId: Int?` y `stringResource`)

- **SplashScreenOwner**: Eliminado import de `koinInject` no utilizado

- **AppModule.kt**:
  - `single<AppLanguage>` con detección automática de locale (`Locale.getDefault().language`)
  - Todos los ViewModels reciben `language = get()`
  - Registrado `DateProvider` como singleton

#### 📅 DateProvider (SPEC-004)

- **DailyTabViewModel**: Ahora usa `DateProvider` en lugar de `Clock` directamente
  - Formatea fechas en el ViewModel (no en UI)
  - Compara fechas con `dateProvider.today()`, `yesterday()`, `tomorrow()`

- **MainScaffoldViewModel**: Integrado con DateProvider

- **LocalDateExtensions.kt**: Migradas funciones a ViewModels

### Removed

- ❌ `stringResource()` de toda la capa de presentación compartida
- ❌ `R.string` de toda la capa de presentación compartida
- ❌ `koinInject()` de todos los Screens y Components
- ❌ Textos hardcodeados de Screens y Components
- ❌ Parámetros de texto sueltos fuera de Params en Components
- ❌ `textId: Int?` en `TitleComponent` (ahora solo `text: String`)

### Refactored

#### 🗂️ Split de `AppLanguage.kt` — Un archivo por idioma

- `AppLanguage.kt` ahora solo contiene la `sealed interface`: contrato puro, sin implementaciones.
- Cada idioma vive en su propio archivo en el mismo package
  `core/datetime/language/`:
  - `Spanish.kt` — `data object Spanish : AppLanguage`
  - `English.kt` — `data object English : AppLanguage`
  - `Portuguese.kt` — `data object Portuguese : AppLanguage`
- **Motivación**: el archivo original tenía ~580 líneas; con el split cada archivo tiene ~150 líneas
  y es responsable de un solo idioma → Single Responsibility, más fácil de revisar en PRs,
  y más fácil de agregar un nuevo idioma sin tocar los existentes.
- Sin cambios de comportamiento: mismos textos, misma lógica de detección de locale en `AppModule`.

### Fixed

#### 🐛 Bug de Timezone Resuelto (SPEC-004)
- **Problema**: App mostraba fecha +1 día en emuladores
  - `TimeZone.currentSystemDefault()` devolvía UTC en lugar de timezone local
  - Emuladores con hora 23:00 mostraban el día siguiente

- **Solución**: DateProvider encapsula correctamente Clock + TimeZone
  - Cachea `TimeZone.currentSystemDefault()` al inicializar
  - Usa `clock.now().toLocalDateTime(timeZone).date` para fecha local correcta
  - ✅ Validado: Si son las 23:00 del 3 de febrero → muestra "3" (no "4")

#### 🏗️ Violaciones arquitectónicas corregidas (SPEC-007)
- **Antes**: `koinInject<AppLanguage>()` en Screens y Components — violaba KMP-readiness
- **Ahora**: Textos fluyen del ViewModel al Screen como parámetro, Components via Params
- **Antes**: `stringResource(R.string.xxx)` en Components — bloqueaba KMP
- **Ahora**: Todos los textos vienen de `AppLanguage` (pure Kotlin)
- **Antes**: Textos de accessibility como parámetros sueltos
- **Ahora**: Encapsulados dentro de Params (`DateHeaderParams`, `TextFieldParams`, etc.)

#### 🔄 Race condition en Splash — SharedFlow replay=1

- **Problema**: después del auto-login, `SplashViewModel` emitía el evento de navegación antes de
  que `GoodLifeNavHost` comenzara a colectar el `SharedFlow`, perdiendo el evento y dejando la
  app en loop infinito sobre el Splash.
- **Solución**: `ComposeNavigationControllerImpl` usa `MutableSharedFlow<NavigationAction>(replay = 1)`,
  almacenando el último evento para entregarlo a suscriptores tardíos.
- `GoodLifeNavHost` colecta directamente con `LaunchedEffect` (sin `repeatOnLifecycle`) para
  garantizar que el collector esté activo desde la primera composición.

#### 🔄 Flickering en `DateHeaderComponent` al cambiar de fecha

- Las lambdas `onPreviousDay` / `onNextDay` se envuelven con `remember(onAction)` en `DailyScreen`
  para garantizar referencias estables entre recomposiciones.
- `enabledColors` y `disabledColors` fueron elevados a `private val` de nivel top del archivo,
  evitando su recreación en cada recomposición.

#### ✅ Beneficios acumulados (SPEC-004 + SPEC-007)
- ✅ Timezone local correcto
- ✅ UI 100% pura (sin lógica de fechas, sin inyección directa, sin stringResource)
- ✅ Testeable con FakeDateProvider y textos mock
- ✅ KMP-compatible (solo `kotlinx.datetime`, textos en pure Kotlin)
- ✅ Encapsula `@OptIn(ExperimentalTime)` en `RealDateProvider.kt`
- ✅ Performance optimizada (cachea TimeZone)
- ✅ 3 idiomas con ~321 traducciones (107 strings × 3)
- ✅ Detección automática de locale
- ✅ Token refresh concurrente serializado con `Mutex` (sin deadlocks, sin duplicados)
- ✅ Auto-login sin race conditions en navegación
- ✅ Codebase de localización split por responsabilidad (un archivo por idioma)

### Documentation

- **SPEC-007**: Especificación completa del sistema de localización
  - Problemas resueltos (koinInject, stringResource, textos sueltos)
  - Arquitectura y flujo de datos
  - Reglas estrictas con ejemplos
  - Cómo agregar nueva vista / nuevo idioma

- **SPEC-004**: Especificación completa del DateProvider pattern
  - Bug de timezone + solución
  - Before/After comparisons
  - Tests deterministas

- **ARCHITECTURE.md**: Actualizado con localización, DateProvider, Params
- **ARCHITECTURE_GUIDE.md**: v2.0 — sección 9 de localización, 6 reglas nuevas, checklist actualizado
- **specs/README.md**: Índice completo SPEC-001 a SPEC-007, diagrama de relaciones
- **PROGRESS.md**: Estado actualizado al 2026-02-03
- **SPEC-003**: Actualizado a 85% — fases 7 y 8a completadas

- **archive/**: Documentación de proceso preservada
  - `ANALISIS-CLOCK-Y-MEJORAS.md` - Análisis del problema
  - `DATEPROVIDER-IMPLEMENTACION-COMPLETA.md` - Tracking de implementación
  - `INSTRUCCIONES-VALIDACION.md` - Checklist de validación

---

## [0.5.0] - 2026-01-20

### Added

#### 📝 Módulo de Registro Completo

- **Pantalla de Registro** con formulario completo
  - Nombre completo (guardado localmente, pendiente backend)
  - Nombre de usuario (username)
  - Email
  - Contraseña con toggle de visibilidad
  - Confirmar contraseña con toggle
  - Checkbox de términos y condiciones

- **Validación en tiempo real**
  - Las contraseñas se comparan mientras el usuario escribe
  - Borde rojo inmediato si no coinciden
  - Botón deshabilitado si las contraseñas no coinciden

- **UseCases nuevos:**
  - `ValidateFullNameUseCase` - Valida nombre completo (min 2 chars, solo letras)
  - `ValidateUserNameUseCase` - Valida username (min 2 chars)
  - `ValidatePasswordMatchUseCase` - Valida que las contraseñas coincidan
  - `RegisterUseCase` - Orquesta el proceso de registro

#### 📁 Nuevos Archivos

**Domain Layer:**
- `domain/usecase/validation/ValidateFullNameUseCase.kt`
- `domain/usecase/validation/ValidateUserNameUseCase.kt`
- `domain/usecase/validation/ValidatePasswordMatchUseCase.kt`
- `domain/usecase/validation/RegisterUseCase.kt`

**Presentation Layer:**
- `presentation/screen/register/RegisterScreen.kt`
- `presentation/screen/register/RegisterScreenOwner.kt`
- `presentation/screen/register/RegisterViewModel.kt`
- `presentation/screen/register/model/RegisterUiState.kt`
- `presentation/screen/register/model/RegisterUiAction.kt`

**Navigation:**
- `navigateToLoginFromRegister()` en `NavigationExtensions.kt`

### Changed

- **RegisterResponse.kt**: Ajustado para coincidir con backend
  - Solo `username`, `email`, `message` (sin `id` ni `createdAt`)
  
- **AppModule.kt**: Registrados todos los UseCases y `RegisterViewModel`

- **AppGraph.kt**: `RegisterScreenOwner` reemplaza placeholder

- **TextFieldComponent**: Maneja visibilidad de password internamente (simplificado)

### Fixed

- Validación de contraseñas ahora funciona en tiempo real
- Botón "Registrarme" se deshabilita correctamente si contraseñas no coinciden
- Parseo correcto de respuesta del backend sin campo `id`

### Documentation

- **SPEC-001-register-screen.md**: Marcado como ✅ Completado
- **CHANGELOG.md**: Actualizado con v0.5.0
- **README.md**: Features actualizadas

---

## [0.4.0] - 2026-01-20

### Added

#### 🔐 Autenticación Biométrica (KMP-Ready)

- **Login con huella digital**: Autenticación biométrica completa
  - Huella digital (Fingerprint)
  - Face ID (en dispositivos compatibles)
  - Métodos alternativos: PIN, patrón, contraseña del dispositivo
  - Prompt automático al abrir la app si está activado
  - Prompt manual al tocar el ícono de huella

- **Auto-completar email**: El campo de email se autocompleta automáticamente cuando la biometría está activada, ahorrando tiempo al usuario

- **Arquitectura KMP-Ready**:
  - Interfaces en capa `domain` (Kotlin puro, sin Android)
  - Implementaciones en capa `platform` (Android específico)
  - Inversión de dependencias con Koin

#### 📁 Nuevos Archivos (Domain Layer - Kotlin Puro)

- `domain/biometric/BiometricAuthenticator.kt` - Interface para autenticación
- `domain/biometric/BiometricPromptConfig.kt` - Configuración del prompt
- `domain/biometric/BiometricAvailability.kt` - Estados de disponibilidad
- `domain/biometric/BiometricResult.kt` - Resultados de autenticación
- `domain/storage/SecureCredentialsStorage.kt` - Interface para credenciales

#### 📁 Nuevos Archivos (Platform Layer - Android)

- `platform/biometric/AndroidBiometricAuthenticator.kt` - Impl. de BiometricAuthenticator
- `platform/storage/AndroidSecureCredentialsStorage.kt` - Impl. de SecureCredentialsStorage

#### 📁 Nuevos Archivos (DI)

- `di/BiometricModule.kt` - Módulo Koin para biometría

#### 📱 Cambios en UI

- **LoginScreen**: 
  - Nuevo `CheckboxComponent` para activar login con huella
  - Ícono de huella digital clickeable para activar prompt manual
  - Email pre-rellenado cuando biometría está activada

- **LoginUiState.Content**:
  - `isBiometricAvailable: Boolean` - Hardware soporta biometría
  - `isBiometricEnabled: Boolean` - Usuario activó biometría
  - `shouldShowBiometricPrompt: Boolean` - Mostrar prompt biométrico

- **LoginUiAction**:
  - `OnBiometricToggle` - Toggle del checkbox
  - `OnBiometricIconClick` - Click en ícono de huella
  - `OnBiometricResult(result)` - Resultado del prompt

#### 🔧 Cambios Técnicos

- **MainActivity**: Cambió de `ComponentActivity` a `FragmentActivity` (requerido por BiometricPrompt)
- **LoginScreenOwner**: 
  - Maneja el ciclo de vida del BiometricPrompt
  - Usa `findActivity()` extension para obtener FragmentActivity desde Compose
  - Pasa Activity al `AndroidBiometricAuthenticator` vía `setActivity()`

- **LoginViewModel**:
  - `checkBiometricStatus()` - Verifica disponibilidad y estado
  - `handleBiometricToggle()` - Maneja toggle de checkbox
  - `handleBiometricIconClick()` - Activa prompt manual
  - `handleBiometricResult()` - Procesa resultado del prompt
  - `performLoginWithCredentials()` - Login con credenciales guardadas

### Changed

- **GoodLifeApp.kt**: Incluye `biometricModule` en la inicialización de Koin
- **strings.xml**: Nuevos strings para prompt biométrico
  - `biometric_prompt_title`
  - `biometric_prompt_subtitle`
  - `biometric_checkbox_label`

### Dependencies

```kotlin
// build.gradle.kts (app)
implementation("androidx.biometric:biometric:1.2.0-alpha05")
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

### Security

- **EncryptedSharedPreferences**: Credenciales almacenadas con AES256_GCM
- **MasterKey**: AES256_GCM para encriptación de claves
- **BiometricPrompt**: Usa `BIOMETRIC_STRONG | DEVICE_CREDENTIAL`

### Documentation

- **SPEC-002-biometric-login.md**: Actualizado a estado ✅ COMPLETADO
- **SPEC-001-register-screen.md**: Actualizado con componentes reutilizables
- **SPEC-003-plan-implementation-register.md**: Nuevo plan de implementación paso a paso

---

## [0.3.0] - 2026-01-09

### Added

#### 🔐 Authentication System
- **Login completo** con backend real
  - Conexión a `https://devtukychloe.ddns.net/api/v1/token`
  - Form-urlencoded authentication
  - Manejo de tokens JWT (access + refresh)
  - Almacenamiento seguro con SharedPreferences

- **LoginUseCase**: Orquesta validación y autenticación
- **ValidateEmailUseCase**: Validación de email
- **ValidatePasswordUseCase**: Validación de contraseña
- **GetCurrentUserUseCase**: Obtiene usuario logueado
- **LogoutUseCase**: Cierre de sesión

#### 🌐 Network Layer (Retrofit + OkHttp)
- **GoodLifeApiService**: Interface Retrofit con endpoints
  - `POST /api/v1/token` - Login
  - `POST /api/v1/register` - Registro
  
- **GoodLifeInterceptor**: Interceptor personalizado
  - Agrega `Authorization: Bearer` automáticamente
  - Logging detallado de URL, request y response
  - Emojis para fácil identificación en Logcat
  - Skip de endpoints públicos

- **NetworkConstants**: URL base y endpoints públicos
- **HttpCode**: Enum con códigos HTTP del backend
- **ApiException**: Excepción personalizada para errores de API

- **DTOs**:
  - `BaseResponse<T>`: Wrapper genérico del backend
  - `AuthResponse`: Respuesta de login (accessToken, refreshToken)
  - `RegisterRequest/Response`: Registro de usuario

#### 💾 Database Layer (Room)
- **GoodLifeDatabase**: Base de datos con patrón Singleton thread-safe
  - `@Volatile` + `synchronized` para thread safety
  - `getInstance(context)` para acceso global
  - `fallbackToDestructiveMigration()` para desarrollo

- **UserEntity**: Entidad para almacenar usuario logueado
- **UserDao**: Operaciones CRUD de usuario
  - `insertUser()`, `getUser()`, `deleteUser()`

- **Mappers como extension functions**:
  - `UserEntity.toDomain()` → `User`
  - `User.fromDomain()` → `UserEntity`
  - `AuthResponse.toAuthToken()` → `AuthToken`
  - `RegisterResponse.toUser()` → `User`

#### 🏠 Home Screen
- **HomeViewModel**: Carga usuario y maneja logout
- **HomeScreen**: Pantalla de bienvenida
  - Mensaje "¡Bienvenido, [nombre]!"
  - Botón de cerrar sesión
  - Estados: Loading, Content, Error

- **HomeUiState** / **HomeUiAction**: Extraídos a archivos separados

#### 📦 Dependency Injection
- **NetworkModule**: OkHttpClient, Retrofit, ApiService, Json
- **DatabaseModule**: GoodLifeDatabase, UserDao
- **AppModule actualizado**: UseCases, DataSources, Repository

#### 🎨 Compose Optimizations
- **@Stable** en todos los UI States y Actions
  - `LoginUiState`, `LoginUiAction`
  - `HomeUiState`, `HomeUiAction`
  - `SplashUiState`, `SplashUiAction`
  
- Previene recomposiciones innecesarias

#### 🔧 Infrastructure
- **TokenManager**: Almacena tokens en SharedPreferences
- **AuthRemoteDataSource**: Ejecuta llamadas API y mapea responses
- **AuthRepositoryImpl**: Implementación completa del repositorio
  - Login → API → Save tokens → Save user to Room
  - Logout → Clear tokens → Delete user from Room

### Changed
- **AndroidManifest.xml**: Agregados permisos INTERNET y ACCESS_NETWORK_STATE
- **build.gradle.kts**: Agregadas dependencias Room, OkHttp, KSP
- **libs.versions.toml**: Versiones de Room 2.7.0-alpha03, OkHttp 4.12.0

### Technical Decisions
- **Singleton en GoodLifeDatabase**: Thread-safe con double-checked locking
- **Extension functions para mappers**: Más idiomático en Kotlin
- **@Stable annotations**: Optimización de recomposición en Compose
- **Mensajes de error del backend**: No hardcodeados, vienen del BaseResponse

---

## [0.2.0] - 2026-01-08

### Added

#### Theme System
- **Font.kt**: Familias de fuentes personalizadas
  - `UbuntuFontFamily`: Light, Regular, Medium, Bold (para títulos)
  - `InterFontFamily`: Light, Regular, Medium, SemiBold, Bold (para body text)

- **res/font/**: 9 archivos de fuentes TTF
  - Ubuntu: `ubuntu_light`, `ubuntu_regular`, `ubuntu_medium`, `ubuntu_bold`
  - Inter: `inter_light`, `inter_regular`, `inter_medium`, `inter_semibold`, `inter_bold`

- **Color.kt**: Paleta de colores completa
  - Colores primarios: `DarkGreen`, `LightGreen`, `GreenSelected`, `EmeraldGreen`
  - Gradientes de fondo: `DegradeBackground1-5`
  - Colores semánticos: `SuccessGreen`, `ErrorRed`, `WarningOrange`, `InfoBlue`
  - Colores de UI: texto, superficies, bordes, botones
  - Colores para modo oscuro
  - Brushes reutilizables: `ButtonEnabledGradientBrush`, `InputBorderNormalGradientBrush`, etc.
  - Listas de colores para estados: `ButtonColorsEnabled`, `InputBorderColorsNormal`, etc.

- **Type.kt**: Sistema tipográfico Material 3 completo
  - Display, Headline, Title con Ubuntu
  - Body, Label con Inter
  - Estilos personalizados: `AppTitleStyle`, `PlaceholderStyle`, `ButtonTextStyle`, `LinkTextStyle`

- **Theme.kt**: Temas claro y oscuro
  - `GoodLifeTheme()`: Composable principal
  - `LightColorScheme` y `DarkColorScheme` completos
  - Configuración automática de status bar
  - `ExtendedColors` para colores adicionales

### Changed
- Deshabilitado Dynamic Color para mantener identidad de marca

---

## [0.1.0] - 2026-01-07

### Added

#### Core Layer
- **Result.kt**: Wrapper genérico para operaciones que pueden fallar (Success, Error, Loading)
  - Funciones de extensión: `map()`, `resultOf()`, `suspendResultOf()`
  - Métodos encadenables: `onSuccess()`, `onError()`, `onLoading()`

- **DispatcherProvider.kt**: Abstracción de dispatchers de coroutines
  - Interface para inyección de dependencias y testing
  - Implementación Android con Dispatchers.Main, IO, Default
  - Preparado para Kotlin Multiplatform

#### Navigation System (Reactive Pattern)
- **NavigationAction.kt**: Sealed class para acciones de navegación
  - `NavigateTo<T>`: Navegación type-safe con NavOptions
  - `NavigateUp`: Retroceso en el stack
  - `PopBackTo<T>`: Pop hasta una ruta específica

- **ComposeNavigationController.kt**: Interface para navegación reactiva
  - Desacopla ViewModels del NavController
  - Expone SharedFlow para eventos de navegación

- **ComposeNavigationControllerImpl.kt**: Implementación con SharedFlow
  - Thread-safe con CoroutineScope
  - NonCancellable para garantizar emisión de eventos

- **SmartNavHost.kt**: NavHost lifecycle-aware
  - Observa SharedFlow con `repeatOnLifecycle(STARTED)`
  - Animaciones de transición preconfiguradas (slide + fade)
  - Previene navegación cuando Activity está en background

#### Type-Safe Routes
- **AppRoute.kt**: Rutas de nivel aplicación
  - Splash, Login, Register, Main
  - Todas con `@Serializable` para type-safety

- **TabRoute.kt**: Rutas dentro del sistema de tabs
  - Home, Workouts, Meals, Settings
  - Rutas con parámetros (WorkoutDetail, MealDetail, TaskDetail)
  - TabGraphRoute para grafos anidados

- **NavigationExtensions.kt**: Extension functions para navegación común
  - `navigateToLogin()`, `navigateToMain()`, `navigateToRegister()`
  - `navigateToWorkoutDetail()`, `navigateToMealDetail()`, etc.

- **AppGraph.kt**: Definición del grafo de navegación principal

#### Dependency Injection
- **AppModule.kt**: Módulo principal de Koin
  - Provisión de DispatcherProvider
  - Provisión de ComposeNavigationController

- **GoodLifeApp.kt**: Application class
  - Inicialización de Koin con androidContext

#### Infrastructure
- **MainActivity.kt**: Activity principal actualizada
  - Single Activity Architecture
  - Integración con SmartNavHost
  - Edge-to-edge display

- **AndroidManifest.xml**: Actualizado con GoodLifeApp

### Changed
- Movido theme de `ui/theme` a `presentation/theme`
- Actualizado `build.gradle.kts` con dependencias de navegación y serialización
- Actualizado `libs.versions.toml` con versiones de librerías

### Technical Decisions
- **SharedFlow vs StateFlow**: SharedFlow para eventos one-shot (navegación), StateFlow para UI state
- **repeatOnLifecycle**: Previene procesamiento de eventos cuando UI no está visible
- **Type-safe routes**: Eliminación de strings mágicos con `@Serializable`
- **Owner Pattern**: Separación de inyección de dependencias y UI pura

---

## Convenciones de Commits

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bugs
- `docs:` Cambios en documentación
- `style:` Formateo, sin cambios de código
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Tareas de mantenimiento
