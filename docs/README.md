# 📚 Documentación GoodLife Android

Índice de especificaciones y guías del proyecto.

**Inventario alineado al código:** [IMPLEMENTATION-STATUS.md](IMPLEMENTATION-STATUS.md) (ingeniería inversa + gaps).

---

## 🎯 Especificaciones (SPECs)

Documentación oficial de features core implementadas.

| SPEC | Feature | Estado | Descripción |
|------|---------|--------|-------------|
| [SPEC-001](specs/SPEC-001-register-screen.md) | Register Screen | ✅ Completado | Pantalla de registro |
| [SPEC-002](specs/SPEC-002-biometric-login.md) | Biometric Login | ✅ Completado | Autenticación biométrica |
| [SPEC-003](specs/SPEC-003-main-scaffold.md) | Main Scaffold | 🚧 En progreso (~90%) | Bottom nav + FAB + grafos por tab; **Daily** real; **Workouts / Meals / Settings** placeholders en `TabNavGraph.kt` |
| [SPEC-004](specs/SPEC-004-date-provider.md) | DateProvider | ✅ Completado | Core de fechas (KMP-ready) |
| [SPEC-005](specs/SPEC-005-network-service.md) | Network Service | ✅ Completado | BaseResponse, HttpCode, Cache |
| [SPEC-006](specs/SPEC-006-offline-first-swr.md) | Offline-First + SWR | Fase 1 ✅ / Fase 2 📝 | Fase 1: Daily+Room en código; Fase 2: cola escritura offline (pendiente) |
| [SPEC-007](specs/SPEC-007-app-language.md) | AppLanguage | ✅ Completado | Localización KMP-ready (ES/EN/PT) |
| [SPEC-008](specs/SPEC-008-notifications-deeplinks.md) | Notificaciones + Deep Links | 📝 Planificado | FCM + esquema goodlife:// |
| [SPEC-009](specs/SPEC-009-add-to-daily.md) | Add to Daily | ✅ Navegación desde FAB (core) | Modal + acciones → CreateTask / CreateHabit / CreateRoutine / CreateMealPlan; pendientes: `QuickActionType.OTHER`, fila de opciones de comida del modal, checklist detallada en el SPEC |
| [SPEC-010](specs/SPEC-010-create-task-screen.md) | Create Task Screen | ✅ Completado | Wizard once/recurrente |
| [SPEC-011](specs/SPEC-011-create-routine-screen.md) | Create Routine Screen | ✅ Completado | Wizard 4 pasos, catálogo paginado |
| [SPEC-012](specs/SPEC-012-create-meal-plan-screen.md) | Create Meal Plan Screen | ✅ Completado | Wizard 3 pasos, macros, scheduling |

---

## 📋 Planes de Implementación

Guías paso a paso para implementar features complejas.

| Plan | Feature | Estado | Descripción |
|------|---------|--------|-------------|
| [DAILY-IMPLEMENTATION-PLAN](plans/DAILY-IMPLEMENTATION-PLAN.md) | Daily Screen | ✅ Implementado en código (ajustar plan) | LazyColumn + `DailyRepositoryImpl` con **Room + SWR** (`DailyDao`); el plan sigue como guía histórica / mejoras futuras |

---

## 📝 Documentación Archivada

Documentación de proceso preservada como referencia histórica.

- [archive/README.md](archive/README.md) - Índice de documentos archivados
- [archive/ANALISIS-CLOCK-Y-MEJORAS.md](archive/ANALISIS-CLOCK-Y-MEJORAS.md) - Análisis del problema de Clock
- [archive/DATEPROVIDER-IMPLEMENTACION-COMPLETA.md](archive/DATEPROVIDER-IMPLEMENTACION-COMPLETA.md) - Tracking de implementación
- [archive/INSTRUCCIONES-VALIDACION.md](archive/INSTRUCCIONES-VALIDACION.md) - Checklist de validación

---

## 🚀 Guías Rápidas

### Serialización de Fechas para API

```kotlin
// ✅ CORRECTO: LocalDate.toString() devuelve ISO-8601
val date: LocalDate = dateProvider.today()  // LocalDate(2026, 2, 3)
val dateString = date.toString()            // "2026-02-03" ✅

// Request al backend
apiService.getDailyLog(dateString)  // GET /daily-logs/2026-02-03

// Parse de response
val responseDate = LocalDate.parse("2026-02-03")  // ✅ Parse directo
```

**Importante:** NO se necesita formateo adicional. `kotlinx.datetime.LocalDate.toString()` ya devuelve formato ISO-8601 (`YYYY-MM-DD`), compatible con el backend.

**Ver más:** [SPEC-004 § 16. Serialización](specs/SPEC-004-date-provider.md#16-serialización-para-api) y [SPEC-005 § 6. Formato de Fechas](specs/SPEC-005-network-service.md#6-formato-de-fechas-iso-8601)

### Stale-While-Revalidate (SWR)

```kotlin
// ✅ PATRÓN SWR: Cache first + Background revalidation (SPEC-006)
override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
    val cached = getCachedDailyLog(date)  // 1. Cache PRIMERO (0ms)
    
    try {
        val response = apiService.getDailyLog(date)  // 2. Backend SIEMPRE
        
        if (HttpCode.isSuccess(response.code)) {
            val fresh = response.data.toDomain()
            saveDailyLogToCache(fresh)  // 3. Actualizar cache
            return Result.success(fresh)
        }
    } catch (e: IOException) {
        // Sin internet: cache es suficiente ✅
    }
    
    return cached ?: throw NoDataAvailableException()
}
```

**Beneficios:**
- UI instantánea (0ms)
- Backend como fuente de verdad
- Funciona offline
- Data siempre actualizada

**Ver más:** [SPEC-006: Offline-First + SWR](specs/SPEC-006-offline-first-swr.md)

### Uso de DateProvider

```kotlin
class MyViewModel(
    private val dateProvider: DateProvider
) : ViewModel() {
    
    // Obtener fecha actual (timezone local)
    val today = dateProvider.today()
    
    // Obtener ayer/mañana
    val yesterday = dateProvider.yesterday()
    val tomorrow = dateProvider.tomorrow()
    
    // Formatear para UI
    val headerText = when (someDate) {
        today -> "Hoy"
        yesterday -> "Ayer"
        tomorrow -> "Mañana"
        else -> someDate.format(...)
    }
}
```

### Testing con FakeDateProvider

```kotlin
@Test
fun `muestra "Hoy" cuando la fecha es hoy`() {
    // Given
    val fixedDate = LocalDate(2025, 12, 25)
    val fakeProvider = FakeDateProvider(fixedDate)
    
    // When
    val viewModel = MyViewModel(fakeProvider)
    
    // Then
    assertEquals("Hoy", viewModel.headerText)
}
```

---

## 📊 Arquitectura del Proyecto

```
app/
├── domain/          ← Lógica de negocio pura
│   ├── model/      
│   ├── repository/ 
│   └── usecase/    
│
├── data/            ← Acceso a datos
│   ├── remote/     ← Retrofit
│   ├── local/      ← Room
│   └── repository/ 
│
├── presentation/    ← UI Layer
│   ├── screen/     
│   ├── components/ 
│   └── navigation/ 
│
├── core/            ← Utilidades
│   ├── datetime/   ← DateProvider, AppLanguage
│   ├── network/    
│   └── result/     
│
└── di/              ← Koin
```

---

## 🎯 Patrones del Proyecto

### 1. Owner Pattern

```
FeatureScreenOwner.kt  → ViewModel injection
FeatureScreen.kt       → UI pura
FeatureViewModel.kt    → State management
```

### 2. DateProvider Pattern

```kotlin
// ✅ Usar DateProvider (no Clock directamente)
class ViewModel(private val dateProvider: DateProvider)

// ✅ Formatear en ViewModel (no en UI)
data class UiState(
    val headerText: String,  // "Hoy" (ya formateado)
    val dayNumber: Int
)
```

### 3. Sealed Classes

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(...) : UiState()
    data class Error(val message: String) : UiState()
}
```

---

## 📞 Referencias

- **Estado del código:** [IMPLEMENTATION-STATUS.md](IMPLEMENTATION-STATUS.md)
- **Changelog:** [CHANGELOG.md](../CHANGELOG.md)
- **Specs:** [specs/](specs/)
- **Archive:** [archive/](archive/)

---

**Última actualización:** 2026-05-14 — Índice alineado con el código (`DailyRepositoryImpl`, `TabNavGraph`, FAB).

---

## Notas de sincronización doc ↔ código

- Si un SPEC o plan **no coincide** con `app/src/main/java`, prima el **código** y actualizá el índice o el SPEC en el mismo espíritu que este commit.
- **SPEC-008** (FCM + `goodlife://`) sigue **planificado**; no hay implementación en el grafo de navegación aún más allá de rutas definidas en documentación.
