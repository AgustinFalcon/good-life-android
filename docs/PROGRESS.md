# Progreso de Implementación - GoodLife Android

> Última actualización: 2026-02-24

---

## 📊 Estado General

| Feature | Estado | Progreso |
|---------|--------|----------|
| Splash Screen | ✅ Completado | 100% |
| Login Screen | ✅ Completado | 100% |
| Register Screen | ✅ Completado | 100% |
| Biometric Login | ✅ Completado | 100% |
| DateProvider (SPEC-004) | ✅ Completado | 100% |
| Network Service (SPEC-005) | ✅ Completado | 100% |
| Localización AppLanguage (SPEC-007) | ✅ Completado | 100% |
| Main Scaffold (SPEC-003) | 🚧 En Progreso | 90% |
| **Daily Tab — Arquitectura** | ✅ Completado | 100% |
| **Daily Tab — DailyItemCard** | ✅ Completado | 100% |
| Workouts Tab | ⏸️ Pendiente | 0% |
| Meals Tab | ⏸️ Pendiente | 0% |
| More/Settings Tab | ⏸️ Pendiente | 0% |
| Notificaciones + Deep Links (SPEC-008) | 📝 Planificado | 0% |

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

## 📋 Próximos Pasos

### Corto plazo

1. Implementar WorkoutsTabScreen + WorkoutsTabViewModel
2. Implementar MealsTabScreen + MealsTabViewModel
3. Implementar MoreTabScreen + MoreTabViewModel
4. `EncryptedSharedPreferences` en `TokenManager` (crítico para seguridad)

### Mediano plazo

5. Backend Integration: conectar `DailyTabViewModel` con API real
6. Offline-First SWR en `DailyRepositoryImpl` (SPEC-006)
7. Deep links: implementar `AndroidManifest.xml` + Navigation (SPEC-008)
8. Tests unitarios: DailyTabViewModel, LoginViewModel

### Largo plazo

9. Dark mode completo
10. Profile screen / Settings
11. Migración a KMP (shared module)
12. iOS target

---

## 📚 Referencias

- [SPEC-003: Main Scaffold](./specs/SPEC-003-main-scaffold.md)
- [SPEC-004: DateProvider](./specs/SPEC-004-date-provider.md)
- [SPEC-007: AppLanguage](./specs/SPEC-007-app-language.md)
- [SPEC-008: Notificaciones + Deep Links](./specs/SPEC-008-notifications-deeplinks.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [DAILY-IMPLEMENTATION-PLAN](./plans/DAILY-IMPLEMENTATION-PLAN.md)
