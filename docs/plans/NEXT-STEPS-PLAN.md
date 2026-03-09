# Plan de Siguientes Pasos — GoodLife Android

> Generado: 2026-03-04
> Contexto: Se completó Create Task (SPEC-010), mejoras biométricas, SWR Fase 1, tests unitarios (60+), ImmutableList, @Stable/@Immutable, extension functions, y reducción de strings hardcodeados.

---

## Roadmap General

```
Fase 1: Create Habit Screen ◄── PRÓXIMO
    │
    ▼
Fase 2: Create Routine/Workout Screen
    │
    ▼
Fase 3: Create Meal Plan Screen
    │
    ▼
Fase 4: Tabs restantes (Workouts, Meals, More/Settings)
    │
    ▼
Fase 5: SWR Fase 2 — Cola de Sync Offline (SPEC-006)
    │
    ▼
Fase 6: Backend Hardening (Rate Limiting, Caching)
    │
    ▼
Fase 7: Pulido (Dark Mode, Estadísticas, Push Notifications)
```

---

## Fase 1 — Create Habit Screen (PRÓXIMO)

**Objetivo:** Crear hábitos recurrentes con meta numérica y tracking de progreso.

### Backend disponible (✅ Implementado)

- `POST /api/v1/habits` → Crear hábito
- `GET /api/v1/habits` → Listar hábitos (filtro `active=true/false`)
- `PATCH /api/v1/habits/{id}` → Actualizar hábito
- `PATCH /api/v1/habits/{id}/activate` → Activar/Desactivar
- `PATCH /api/v1/daily-logs/habit-logs/{habitLogId}` → Actualizar progreso (currentValue)
- Categorías: HYDRATION, MEDITATION, READING, EXERCISE, SLEEP, NUTRITION, LEARNING, MINDFULNESS, SOCIAL, CREATIVITY, PRODUCTIVITY, HEALTH, CUSTOM

### Diferencias con Create Task

| Aspecto | Task | Habit |
|---------|------|-------|
| Recurrencia | Opcional (una vez / se repite) | Siempre recurrente (daysOfWeek) |
| Meta numérica | No tiene | targetValue + unit (ej: 8 vasos) |
| Categoría | No tiene | Enum predefinido (HYDRATION, etc.) |
| Tracking | COMPLETED / PENDING | currentValue / targetValue (progreso gradual) |
| Streak | No | Sí (futuro) |

### Qué reutilizar de Create Task

- `SwitchComponent`, `DayChipComponent`, `DateSelectorComponent`, `TimeSelectorComponent`
- `TimePickerDialogComponent`, `DatePickerBottomSheetComponent`
- `SnackbarComponent`, `SuccessDialog`
- Patrón `*Params` + `*Component`
- `CreateItemSharedTexts` (labels compartidos)

### Qué crear nuevo

- `CreateHabitScreen.kt` + `CreateHabitScreenOwner.kt` + `CreateHabitViewModel.kt`
- `CreateHabitUiState.kt` + `CreateHabitUiAction.kt`
- `CreateHabitUseCase.kt` + `CreateHabitResult.kt`
- Domain model: `Habit.kt` + `HabitCategory.kt`
- `HabitApiService.kt` + `HabitRemoteDataSource.kt` + `HabitRepositoryImpl.kt`
- `HabitRepository.kt` (interfaz de dominio)
- `HabitModule.kt` (Koin)
- `CreateHabitRequest.kt` (DTO)
- `AppRoute.CreateHabit` + registrar en `AppGraph`
- Conectar `QuickActionType.HABIT` en `MainScaffoldViewModel`
- `CreateHabitTexts` en `UiTexts.kt` + traducciones (ES/EN/PT)
- Componentes nuevos: selector de categoría (chips o dropdown), input de meta numérica (targetValue + unit)

### Estimación: 6-8 horas

---

## Fase 2 — Create Routine/Workout Screen

**Objetivo:** Crear rutinas semanales con workouts y ejercicios.

### Backend disponible (✅ Implementado)

- `POST /api/v1/routines` → Crear rutina
- `POST /api/v1/routines/{id}/workouts` → Agregar workout a rutina
- `POST /api/v1/workouts/{id}/exercises` → Agregar ejercicios con sets
- `PATCH /api/v1/routines/{id}/activate` → Activar rutina
- `GET /api/v1/exercise-catalog/muscle-groups` → Catálogo de grupos musculares
- `GET /api/v1/exercise-catalog/muscle-groups/{id}/exercises` → Ejercicios por grupo

### Complejidad

Esta pantalla es la más compleja del proyecto: es un wizard multi-paso (crear rutina → agregar workouts por día → agregar ejercicios con sets a cada workout). Considerar un flujo step-by-step o un diseño con tabs.

### Estimación: 10-14 horas

---

## Fase 3 — Create Meal Plan Screen

**Objetivo:** Planificar comidas con ingredientes y cálculo de macros.

### Backend disponible (✅ Implementado)

- `GET /api/v1/ingredients` → Catálogo de ingredientes
- `POST /api/v1/ingredients` → Crear ingrediente custom
- `POST /api/v1/meals` → Crear meal (receta con ingredientes)
- `POST /api/v1/meal-plans` → Crear plan de comidas con scheduling
- Macros calculados automáticamente (calorías, proteínas, carbos, grasas)

### Estimación: 8-10 horas

---

## Fase 4 — Tabs Restantes

### 4a. Workouts Tab

- `WorkoutsTabScreen` + `WorkoutsTabViewModel`
- Listado de rutinas y próximo workout del día
- Pantalla de ejecución de workout (registrar sets/reps)

### 4b. Meals Tab

- `MealsTabScreen` + `MealsTabViewModel`
- Listado de comidas del día
- Resumen de macros diarios

### 4c. More/Settings Tab

- `MoreTabScreen` + `MoreTabViewModel`
- Profile, configuración, logout
- Selector de idioma
- Toggle dark mode

### Estimación total: 10-12 horas

---

## Fase 5 — SWR Fase 2: Cola de Sincronización Offline (SPEC-006)

**Prerequisito:** Todas las pantallas de creación (Task, Habit, Routine, Meal) deben existir primero. El DailyLog ya muestra todos los tipos de items.

**Objetivo:** Que los cambios de status (COMPLETED, SKIPPED) no se pierdan si no hay internet.

### Qué incluye

- `syncStatus` en `DailyItemEntity` (SYNCED / PENDING_SYNC)
- Tabla `pending_sync` + `PendingSyncDao`
- `SyncPendingChangesUseCase` (batch de envío)
- Botón "Sincronizar" en la UI (manual)
- `SyncPendingWorker` (WorkManager cada 15 min con internet)
- `CleanupCacheWorker` (garbage collection de cache >7 días)
- Tests unitarios del flujo de sync

### Detalle completo

Ver `SPEC-006-offline-first-swr.md` Sección 4.

### Estimación: 6-8 horas

---

## Fase 6 — Backend Hardening

### Mejoras documentadas en `back-end/GoodLife-backend-v2/docs/mejoras/`

1. **Rate Limiting** (Nginx/Bucket4j) → Protección contra abuso de API y DDoS
2. **Caching** (Spring Cache / Redis) → Reducir queries a PostgreSQL
3. **Security Headers** → HSTS, X-Frame-Options, CSP
4. **Refresh Token Invalidation** → Logout seguro
5. **GlobalExceptionHandler** → Responses de error consistentes

### Estimación: 4-6 horas

---

## Fase 7 — Pulido y Features Avanzados

- Dark mode completo
- Estadísticas de hábitos (streaks, gráficos de progreso)
- Push notifications (FCM)
- Deep links (SPEC-008) — implementar con push notifications
- Migración a KMP (shared module)
- iOS target

---

## Lo que ya está hecho ✅

| Feature | Estado |
|---------|--------|
| Splash + Login + Register | ✅ |
| Biometric Login (refactored, Channel events, EncryptedSharedPreferences) | ✅ |
| Main Scaffold + Bottom Nav | ✅ 90% |
| Daily Tab completo (DailyItemCard + refresh) | ✅ |
| Create Task Screen (SPEC-010) | ✅ |
| SWR Fase 1 (Room cache + backend fallback) | ✅ |
| 7 Componentes reutilizables (Switch, DayChip, DateSelector, etc.) | ✅ |
| Unit Tests (60+ tests: domain + data + presentation) | ✅ |
| ImmutableList en UiStates | ✅ |
| @Stable en states, @Immutable en modelos de lista | ✅ |
| Extension functions (formatArgs, toDisplayString, findFragmentActivity, etc.) | ✅ |
| Reducción de strings hardcodeados → AppLanguage | ✅ |
| SPEC-006 v2.0 (SWR + Sync Offline documentado) | ✅ |

---

## Prompt para el Próximo Chat

```
Estoy desarrollando GoodLife, una app Android con Kotlin + Jetpack Compose.

## Arquitectura
- Clean Architecture + MVVM + DDD
- Capas: presentation → domain → data
- Patrón Owner: Screen (UI pura) + ScreenOwner (orquestación) + ViewModel (lógica)
- DI con Koin (modules por feature)
- Navegación con ComposeNavigationController + SharedFlow
- Localización KMP-ready con AppLanguage sealed interface (sin stringResource)
- Componentes reutilizables: data class *Params + @Composable *Component
- SWR Backend-First: backend como fuente de verdad + Room como fallback offline
- @Stable en UiStates, @Immutable en modelos de lista, ImmutableList para evitar recomposición

## Lo que ya existe
- Login + Register + Biometric login (Channel events, EncryptedSharedPreferences)
- Main Scaffold con bottom nav (Daily, Workouts, Meals, More tabs)
- Daily Tab completo con DailyItemCard, refresh automático, SWR con Room
- Create Task Screen COMPLETO (SPEC-010):
  - Modos: Una vez (fecha puntual) / Se repite (días de semana)
  - DatePicker, TimePicker, Success/Error feedback
  - Componentes: SwitchComponent, DayChipComponent, DateSelectorComponent,
    TimeSelectorComponent, TimePickerDialogComponent, DatePickerBottomSheetComponent,
    SnackbarComponent, SuccessDialog
  - Backend: POST /api/v1/tasks → 201 Created
- 60+ unit tests (domain + data + presentation layers)
- Extension functions: formatArgs, toDisplayString, parseLocalDateOrNull, findFragmentActivity

## Lo que necesito ahora
Implementar la pantalla Create Habit. El backend ya tiene:
- POST /api/v1/habits (name, description, category, targetValue, unit, daysOfWeek, startDate, endDate, scheduledTime)
- Categorías: HYDRATION, MEDITATION, READING, EXERCISE, SLEEP, NUTRITION, LEARNING, MINDFULNESS, SOCIAL, CREATIVITY, PRODUCTIVITY, HEALTH, CUSTOM
- Un hábito siempre es recurrente (daysOfWeek), tiene meta numérica (targetValue + unit)
- El progreso se actualiza via PATCH /api/v1/daily-logs/habit-logs/{habitLogId} con {currentValue}
- Se reutilizan los componentes de Create Task (SwitchComponent, DayChipComponent, etc.)

## Documentación clave
- front-end/Android/docs/ARCHITECTURE.md — arquitectura general
- front-end/Android/docs/ARCHITECTURE_GUIDE.md — guía con reglas y patrones
- front-end/Android/docs/specs/SPEC-010-create-task-screen.md — referencia de Create Task
- front-end/Android/docs/specs/SPEC-006-offline-first-swr.md — SWR + sync offline
- back-end/GoodLife-backend-v2/docs/specs/modules/habits.spec.md — spec del backend de hábitos
- front-end/Android/docs/PROGRESS.md — estado actual del proyecto
- front-end/Android/CHANGELOG.md — historial de cambios
- front-end/Android/docs/plans/NEXT-STEPS-PLAN.md — plan general

## Reglas del proyecto
1. Screens y Components lo más "estúpidos" posible (sin lógica interna)
2. Lógica simple de renderizado (isBlank, isLoading) puede quedarse en el Screen
3. Componentes con patrón: data class XxxParams + @Composable fun XxxComponent
4. Textos siempre desde AppLanguage, nunca stringResource ni hardcoded
5. UseCases son dueños del dispatcher (withContext(dispatcher.io))
6. Result types semánticos por UseCase (no genéricos)
7. Colores del proyecto en Color.kt, fuentes en Type.kt
8. @Stable en UiStates, @Immutable en modelos de lista pura
9. ImmutableList + persistentListOf() en states con listas
10. Backend: Spring Boot + Kotlin, Hexagonal Architecture, JWT RS256
```

---

*Este plan es una guía. Adaptar según prioridades del momento.*
