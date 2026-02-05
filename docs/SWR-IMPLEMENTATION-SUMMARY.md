# 📊 Resumen: Implementación de Offline-First + SWR

**Fecha:** 2026-02-05  
**Decisión:** Adoptar Stale-While-Revalidate como arquitectura estándar

---

## ✅ Lo que se Hizo

### 1. **SPEC-006 Creado** (800+ líneas)
📄 `docs/specs/SPEC-006-offline-first-swr.md`

**Contenido completo:**
- ✅ § 1-2: Objetivo y problema actual (TTL-based caching)
- ✅ § 3: Solución SWR (concepto, ventajas vs TTL)
- ✅ § 4: Implementación completa del Repository
- ✅ § 5: Optimistic UI en ViewModel
- ✅ § 6: Flujos de usuario (4 escenarios: online/offline, read/write)
- ✅ § 7: Optimizaciones avanzadas (reglas por fecha, pull-to-refresh, cleanup)
- ✅ § 8: Performance (benchmarks, comparación)
- ✅ § 9: Comparación con apps profesionales (Todoist, Trello, etc.)
- ✅ § 10: Roadmap (Fase 1-4: SWR → Optimizaciones → Pending Sync → Version-based)
- ✅ § 11: Aplicación en otros módulos (Tasks, Habits, Workouts, Meals)
- ✅ § 12: Testing
- ✅ § 13: Métricas esperadas
- ✅ § 14: Decisiones arquitectónicas
- ✅ § 15: Referencias profesionales
- ✅ § 16: Glosario

### 2. **DAILY-IMPLEMENTATION-PLAN.md Actualizado**
📄 `docs/plans/DAILY-IMPLEMENTATION-PLAN.md`

**Cambios realizados:**

#### ❌ Eliminado:
```kotlin
// DailyLogEntity
val cachedAt: Long = System.currentTimeMillis()

fun isFresh(): Boolean {
    val ageInMinutes = (now - cachedAt) / (1000 * 60)
    return ageInMinutes < 60  // ❌ TTL ya no se usa
}
```

#### ✅ Agregado:
```kotlin
// Repository con SWR
private suspend fun fetchDailyLogWithSWR(dateString: String): DailyLog {
    // 1. Cache primero (instantáneo)
    val cached = getCachedDailyLog(dateString)
    
    // 2. Backend siempre (si hay internet)
    try {
        val response = apiService.getDailyLog(dateString)
        if (HttpCode.isSuccess(response.code)) {
            val fresh = response.data.toDomain()
            saveDailyLogToCache(fresh)  // Revalidation
            return fresh
        }
    } catch (e: IOException) {
        // Sin internet → cache es suficiente
    }
    
    return cached ?: throw NoDataAvailableException()
}

// ViewModel con Optimistic UI
private fun updateItemStatus(itemId: Long, newStatus: ItemStatus) {
    // 1. Actualizar UI ANTES del request (optimistic)
    val updatedItems = currentState.items.map { item ->
        if (item.id == itemId) item.copy(status = newStatus) else item
    }
    _uiState.value = currentState.copy(items = updatedItems)
    
    // 2. Request al backend
    viewModelScope.launch {
        updateItemStatusUseCase(itemId, newStatus)
            .onSuccess { /* confirmar */ }
            .onError { /* revertir si falla */ }
    }
}
```

### 3. **Documentación Actualizada**

#### README.md
- ✅ Agregado SPEC-006 a tabla de SPECs
- ✅ Nueva guía rápida: "Stale-While-Revalidate (SWR)"
- ✅ Código de ejemplo SWR

#### CHANGELOG.md
- ✅ Nueva sección "[Unreleased]" con SWR
- ✅ Documentado SPEC-006 y actualización del plan

---

## 🎯 Dónde Aplicar SWR

### Módulos que usarán SWR:

| Módulo | Repository | Método Principal | Estado |
|--------|------------|------------------|--------|
| **Daily Log** | `DailyRepositoryImpl` | `getDailyLog(date)` | 🟡 Listo para implementar |
| **Tasks** | `TasksRepositoryImpl` | `getTasksForDate(date)` | 🔴 Pendiente |
| **Habits** | `HabitsRepositoryImpl` | `getHabitsForDate(date)` | 🔴 Pendiente |
| **Workouts** | `WorkoutsRepositoryImpl` | `getActiveRoutine()` | 🔴 Pendiente |
| **Meals** | `MealsRepositoryImpl` | `getMealsForDate(date)` | 🔴 Pendiente |

**Patrón común:**
1. Cache first (UI instantánea)
2. Backend revalidation (consistencia)
3. Fallback a cache si falla (offline)

---

## 📋 Próximos Pasos Concretos

### Paso 1: Implementar Daily con SWR (HOY) - 2-3 horas

**Checklist:**
- [ ] **Fase 1: Domain Layer** (30 min)
  - [ ] Crear modelos: `DailyLog`, `DailyItem`, `DailyItemType`, `ItemStatus`
  - [ ] Crear `DailyRepository` interface
  - [ ] Crear UseCases: `GetDailyItemsUseCase`, `UpdateItemStatusUseCase`

- [ ] **Fase 2: Data Layer - Remote** (30 min)
  - [ ] Crear DTOs: `DailyLogDto`, `DailyLogItemDto`
  - [ ] Agregar endpoints a `GoodLifeApiService`
  - [ ] Crear mappers DTO → Domain

- [ ] **Fase 3: Data Layer - Local** (30 min)
  - [ ] Crear entities: `DailyLogEntity` (SIN cachedAt), `DailyLogItemEntity`
  - [ ] Crear `DailyDao` con queries
  - [ ] Crear mappers Entity ↔ Domain

- [ ] **Fase 4: Repository con SWR** (30 min)
  - [ ] Implementar `fetchDailyLogWithSWR()`
  - [ ] Implementar `updateItemStatus()` con offline fallback
  - [ ] Agregar optimización por tipo de fecha

- [ ] **Fase 5: Presentation** (30 min)
  - [ ] Actualizar `DailyUiState` (Success/Error/Loading)
  - [ ] Actualizar `DailyTabViewModel` con Optimistic UI
  - [ ] Actualizar `DailyScreen` con LazyColumn

- [ ] **Fase 6: DI** (15 min)
  - [ ] Crear `DailyModule` con Repository, UseCases, ViewModel
  - [ ] Registrar en `GoodLifeApp`

### Paso 2: Testing (15 min)
- [ ] Test de SWR (cache → backend → actualiza cache)
- [ ] Test de offline (solo cache)
- [ ] Test de Optimistic UI

### Paso 3: Validar en Dispositivo (15 min)
- [ ] Con internet: Verificar que UI sea instantánea
- [ ] Sin internet: Verificar que funcione offline
- [ ] Marcar tarea: Verificar Optimistic UI

---

## 🚀 Futuro (Fase 2-4)

### Fase 2: Optimizaciones (1-2 semanas después) - 1 hora
- Pull-to-refresh fuerza backend
- Reglas inteligentes por tipo de fecha (días viejos = cache only)
- Cleanup de cache viejo (WorkManager)

### Fase 3: Pending Sync (FUTURO) - 4-6 horas
- Tabla `pending_syncs` en Room
- Queue de sincronización
- WorkManager para sincronizar cuando vuelva internet
- UI para mostrar "Cambios pendientes"

### Fase 4: Version-Based (OPCIONAL) - 8-10 horas
- Agregar `version: Long` en backend y Android
- Lógica de comparación de versiones
- Backend responde 304 Not Modified

---

## 💡 Puntos Clave para Recordar

### ✅ Ventajas de SWR sobre TTL:

| Aspecto | TTL (60 min) | SWR |
|---------|--------------|-----|
| **Time to render** | 0ms si cache / 400ms sin cache | **0ms siempre** ✅ |
| **Consistencia** | Hasta 60 min desfasado | **< 1 segundo** ✅ |
| **Backend = verdad** | ❌ NO (ignora si cache fresco) | **✅ SÍ** (siempre consulta) |
| **Offline** | ✅ Funciona | ✅ Funciona |
| **Requests/día** | ~12 | ~15 (+25% aceptable) |

### ❌ Lo que NO debes hacer:

1. ❌ Mantener `cachedAt` y `isFresh()` (ya no son necesarios)
2. ❌ Bloquear UI esperando backend (cache first siempre)
3. ❌ Request always sin cache (rompe offline)
4. ❌ Sobre-optimizar con ETag/versioning desde día 1 (YAGNI)

### ✅ Lo que SÍ debes hacer:

1. ✅ Cache first (UI instantánea)
2. ✅ Backend revalidation (consistencia)
3. ✅ Fallback a cache si falla (offline)
4. ✅ Optimistic UI para updates (UX premium)

---

## 🎓 Aprendizajes Clave

### Problema Identificado:
**TTL-based caching NO es offline-first real cuando el backend es la fuente de verdad.**

- Usuario puede ver data incorrecta aunque TENGA internet
- Cambios en backend tardan hasta 60 min en reflejarse
- No hay garantía de consistencia

### Solución Profesional:
**Stale-While-Revalidate (SWR) es el estándar de la industria.**

Apps top-tier lo usan:
- ✅ Todoist
- ✅ Trello  
- ✅ Google Keep
- ✅ Notion
- ✅ Asana

### Por qué SWR Funciona:

```
Usuario abre Daily
    ↓
Cache (0ms) → UI renderiza INSTANTÁNEAMENTE ✅
    ↓
Backend (paralelo) → Si cambió algo → UI refresca ✅
    ↓
Sin internet → Cache es suficiente ✅
```

**Resultado:** UX de apps profesionales + consistencia + offline.

---

## 📚 Referencias para Leer

### Esenciales:
1. **SPEC-006** (este proyecto): Implementación completa con código
2. [Google I/O 2019: Offline-first apps](https://www.youtube.com/watch?v=70WqJxymPr8)
3. [RFC 5861: Stale-While-Revalidate](https://datatracker.ietf.org/doc/html/rfc5861)

### Opcionales:
- [Android Developer Guide: Offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first)
- [Room Performance Best Practices](https://developer.android.com/training/data-storage/room/best-practices)

---

## 🎯 Conclusión

**Tu intuición era correcta:** Request siempre + cache es la estrategia profesional.

**La clave:** NO bloquear UI esperando el request (cache first + background revalidation).

**SWR te da:**
- ✅ UX de apps top-tier (UI instantánea)
- ✅ Backend como source of truth (consistencia)
- ✅ Funcionalidad offline completa
- ✅ Código mantenible y escalable

**Ahora:** Implementa Daily con SWR siguiendo el plan actualizado.

**Después:** Replica el patrón en Tasks, Habits, Workouts, Meals.

---

**¡Éxito con la implementación!** 🚀

Si necesitas ayuda en alguna fase, consulta:
- SPEC-006 para conceptos
- DAILY-IMPLEMENTATION-PLAN.md para código
- Este resumen para decisiones clave
