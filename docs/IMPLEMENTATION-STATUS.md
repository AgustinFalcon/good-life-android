# Estado de implementación — GoodLife Android

> **Estado documental — 2026-09-10:** este registro es histórico. La fuente canónica de estado es [REVIVAL-READINESS-2026-09.md](REVIVAL-READINESS-2026-09.md); no usar los porcentajes ni pendientes de este archivo para planificar trabajo nuevo.

**Inventario por ingeniería inversa del código.**  
**Fecha:** 2026-05-14 — Si este documento y `app/src/main/java` discrepan, **manda el código**.

---

## 1. Resumen ejecutivo

| Área | Estado en código |
|------|------------------|
| **Stack** | Kotlin, Jetpack Compose, Material 3, Coroutines, StateFlow, Koin, Room, Retrofit, kotlinx-serialization (rutas) |
| **Arquitectura** | Clean + MVVM; dominio Kotlin puro; navegación reactiva vía `ComposeNavigationController` + `SharedFlow` |
| **Actividad** | Una sola `MainActivity` (`FragmentActivity` por BiometricPrompt) |
| **Persistencia local** | Room v2: `UserEntity`, `DailyLogEntity`, `DailyItemEntity`; cache SWR de Daily en `DailyRepositoryImpl` |
| **Tests unitarios** | 12 archivos bajo `app/src/test` (incl. `ExampleUnitTest`); foco en UseCases y ViewModels clave |

---

## 2. Navegación

### 2.1 Rutas de aplicación (`AppRoute`)

Implementadas en `presentation/navigation/route/AppRoute.kt` y registradas en `AppGraph.kt`:

| Ruta | Pantalla (Owner) |
|------|------------------|
| `Splash` | `SplashScreenOwner` |
| `Login` | `LoginScreenOwner` |
| `Register` | `RegisterScreenOwner` |
| `Main` | `MainScaffoldScreenOwner` |
| `CreateTask` | `CreateTaskScreenOwner` |
| `CreateHabit` | `CreateHabitScreenOwner` |
| `CreateRoutine` | `CreateRoutineScreenOwner` |
| `CreateMealPlan` | `CreateMealPlanScreenOwner` |

**Host:** `GoodLifeNavHost` (no existe `SmartNavHost` en el código).

### 2.2 Tabs (`TabNavGraph.kt`)

Grafos anidados con `NavHost` dentro de `MainScaffoldScreen`:

| Tab | `TabGraphRoute` | Contenido real |
|-----|-----------------|----------------|
| Home / Daily | `DailyGraph` | `DailyScreenOwner` + detalle con placeholder |
| Workouts | `WorkoutsGraph` | Placeholder + detalle placeholder |
| Meals | `MealsGraph` | Placeholder + detalle placeholder |
| More / Settings | `SettingsGraph` | Placeholder `Settings` / `Profile` |

Rutas tipadas definidas en `TabRoute.kt` (incl. detalles con ids).

### 2.3 FAB / acciones rápidas

`MainScaffoldViewModel`: modal fullscreen + grid; navegación a `CreateTask`, `CreateHabit`, `CreateRoutine`, `CreateMealPlan`.  
**Pendiente en código:** `QuickActionType.OTHER` (TODO); acciones de la fila “meal options” del modal cierran modal sin navegar (revisar producto).

### 2.4 Deep links / FCM

**No implementado** en el grafo Compose (SPEC-008 planificado).

---

## 3. Capa domain

### 3.1 Repositorios (interfaces)

`domain/repository/`: `AuthRepository`, `DailyRepository`, `TaskRepository`, `HabitRepository`, `RoutineRepository`, `TrainingCatalogRepository`, `NutritionCatalogRepository`, `MealPlanRepository`.

### 3.2 Implementaciones `data/repository/`

Ocho archivos `*RepositoryImpl.kt` — uno por interfaz anterior.

### 3.3 Use cases (muestra por vertical)

- **Auth / sesión:** login, register, validaciones, `CheckSession`, `GetCurrentUser`, `Logout`, biométrica (`BiometricLoginHandler` en domain).
- **Daily:** `GetDailyItems`, `UpdateItemStatus` (+ result sellados).
- **Task / Habit:** `CreateTask`, `CreateHabit` (+ results).
- **Routine / training:** `CreateRoutine`, `SearchExercises`, `GetMuscleGroups`, `ActivateRoutine` (+ results).
- **Nutrition:** `SearchIngredients`, `SearchMeals`, `CreateCustomIngredient`, `CreateCustomMeal`, `CreateMealPlan` (+ results).

Lista completa: inspeccionar `domain/usecase/**`.

---

## 4. Capa data — API (Retrofit)

Servicios en `data/remote/api/`:

- `auth/AuthApiService`
- `daily/DailyApiService`
- `task/TaskApiService`
- `habit/HabitApiService`
- `training/RoutineApiService`, `training/TrainingCatalogApiService`
- `nutrition/NutritionCatalogApiService`, `nutrition/MealPlanApiService`

DataSources remotos por feature; `executeApiCall` / `BaseResponse` según SPEC-005.

---

## 5. Capa data — Room

`GoodLifeDatabase` (v2): `UserDao`, `DailyDao`; entidades `UserEntity`, `DailyLogEntity`, `DailyItemEntity`.  
**SWR lectura** para Daily documentado en `DailyRepositoryImpl`.  
**No** hay cola de escritura offline global (SPEC-006 Fase 2 pendiente).

---

## 6. Inyección de dependencias (Koin)

Módulos en `di/`:

`AppModule` (coreModule), `NetworkModule`, `DatabaseModule`, `BiometricModule`, `AuthModule`, `DailyModule`, `TaskModule`, `HabitModule`, `RoutineModule`, `NutritionModule`.

Orden de carga: `GoodLifeApp.initKoin()`.

---

## 7. Presentation — `*ScreenOwner` (9)

| Ubicación |
|-----------|
| `splash/SplashScreenOwner` |
| `login/LoginScreenOwner` |
| `register/RegisterScreenOwner` |
| `main/MainScaffoldScreenOwner` |
| `tabs/daily/DailyScreenOwner` |
| `add/task/CreateTaskScreenOwner` |
| `add/habit/CreateHabitScreenOwner` |
| `add/routine/CreateRoutineScreenOwner` |
| `add/mealplan/CreateMealPlanScreenOwner` |

---

## 8. Core / platform

- **Core:** `Result`, `DispatcherProvider`, `DateProvider`, `AppLanguage`, red (`Interceptor`, `Authenticator`, `SessionEventBus`), `TokenManager`, etc.
- **Platform:** `AndroidBiometricAuthenticator`, `AndroidSecureCredentialsStorage` (implementaciones Android de contratos domain/core).

---

## 9. Gaps conocidos (código)

1. Tabs Workouts / Meals / Settings: UI real + ViewModels.
2. Detalles `DailyDetail`, `WorkoutDetail`, `MealDetail`: placeholders.
3. SPEC-006 Fase 2: sync de escrituras offline (Tasks/Habits/etc. no siguen el mismo patrón SWR que Daily).
4. SPEC-008: notificaciones y `goodlife://`.
5. Documentación histórica puede nombrar archivos o conteos viejos — usar este archivo + fuentes citadas.

---

## 10. Referencias cruzadas

- Índice SPECs: [README.md](README.md)
- Progreso narrativo: [PROGRESS.md](PROGRESS.md)
- Arquitectura detallada: [ARCHITECTURE.md](ARCHITECTURE.md) / [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md)
