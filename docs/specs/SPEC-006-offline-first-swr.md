# SPEC-006: SWR Cache + Sincronización Offline

**Versión:** 2.0
**Estado:** Fase 1 ✅ Implementada | Fase 2 📝 Pendiente
**Fecha de creación:** 2026-02-05
**Última actualización:** 2026-05-14

---

## 1. Información General

### 1.1 Objetivo

Garantizar que la app GoodLife funcione de forma confiable con y sin internet, usando un sistema de cache inteligente y una cola de sincronización para no perder datos del usuario.

### 1.2 Alcance

| Fase | Descripción | Estado |
|------|-------------|--------|
| **Fase 1** | SWR Backend-First + Room como fallback (lectura) | ✅ Implementada |
| **Fase 2** | Cola de sincronización offline (escritura) | 📝 Pendiente |
| **Fase 3** | Optimizaciones avanzadas (reglas por fecha, version-based) | 🔮 Futuro |

### 1.3 Módulos Afectados

- Daily Log (Fase 1 implementada)
- Tasks, Habits, Workouts, Meals (Fase 2+, mismo patrón)

---

## 2. Arquitectura General

### 2.1 Principio Fundamental

> **El backend es SIEMPRE la fuente de verdad.** Room es un espejo temporal que sirve como plan B cuando no hay internet.

```
┌─────────────────────────────────────────────────────────────────┐
│                        ARQUITECTURA SWR                         │
│                                                                 │
│   ┌───────────────┐                      ┌───────────────────┐  │
│   │   Backend     │ ◄── fuente de verdad │   Room (cache)    │  │
│   │   (API REST)  │                      │   (espejo local)  │  │
│   └───────┬───────┘                      └─────────┬─────────┘  │
│           │                                        │            │
│           │  LECTURA (Fase 1):                     │            │
│           │  1. Pido al backend                    │            │
│           │  2. Si OK → guardo en Room + devuelvo  │            │
│           │  3. Si FALLA → leo de Room             │            │
│           │                                        │            │
│           │  ESCRITURA (Fase 2):                   │            │
│           │  1. Intento enviar al backend           │            │
│           │  2. Si FALLA → guardo en cola local    │            │
│           │  3. Cuando haya internet → sincronizo  │            │
│           │                                        │            │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Capas Involucradas

```
┌──────────────────────────────────────────────────────────────┐
│  PRESENTACIÓN (Compose)                                       │
│                                                              │
│  DailyScreen ← observa → DailyTabViewModel                  │
│                               │                              │
├──────────────────────────────────────────────────────────────┤
│  DOMINIO                                                      │
│                                                              │
│  GetDailyItemsUseCase / UpdateItemStatusUseCase              │
│  DailyRepository (interfaz — NO sabe de Room ni Retrofit)    │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│  DATA                                                         │
│                                                              │
│  DailyRepositoryImpl (implementa SWR + cola de sync)         │
│       │                    │                                 │
│       ▼                    ▼                                 │
│  DailyRemoteDataSource    DailyDao (Room)                    │
│  (Ktor/Retrofit)          (SQLite local)                     │
│       │                    │                                 │
│       ▼                    ▼                                 │
│  Backend API           daily_log + daily_item tables         │
│  (HTTP)                pending_sync table (Fase 2)           │
└──────────────────────────────────────────────────────────────┘
```

**El dominio NO sabe que existe Room.** Solo conoce la interfaz `DailyRepository`. La decisión de usar cache o cola de sync es un detalle de implementación de la capa data.

---

## 3. Fase 1: SWR Backend-First con Room Fallback (✅ IMPLEMENTADA)

### 3.1 Qué Resuelve

Sin esta fase, si el usuario abre la app sin internet, ve un error. Con SWR, ve los datos del último fetch exitoso.

### 3.2 Regla de Oro

> "Siempre pido al servidor; si responde, guardo una copia local; si no responde, uso esa copia como plan B."

### 3.3 Diagrama de Decisión

```
                    ┌──────────────┐
                    │ getDailyLog  │
                    │   (fecha)    │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │   Llamar     │
                    │   Backend    │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
               ┌────│   ¿Éxito?   │────┐
               │    └──────────────┘    │
             ✅ SÍ                    ❌ NO
               │                        │
        ┌──────▼──────┐          ┌──────▼──────┐
        │  Mapear a   │          │  Buscar en  │
        │  DailyLog   │          │    Room     │
        └──────┬──────┘          └──────┬──────┘
               │                        │
        ┌──────▼──────┐          ┌──────▼──────┐
        │  Guardar    │     ┌────│ ¿Hay cache? │────┐
        │  en Room    │     │    └─────────────┘    │
        └──────┬──────┘   ✅ SÍ                   ❌ NO
               │            │                        │
        ┌──────▼──────┐  ┌──▼────────────┐  ┌───────▼──────┐
        │  Devolver   │  │  Devolver     │  │  Devolver    │
        │  FRESCO     │  │  STALE (viejo)│  │  ERROR       │
        └─────────────┘  └──────────────┘  └──────────────┘
```

### 3.4 Implementación Actual: DailyRepositoryImpl

```kotlin
class DailyRepositoryImpl(
    private val remoteDataSource: DailyRemoteDataSource,
    private val dailyDao: DailyDao,
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        val dateString = date.toString()

        return when (val remoteResult = remoteDataSource.getDailyLogByDate(dateString)) {
            is Result.Success -> {
                val freshLog = remoteResult.data.toDomain()
                saveToCacheQuietly(freshLog)        // Guardar en Room
                Result.Success(freshLog)            // Devolver fresco
            }
            is Result.Error -> {
                val cached = dailyDao.getDailyLogByDate(dateString)
                if (cached != null) {
                    Result.Success(cached.toDomain()) // Fallback: datos viejos
                } else {
                    remoteResult                      // Sin cache: propagar error
                }
            }
        }
    }

    override suspend fun updateItemStatus(itemId: Long, status: DailyItemStatus): Result<DailyLog> {
        return when (val remoteResult = remoteDataSource.updateItemStatus(itemId, status)) {
            is Result.Success -> {
                val freshLog = remoteResult.data.toDomain()
                saveToCacheQuietly(freshLog)
                Result.Success(freshLog)
            }
            is Result.Error -> remoteResult // ⚠️ Fase 2 mejora esto
        }
    }

    private suspend fun saveToCacheQuietly(dailyLog: DailyLog) {
        try {
            val logEntity = dailyLog.toEntity()
            val itemEntities = dailyLog.items.map { it.toItemEntity(dailyLog.id) }
            dailyDao.saveDailyLog(logEntity, itemEntities)
        } catch (_: Exception) {
            // Si Room falla, no rompe la app
        }
    }
}
```

### 3.5 Tablas Room Actuales

#### Tabla: `daily_log`

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | Long (PK) | Mismo ID que el backend |
| date | String | Fecha ISO "2026-03-04" |
| completionRate | Double | % completado (0.0 a 1.0) |
| cachedAt | Long | Timestamp de cuándo se cacheó |

#### Tabla: `daily_item`

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | Long (PK) | Mismo ID que el backend |
| dailyLogId | Long (FK → daily_log.id) | Referencia al log padre |
| type | String | "TASK", "HABIT", "WORKOUT", "MEAL" |
| referenceId | Long | ID de la task/habit original |
| scheduledTime | String? | Hora ISO "14:30" o null |
| status | String | "PENDING", "IN_PROGRESS", "COMPLETED", "SKIPPED" |
| title | String | Nombre de la tarea |
| description | String? | Descripción opcional |

**Relación 1:N:** Un `daily_log` tiene muchos `daily_item`. CASCADE DELETE: al borrar un log se borran sus items.

#### DailyLogWithItems (relación Room)

```kotlin
data class DailyLogWithItems(
    @Embedded val log: DailyLogEntity,
    @Relation(parentColumn = "id", entityColumn = "dailyLogId")
    val items: List<DailyItemEntity>
)
```

### 3.6 ¿Cuándo se Refresca el Cache?

| Evento | ¿Se actualiza Room? |
|--------|---------------------|
| Abrir la pantalla Daily | ✅ Sí (fetch backend → save Room) |
| Volver de crear una Task | ✅ Sí (LifecycleResumeEffect → refresh) |
| Cambiar el día (← →) | ✅ Sí (nuevo fetch para la nueva fecha) |
| Marcar item como completado (PATCH) | ✅ Sí (backend responde DailyLog actualizado) |
| App vuelve de background | ✅ Sí (LifecycleResumeEffect) |

### 3.7 Flujos de Usuario (Fase 1)

#### Con internet: Abrir Daily

```
Usuario abre Daily → ViewModel llama getDailyLog(fecha)
    → Backend responde OK
    → Guarda en Room + devuelve datos frescos
    → Pantalla muestra tareas actualizadas
```

#### Sin internet: Abrir Daily

```
Usuario abre Daily → ViewModel llama getDailyLog(fecha)
    → Backend falla (sin internet)
    → Busca en Room → Hay cache de la última vez
    → Pantalla muestra tareas (pueden estar viejas)
```

#### Crear Task y Volver

```
Usuario crea task → POST al backend → Backend la guarda
    → Navega atrás al Daily
    → LifecycleResumeEffect dispara refresh()
    → GET al backend → DailyLog ahora incluye la task nueva
    → Guarda en Room + pantalla muestra la task
```

**La task nueva NO se guarda en Room al crearla.** El backend decide qué tasks aparecen en cada DailyLog. Room solo cachea lo que el backend devuelve.

#### Marcar item como COMPLETED (con internet)

```
Usuario toca checkbox → PATCH al backend con {status: COMPLETED}
    → Backend actualiza status + recalcula completionRate
    → Responde con DailyLog actualizado
    → Se guarda en Room + pantalla se actualiza
```

#### ⚠️ Marcar item como COMPLETED (SIN internet) — PROBLEMA ACTUAL

```
Usuario toca checkbox → PATCH al backend
    → Backend falla (sin internet)
    → ❌ Se muestra error
    → ❌ El cambio se pierde
    → El usuario tiene que volver a intentar cuando tenga internet
```

**Este es el problema que resuelve la Fase 2.**

---

## 4. Fase 2: Cola de Sincronización Offline (📝 POR IMPLEMENTAR)

### 4.1 Qué Resuelve

Cuando el usuario marca una tarea como COMPLETED sin internet, el cambio se pierde. La Fase 2 guarda ese cambio localmente y lo envía al backend cuando vuelva la conexión.

### 4.2 Concepto: Persistencia Eventual

> "Guardo el cambio en Room inmediatamente. Lo pongo en una cola. Cuando haya internet, lo envío al backend."

```
┌────────────────────────────────────────────────────────────────┐
│                    FLUJO OFFLINE WRITE                          │
│                                                                │
│  Usuario marca COMPLETED                                       │
│       │                                                        │
│       ▼                                                        │
│  1. Intento PATCH al backend                                   │
│       │                                                        │
│       ├─ ✅ OK → Guardo respuesta en Room → FIN                │
│       │                                                        │
│       └─ ❌ FALLA (sin internet)                               │
│              │                                                  │
│              ▼                                                  │
│  2. Actualizo Room localmente (optimistic update)              │
│              │                                                  │
│              ▼                                                  │
│  3. Creo entrada en tabla pending_sync                         │
│     {itemId: 42, action: UPDATE_STATUS, payload: "COMPLETED"} │
│              │                                                  │
│              ▼                                                  │
│  4. UI muestra el cambio inmediatamente (optimistic)           │
│              │                                                  │
│              ▼                                                  │
│  5. Cuando vuelva internet:                                    │
│     ├─ Manual: Usuario toca "Sincronizar"                      │
│     └─ Auto: WorkManager detecta conexión                      │
│              │                                                  │
│              ▼                                                  │
│  6. Recorro la cola → envío cada cambio al backend             │
│              │                                                  │
│              ▼                                                  │
│  7. Si el backend acepta → borro de la cola                    │
│     Si el backend rechaza → manejo conflicto                   │
│              │                                                  │
│              ▼                                                  │
│  8. Hago GET fresh al backend → actualizo Room completo        │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### 4.3 Modelado de Datos

#### 4.3.1 Nuevo campo en DailyItemEntity: `syncStatus`

```kotlin
@Entity(tableName = "daily_item", ...)
data class DailyItemEntity(
    @PrimaryKey val id: Long,
    val dailyLogId: Long,
    val type: String,
    val referenceId: Long,
    val scheduledTime: String?,
    val status: String,
    val title: String,
    val description: String?,
    val syncStatus: String = "SYNCED"  // NUEVO: "SYNCED" | "PENDING_SYNC"
)
```

- `SYNCED`: El status de este item coincide con el backend.
- `PENDING_SYNC`: El status fue cambiado localmente y todavía no se envió al backend.

#### 4.3.2 Nueva tabla: `pending_sync`

```kotlin
@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,    // "DAILY_ITEM"
    val entityId: Long,        // ID del item afectado
    val action: String,        // "UPDATE_STATUS"
    val payload: String,       // JSON: {"status": "COMPLETED"}
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,   // Intentos fallidos
    val maxRetries: Int = 5    // Máximo de reintentos
)
```

#### 4.3.3 Nuevo DAO: PendingSyncDao

```kotlin
@Dao
interface PendingSyncDao {

    @Query("SELECT * FROM pending_sync ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<PendingSyncEntity>

    @Query("SELECT COUNT(*) FROM pending_sync")
    fun getPendingCount(): Flow<Int>

    @Insert
    suspend fun insert(sync: PendingSyncEntity)

    @Query("DELETE FROM pending_sync WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE pending_sync SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetry(id: Long)

    @Query("DELETE FROM pending_sync WHERE retryCount >= maxRetries")
    suspend fun deleteExhaustedRetries()
}
```

### 4.4 Cambios en DailyRepositoryImpl

```kotlin
override suspend fun updateItemStatus(itemId: Long, status: DailyItemStatus): Result<DailyLog> {
    return when (val remoteResult = remoteDataSource.updateItemStatus(itemId, status)) {
        is Result.Success -> {
            val freshLog = remoteResult.data.toDomain()
            saveToCacheQuietly(freshLog)
            Result.Success(freshLog)
        }
        is Result.Error -> {
            // NUEVO: En vez de propagar error, guardar en cola
            updateLocalOptimistically(itemId, status)
            enqueuePendingSync(itemId, status)

            // Devolver el cache actualizado localmente
            val cached = dailyDao.getDailyLogByItemId(itemId)
            if (cached != null) {
                Result.Success(cached.toDomain())
            } else {
                remoteResult
            }
        }
    }
}

private suspend fun updateLocalOptimistically(itemId: Long, status: DailyItemStatus) {
    dailyDao.updateItemStatus(itemId, status.name)
    dailyDao.updateItemSyncStatus(itemId, "PENDING_SYNC")
}

private suspend fun enqueuePendingSync(itemId: Long, status: DailyItemStatus) {
    pendingSyncDao.insert(
        PendingSyncEntity(
            entityType = "DAILY_ITEM",
            entityId = itemId,
            action = "UPDATE_STATUS",
            payload = """{"status": "${status.name}"}"""
        )
    )
}
```

### 4.5 Sincronización Manual

#### 4.5.1 UseCase: SyncPendingChangesUseCase

```kotlin
class SyncPendingChangesUseCase(
    private val pendingSyncDao: PendingSyncDao,
    private val remoteDataSource: DailyRemoteDataSource,
    private val dailyDao: DailyDao,
    private val dispatcher: DispatcherProvider
) {
    suspend operator fun invoke(): SyncResult = withContext(dispatcher.io) {
        val pendingItems = pendingSyncDao.getAllPending()
        if (pendingItems.isEmpty()) return@withContext SyncResult.NothingToSync

        var successCount = 0
        var failCount = 0

        for (item in pendingItems) {
            try {
                when (item.action) {
                    "UPDATE_STATUS" -> {
                        val status = parseStatus(item.payload)
                        val result = remoteDataSource.updateItemStatus(item.entityId, status)
                        if (result is Result.Success) {
                            saveToCacheQuietly(result.data.toDomain())
                            pendingSyncDao.deleteById(item.id)
                            dailyDao.updateItemSyncStatus(item.entityId, "SYNCED")
                            successCount++
                        } else {
                            pendingSyncDao.incrementRetry(item.id)
                            failCount++
                        }
                    }
                }
            } catch (_: Exception) {
                pendingSyncDao.incrementRetry(item.id)
                failCount++
            }
        }

        pendingSyncDao.deleteExhaustedRetries()

        when {
            failCount == 0 -> SyncResult.AllSynced(successCount)
            successCount > 0 -> SyncResult.PartiallySynced(successCount, failCount)
            else -> SyncResult.Failed
        }
    }
}

sealed interface SyncResult {
    data object NothingToSync : SyncResult
    data class AllSynced(val count: Int) : SyncResult
    data class PartiallySynced(val synced: Int, val failed: Int) : SyncResult
    data object Failed : SyncResult
}
```

#### 4.5.2 UI: Botón de Sincronizar

El botón solo aparece cuando hay items pendientes de sincronización.

```kotlin
// En DailyUiState.Success
val pendingSyncCount: Int = 0

// En DailyScreen
if (uiState.pendingSyncCount > 0) {
    SyncBanner(
        count = uiState.pendingSyncCount,
        isSyncing = uiState.isSyncing,
        onSyncClick = { onAction(DailyUiAction.OnSync) }
    )
}
```

El ViewModel expone `onSync()` que llama al `SyncPendingChangesUseCase`:

```kotlin
is DailyUiAction.OnSync -> syncPendingChanges()

private fun syncPendingChanges() {
    viewModelScope.launch {
        _uiState.update { if (it is Success) it.copy(isSyncing = true) else it }

        when (val result = syncPendingChangesUseCase()) {
            is SyncResult.AllSynced -> {
                loadItems() // Refresh con datos del backend
            }
            is SyncResult.PartiallySynced -> {
                loadItems()
                showError("${result.failed} cambios no se pudieron sincronizar")
            }
            is SyncResult.Failed -> {
                _uiState.update { if (it is Success) it.copy(isSyncing = false) else it }
                showError("Sin conexión. Intentá más tarde.")
            }
            is SyncResult.NothingToSync -> {
                _uiState.update { if (it is Success) it.copy(isSyncing = false) else it }
            }
        }
    }
}
```

### 4.6 WorkManager: Sincronización Automática

Si el usuario no sincroniza manualmente, un Worker periódico lo intenta en background.

```kotlin
class SyncPendingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val syncUseCase: SyncPendingChangesUseCase = // Obtener de Koin

        return when (syncUseCase()) {
            is SyncResult.AllSynced,
            is SyncResult.NothingToSync -> Result.success()
            is SyncResult.PartiallySynced -> Result.retry()
            is SyncResult.Failed -> Result.retry()
        }
    }
}
```

Registro en `GoodLifeApp`:

```kotlin
val syncRequest = PeriodicWorkRequestBuilder<SyncPendingWorker>(
    repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES
).setConstraints(
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Solo con internet
        .build()
).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "sync_pending_changes",
    ExistingPeriodicWorkPolicy.KEEP,
    syncRequest
)
```

### 4.7 Garbage Collection del Cache

Limpieza automática de datos viejos para que Room no crezca indefinidamente.

```kotlin
class CleanupCacheWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dailyDao: DailyDao = // Obtener de Koin
        val cutoff = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000) // 7 días
        dailyDao.deleteOlderThan(cutoff)
        return Result.success()
    }
}
```

Frecuencia: una vez por semana o al abrir la app.

### 4.8 Flujos Completos (Fase 2)

#### Marcar COMPLETED sin internet

```
Usuario toca checkbox
    │
    ├─ 1. Intenta PATCH al backend → ❌ Sin internet
    │
    ├─ 2. Actualiza Room localmente (status = COMPLETED, syncStatus = PENDING_SYNC)
    │
    ├─ 3. Crea entrada en pending_sync
    │
    ├─ 4. UI muestra tarea completada inmediatamente ✅
    │
    └─ 5. Cuando vuelva internet:
           ├─ A) Usuario toca "Sincronizar" → envía cola → ✅
           └─ B) WorkManager (cada 15 min) → envía cola → ✅
```

#### Sincronización exitosa

```
Sync se dispara (manual o automático)
    │
    ├─ 1. Lee pending_sync → hay 3 cambios pendientes
    │
    ├─ 2. Para cada uno → PATCH al backend
    │      ├─ Item 42: COMPLETED → ✅ Backend acepta
    │      ├─ Item 43: SKIPPED → ✅ Backend acepta
    │      └─ Item 44: COMPLETED → ❌ Error (reintenta después)
    │
    ├─ 3. Borra items sincronizados de pending_sync
    │
    ├─ 4. Incrementa retryCount del item fallido
    │
    └─ 5. Hace GET fresh al backend → Room se actualiza completo
```

---

## 5. Criterios de Aceptación

### Fase 1 (✅ Implementada)

- [x] Con internet: datos siempre frescos del backend
- [x] Sin internet: datos cacheados de la última visita
- [x] Sin internet ni cache: mensaje de error claro
- [x] Cada fetch exitoso actualiza Room
- [x] Si Room falla al guardar, la app no se rompe
- [x] Al volver de crear task, se hace refresh automático

### Fase 2 (📝 Pendiente)

- [ ] Marcar item sin internet guarda el cambio en Room localmente
- [ ] El cambio se muestra inmediatamente en la UI (optimistic update)
- [ ] El cambio se encola en `pending_sync`
- [ ] Botón "Sincronizar" visible solo cuando hay pendientes
- [ ] Al sincronizar, los cambios se envían al backend en batch
- [ ] Tras sincronización exitosa, `pending_sync` se limpia
- [ ] WorkManager intenta sincronizar cada 15 min con conexión
- [ ] No se duplican items: cada item se identifica por su ID del backend
- [ ] Items con más de 5 reintentos fallidos se descartan
- [ ] Cache viejo (>7 días) se limpia automáticamente
- [ ] La UI no muestra saltos bruscos al sincronizar
- [ ] Errores de red se muestran sin romper la app

---

## 6. Decisiones Arquitectónicas

### 6.1 ¿Por qué Backend-First y no Cache-First?

| Aspecto | Cache-First (Room → Backend) | Backend-First (Backend → Room) |
|---------|------------------------------|--------------------------------|
| UX | 0ms render (instantáneo) pero puede "saltar" | ~400ms pero sin saltos |
| Consistencia | Puede mostrar datos viejos brevemente | Siempre datos frescos |
| Complejidad | Media-Alta (manejar dos emisiones) | Media (una sola respuesta) |
| Flickering | Posible (cache viejo → dato nuevo) | No hay |

**Decisión:** Backend-First porque la lógica de negocio (recurrencia, asignación de tasks a días, completionRate) vive en el backend. Duplicarla en Room sería error-prone.

### 6.2 ¿Por qué Sync Manual + WorkManager y no solo WorkManager?

- **Sync manual** da control al usuario: "quiero que esto se sincronice AHORA".
- **WorkManager** es el safety net: si el usuario se olvida, se sincroniza solo.
- Ambos usan el mismo `SyncPendingChangesUseCase` (código compartido).

### 6.3 ¿Por qué NO crear tasks offline?

Crear una task involucra lógica del backend: validación, asignación de ID, recurrencia, cálculo de en qué días aparece. Replicar eso offline sería demasiado complejo y propenso a inconsistencias. Crear tasks REQUIERE internet.

Lo que SÍ funciona offline es **cambiar el status** de items que ya existen (COMPLETED, SKIPPED), porque es un cambio simple y predecible.

---

## 7. Comparación con Otras Estrategias

| Estrategia | Descripción | Complejidad | ¿Nosotros? |
|------------|-------------|-------------|-------------|
| Solo Backend | Sin cache, siempre red | Baja | ❌ Mala UX offline |
| SWR Backend-first (lectura) | Backend → Room fallback | Media | ✅ Fase 1 |
| SWR + Cola de sync (escritura) | Backend-first + pending queue | Media-Alta | ✅ Fase 2 |
| Full Offline-first | Todo local, sync bidireccional | Alta | ❌ Demasiado complejo |
| CQRS / Event Sourcing | Eventos locales + sync | Muy Alta | ❌ Overkill |

---

## 8. Roadmap

### Fase 1: SWR Lectura (✅ COMPLETADA)

- [x] `DailyRepositoryImpl` con SWR
- [x] Entidades Room: `DailyLogEntity`, `DailyItemEntity`
- [x] `DailyDao` con queries y transacciones
- [x] `DailyLogWithItems` con mappers toDomain/toEntity
- [x] `saveToCacheQuietly` para no romper si Room falla
- [x] `LifecycleResumeEffect` para refresh al volver
- [x] Tests unitarios del repositorio

### Fase 2: Cola de Sincronización Offline (📝 PRÓXIMO)

Estimación: 6-8 horas

- [ ] Agregar `syncStatus` a `DailyItemEntity` (migración Room v3)
- [ ] Crear `PendingSyncEntity` + `PendingSyncDao`
- [ ] Modificar `updateItemStatus` para guardar en cola si falla
- [ ] Crear `SyncPendingChangesUseCase`
- [ ] Crear `SyncPendingWorker` (WorkManager periódico)
- [ ] Crear `CleanupCacheWorker` (garbage collection)
- [ ] Agregar `pendingSyncCount` y `isSyncing` a `DailyUiState`
- [ ] Crear `SyncBanner` composable (botón de sync manual)
- [ ] Registrar Workers en `GoodLifeApp`
- [ ] Módulo Koin para sync dependencies
- [ ] Tests unitarios: SyncUseCase, Repository offline, Workers

### Fase 3: Optimizaciones (🔮 FUTURO)

- [ ] Reglas inteligentes por tipo de fecha (días viejos = cache only)
- [ ] Pull-to-refresh fuerza backend
- [ ] Version-based sync con ETag/304 Not Modified
- [ ] Replicar SWR en Tasks, Habits, Workouts, Meals repositories

---

## 9. Testing

### 9.1 Tests de Fase 1 (✅ Existentes)

| Test | Archivo |
|------|---------|
| getDailyLog retorna backend cuando funciona | `GetDailyItemsUseCaseTest` |
| getDailyLog retorna cache cuando backend falla | `GetDailyItemsUseCaseTest` |
| updateItemStatus actualiza cache con respuesta | `UpdateItemStatusUseCaseTest` |
| ViewModel refresh recargar datos | `DailyTabViewModelTest` |

### 9.2 Tests de Fase 2 (📝 Por Crear)

| Test | Qué Verifica |
|------|--------------|
| updateItemStatus encola cuando offline | Que se cree entrada en pending_sync |
| updateItemStatus actualiza Room local | Que el item cambie a COMPLETED + PENDING_SYNC |
| SyncUseCase envía cola al backend | Que cada item se envíe y se borre de la cola |
| SyncUseCase maneja fallos parciales | Que items exitosos se borren y fallidos persistan |
| SyncUseCase respeta maxRetries | Que items agotados se eliminen |
| Worker se ejecuta solo con internet | Constraint de NetworkType.CONNECTED |
| Cleanup borra cache viejo | Que entries >7 días se eliminen |

---

## 10. Glosario

| Término | Definición |
|---------|------------|
| **SWR** | Stale-While-Revalidate: pedir backend, usar cache como fallback |
| **Backend-First** | El backend siempre se consulta primero; Room es plan B |
| **Optimistic Update** | Actualizar la UI antes de que el backend confirme |
| **Pending Sync** | Cola de cambios locales que esperan enviarse al backend |
| **syncStatus** | Campo que indica si un item de Room coincide con el backend |
| **Garbage Collection** | Limpieza automática de cache viejo en Room |
| **WorkManager** | API de Android para ejecutar trabajo en background con constraints |
| **Batch Sync** | Enviar múltiples cambios pendientes en una sola operación |

---

## 11. Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Kotlin Coroutines** | Operaciones async en Repository y UseCases |
| **Room** | Base de datos local (cache + cola de sync) |
| **WorkManager** | Sincronización periódica en background |
| **Jetpack Compose** | UI declarativa (DailyScreen, SyncBanner) |
| **Koin** | Inyección de dependencias |
| **kotlinx.datetime** | Manejo de fechas (LocalDate, LocalTime) |
| **kotlinx.serialization** | Serialización del payload de pending_sync |

---

**FIN DE SPEC-006 v2.0**

---

**Autor:** GoodLife Development Team
**Fecha:** 2026-03-04
**Estado:** Fase 1 ✅ | Fase 2 📝
