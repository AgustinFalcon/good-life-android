# 🚀 FASE 1: Quick Start - Daily con API REST

**Tiempo estimado:** 2-3 horas  
**Objetivo:** App funcional con internet (backend como verdad 100%)

---

## ✅ Checklist Visual

```
[ ] Paso 1: Domain Layer (30 min)
    ├─ [ ] DailyLog.kt
    ├─ [ ] DailyItem.kt
    ├─ [ ] DailyItemType.kt (enum)
    ├─ [ ] ItemStatus.kt (enum)
    ├─ [ ] DailyRepository.kt (interface)
    ├─ [ ] GetDailyItemsUseCase.kt
    └─ [ ] UpdateItemStatusUseCase.kt

[ ] Paso 2: Data - DTOs (30 min)
    ├─ [ ] DailyLogDto.kt
    ├─ [ ] DailyLogItemDto.kt
    ├─ [ ] TaskSummaryDto.kt
    ├─ [ ] HabitLogSummaryDto.kt
    ├─ [ ] DailyLogMapper.kt
    └─ [ ] Agregar endpoints en GoodLifeApiService.kt

[ ] Paso 3: Repository SIMPLE (20 min)
    └─ [ ] DailyRepositoryImpl.kt (sin Room)

[ ] Paso 4: Presentation (30 min)
    ├─ [ ] DailyUiState.kt (Success/Error/Loading)
    ├─ [ ] DailyUiAction.kt
    ├─ [ ] DailyTabViewModel.kt (actualizar)
    ├─ [ ] DailyScreen.kt (actualizar con LazyColumn)
    └─ [ ] DailyItemCard.kt (nuevo)

[ ] Paso 5: DI (15 min)
    ├─ [ ] DailyModule.kt
    └─ [ ] Registrar en GoodLifeApp.kt

[ ] Paso 6: Testing (15 min)
    ├─ [ ] Probar con internet
    ├─ [ ] Verificar carga de datos
    ├─ [ ] Verificar navegación entre días
    └─ [ ] Verificar actualización de status
```

---

## 🎯 Arquitectura SIMPLE

```
┌────────────────┐
│  DailyScreen   │ ← UI pura
└────────────────┘
        ↓ onAction
┌────────────────┐
│ ViewModel      │ ← State management
└────────────────┘
        ↓ UseCase
┌────────────────┐
│ Repository     │ ← Solo API (sin Room)
└────────────────┘
        ↓ ApiService
┌────────────────┐
│   Backend      │ ← Fuente de verdad 100%
└────────────────┘
```

---

## 🔥 Repository SUPER SIMPLE

```kotlin
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService,
    private val dispatcher: DispatcherProvider
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val response = apiService.getDailyLog(date.toString())
                
                when {
                    HttpCode.isSuccess(response.code) -> {
                        response.data?.toDomain()
                            ?: throw ApiException.NotFoundException()
                    }
                    else -> {
                        throw ApiException.fromCode(response.code)
                    }
                }
            }
        }
    }

    override suspend fun updateItemStatus(
        itemId: Long,
        status: ItemStatus
    ): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val response = apiService.updateItemStatus(itemId, status.name)
                
                when {
                    HttpCode.isSuccess(response.code) -> {
                        response.data?.toDomain()
                            ?: throw ApiException.NotFoundException()
                    }
                    else -> {
                        throw ApiException.fromCode(response.code)
                    }
                }
            }
        }
    }
}
```

**Eso es TODO.** 30 líneas de código. Simple, claro, directo.

---

## ⚡ Flujo de Datos

### Usuario abre Daily:
```
User taps Daily tab
    ↓
ViewModel.loadItems()
    ↓
GetDailyItemsUseCase(currentDate)
    ↓
Repository.getDailyLog(LocalDate(2026, 2, 5))
    ↓
ApiService.getDailyLog("2026-02-05")
    ↓
Backend: GET /api/v1/daily-logs/2026-02-05
    ↓
Response: { "code": 200, "data": { ... } }
    ↓
Mapper: DailyLogDto.toDomain()
    ↓
ViewModel: _uiState.value = Success(...)
    ↓
UI: Renderiza lista de items
```

### Usuario marca tarea completada:
```
User taps checkbox
    ↓
ViewModel.updateItemStatus(itemId, COMPLETED)
    ↓
UpdateItemStatusUseCase(itemId, COMPLETED)
    ↓
Repository.updateItemStatus(...)
    ↓
ApiService.updateItemStatus(itemId, "COMPLETED")
    ↓
Backend: PATCH /api/v1/daily-logs/items/123/status?status=COMPLETED
    ↓
Response: { "code": 200, "data": { ... completionRate actualizado } }
    ↓
ViewModel: _uiState.value = Success(...) ← completion rate actualizado
    ↓
UI: Renderiza con checkbox marcado + progress actualizado
```

---

## 🎨 Formato de Fecha (ISO-8601)

```kotlin
val date = LocalDate(2026, 2, 5)
val dateString = date.toString()  // "2026-02-05" ✅ ISO-8601

// Listo para el backend
apiService.getDailyLog(dateString)  // GET /daily-logs/2026-02-05
```

**NO necesitas formatear.** `LocalDate.toString()` ya devuelve ISO-8601.

---

## 📝 Endpoints del Backend

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/daily-logs/{date}` | GET | Obtener daily log de una fecha |
| `/api/v1/daily-logs/items/{itemId}/status` | PATCH | Actualizar status de un item |

**Ejemplo request:**
```http
GET /api/v1/daily-logs/2026-02-05
Authorization: Bearer eyJhbGc...
```

**Ejemplo response:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "userId": 123,
    "date": "2026-02-05",
    "completionRate": 0.5,
    "items": [
      {
        "id": 1,
        "itemType": "TASK",
        "scheduledTime": "08:00:00",
        "status": "PENDING",
        "task": {
          "id": 5,
          "title": "Reunión de equipo"
        }
      }
    ]
  }
}
```

---

## ⚠️ Limitaciones Esperadas (FASE 1)

| Limitación | Impacto | Solución |
|------------|---------|----------|
| **No funciona sin internet** | App crashea/error | FASE 2 (Room/cache) |
| **UI espera red** | Delay de 200-500ms al cargar | FASE 2 (cache-first) |
| **Requests repetidos** | Navegas a ayer → request | FASE 2 (cache) |

**¿Es un problema?** ❌ **NO** para FASE 1.

Tu objetivo ahora es:
1. Aprender la arquitectura
2. Conectar con backend
3. Hacer que funcione (con internet)

Después (FASE 2):
4. Agregar Room
5. Soporte offline
6. UI instantánea

---

## ✅ Resultado Esperado (FASE 1)

Cuando completes FASE 1, tu app:
- ✅ Carga datos del backend
- ✅ Muestra daily log del día actual
- ✅ Navega entre días (← ayer | hoy | mañana →)
- ✅ Actualiza status de items
- ✅ Muestra porcentaje de completitud
- ✅ Pull-to-refresh funciona
- ✅ Maneja errores correctamente

**Con la limitación aceptable:**
- ❌ Requiere internet (se arregla en FASE 2)

---

## 🚀 ¡Empezá!

1. Leé el plan completo: `DAILY-IMPLEMENTATION-PLAN.md`
2. Seguí el checklist paso a paso
3. Empezá por Domain Layer (lo más fácil)
4. Probá en cada paso

**¿Dudas?**
- Consultá SPEC-005 (Network Service)
- Consultá SPEC-004 (DateProvider + ISO-8601)

---

**¡Éxito!** 🎯
