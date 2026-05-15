# SPEC-005: Network Service & API Communication

**Versión:** 1.0  
**Estado:** 📝 En Desarrollo  
**Fecha de creación:** 2026-02-04  
**Última actualización:** 2026-02-04

---

## 1. Información General

### 1.1 Objetivo

Documentar la comunicación entre el frontend Android y el backend GoodLife, incluyendo:
- Estructura de responses (`BaseResponse`)
- Códigos HTTP (`HttpCode`)
- Manejo de errores (`ApiException`)
- Endpoints consumidos
- Formato de fechas (ISO-8601)
- Estrategias de cache

### 1.2 Alcance

Este SPEC cubre:
- ✅ Capa de Network (Retrofit, OkHttp)
- ✅ DTOs y serialización (Kotlinx Serialization)
- ✅ Mappers (DTO → Domain)
- ✅ Manejo de errores
- ✅ Cache con Room (offline-first)

**NO cubre:**
- ❌ Lógica de negocio (ver SPECs de cada módulo)
- ❌ UI/Composables (ver SPEC-003)
- ❌ ViewModels (ver SPECs específicos)

---

## 2. Arquitectura de Red

### 2.1 Diagrama de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                      │
│  ViewModel (solicita datos, recibe Result<T>)               │
└─────────────────────────────────────────────────────────────┘
                           ↓ UseCase
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  UseCase (recibe LocalDate, devuelve Result<DomainModel>)   │
└─────────────────────────────────────────────────────────────┘
                           ↓ Repository interface
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
│                                                             │
│  ┌───────────────────────────────────────────────┐         │
│  │ Repository Implementation                     │         │
│  │  • Decide: cache o network?                   │         │
│  │  • Convierte: LocalDate → String (ISO-8601)   │         │
│  │  • Mapea: DTO → Domain                        │         │
│  └───────────────────────────────────────────────┘         │
│         ↓ Remote                     ↓ Local               │
│  ┌──────────────┐             ┌────────────┐               │
│  │ ApiService   │             │  RoomDao   │               │
│  │ (Retrofit)   │             │  (Cache)   │               │
│  └──────────────┘             └────────────┘               │
│         ↓ HTTP                       ↓ SQL                 │
│  ┌──────────────┐             ┌────────────┐               │
│  │   Backend    │             │   SQLite   │               │
│  │   REST API   │             │  Database  │               │
│  └──────────────┘             └────────────┘               │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Stack Tecnológico

| Componente | Librería | Versión | Propósito |
|------------|----------|---------|-----------|
| HTTP Client | Retrofit | 2.11.0 | Requests REST |
| Serialización | Kotlinx Serialization | 1.7.3 | JSON ↔ Kotlin |
| HTTP Logging | OkHttp Logging Interceptor | 4.12.0 | Debug de requests |
| Cache Local | Room | 2.7.0-alpha03 | Persistencia offline |

---

## 3. BaseResponse<T>

### 3.1 Estructura del Backend

El backend **siempre** devuelve esta estructura:

```json
{
  "code": 200,
  "data": { ... },
  "message": null
}
```

### 3.2 Implementación Android

```kotlin
@Serializable
data class BaseResponse<T>(
    val code: Int,
    val data: T? = null,
    val message: String? = null
) {
    val isSuccess: Boolean get() = code in 200..299
    val isError: Boolean get() = !isSuccess
}
```

### 3.3 Casos de Uso

| Código | data | message | Significado |
|--------|------|---------|-------------|
| 200 | ✅ Presente | `null` | Éxito con datos |
| 201 | ✅ Presente | `null` | Recurso creado |
| 204 | `null` | `null` | Éxito sin contenido |
| 400 | `null` | ✅ Presente | Validación fallida |
| 401 | `null` | ✅ Presente | Token inválido/expirado |
| 404 | `null` | ✅ Presente | Recurso no encontrado |
| 500 | `null` | ✅ Presente | Error del servidor |

### 3.4 Ejemplo Real: Daily Log

**Request:**
```http
GET /api/v1/daily-logs/2026-02-03
Authorization: Bearer eyJhbGc...
```

**Response (200 OK):**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "userId": 123,
    "date": "2026-02-03",
    "completionRate": 0.5,
    "items": [
      {
        "id": 1,
        "itemType": "TASK",
        "referenceId": 5,
        "scheduledTime": "08:00:00",
        "status": "PENDING",
        "task": {
          "id": 5,
          "title": "Reunión de equipo"
        }
      }
    ]
  },
  "message": null
}
```

**Response (404 Not Found):**
```json
{
  "code": 404,
  "data": null,
  "message": "Daily log no encontrado para la fecha 2026-02-03"
}
```

---

## 4. HttpCode

### 4.1 Enum de Códigos HTTP

```kotlin
enum class HttpCode(val code: Int) {
    // 2xx - Éxito
    SUCCESS(200),
    CREATED(201),
    NO_CONTENT(204),

    // 4xx - Errores del cliente
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),

    // 5xx - Errores del servidor
    INTERNAL_SERVER_ERROR(500);

    companion object {
        fun fromCode(code: Int): HttpCode? = entries.find { it.code == code }
        fun isSuccess(code: Int): Boolean = code in 200..299
        fun isClientError(code: Int): Boolean = code in 400..499
        fun isServerError(code: Int): Boolean = code in 500..599
    }
}
```

### 4.2 Tabla de Códigos

| Código | Enum | Significado | Acción en App |
|--------|------|-------------|---------------|
| 200 | `SUCCESS` | Operación exitosa | Mostrar datos |
| 201 | `CREATED` | Recurso creado | Navegar a detalle |
| 204 | `NO_CONTENT` | Eliminación exitosa | Actualizar lista |
| 400 | `BAD_REQUEST` | Validación fallida | Mostrar errores de formulario |
| 401 | `UNAUTHORIZED` | Token inválido | Navegar a login |
| 403 | `FORBIDDEN` | Sin permisos | Mostrar mensaje de acceso denegado |
| 404 | `NOT_FOUND` | Recurso no existe | Mostrar "No encontrado" |
| 500 | `INTERNAL_SERVER_ERROR` | Error del servidor | Mostrar "Intenta más tarde" |

### 4.3 Uso en Repository

```kotlin
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return suspendResultOf {
            val dateString = date.toString()  // "2026-02-03"
            val response = apiService.getDailyLog(dateString)
            
            when {
                HttpCode.isSuccess(response.code) -> {
                    response.data?.toDomain() 
                        ?: throw ApiException.NotFoundException("Daily log no encontrado")
                }
                else -> {
                    throw ApiException.fromCode(response.code, response.message)
                }
            }
        }
    }
}
```

---

## 5. ApiException

### 5.1 Jerarquía de Excepciones

```kotlin
sealed class ApiException(
    override val message: String,
    val code: Int
) : Exception(message) {

    class BadRequestException(message: String) : ApiException(message, 400)
    class UnauthorizedException(message: String) : ApiException(message, 401)
    class ForbiddenException(message: String) : ApiException(message, 403)
    class NotFoundException(message: String) : ApiException(message, 404)
    class ServerException(message: String) : ApiException(message, 500)
    class UnknownApiException(message: String, code: Int) : ApiException(message, code)

    companion object {
        fun fromCode(code: Int, message: String?): ApiException {
            val errorMessage = message ?: "Error desconocido"
            return when (code) {
                400 -> BadRequestException(errorMessage)
                401 -> UnauthorizedException(errorMessage)
                403 -> ForbiddenException(errorMessage)
                404 -> NotFoundException(errorMessage)
                500 -> ServerException(errorMessage)
                else -> UnknownApiException(errorMessage, code)
            }
        }
    }
}
```

### 5.2 Manejo en ViewModel

```kotlin
class DailyTabViewModel(
    private val getDailyItemsUseCase: GetDailyItemsUseCase
) : ViewModel() {

    fun loadItems(date: LocalDate) {
        viewModelScope.launch {
            getDailyItemsUseCase(date)
                .onSuccess { items ->
                    _uiState.value = DailyUiState.Success(items)
                }
                .onError { error ->
                    val errorMessage = when (error) {
                        is ApiException.UnauthorizedException -> "Sesión expirada"
                        is ApiException.NotFoundException -> "No hay datos para esta fecha"
                        is ApiException.ServerException -> "Error del servidor. Intenta más tarde"
                        else -> error.message
                    }
                    _uiState.value = DailyUiState.Error(errorMessage)
                }
        }
    }
}
```

---

## 6. Formato de Fechas (ISO-8601)

### 6.1 Estándar ISO-8601

**Formato:** `YYYY-MM-DD`  
**Ejemplo:** `2026-02-03` (3 de febrero de 2026)

### 6.2 Conversión Automática

`kotlinx.datetime.LocalDate.toString()` devuelve automáticamente ISO-8601:

```kotlin
val date = LocalDate(2026, 2, 3)
val dateString = date.toString()  // "2026-02-03" ✅ ISO-8601
```

**NO se necesita formateo adicional.**

### 6.3 Flujo Completo

```kotlin
// 1. ViewModel mantiene LocalDate
val currentDate: LocalDate = dateProvider.today()  // LocalDate(2026, 2, 3)

// 2. UseCase recibe LocalDate
suspend fun getDailyItems(date: LocalDate): Result<List<DailyItem>>

// 3. Repository convierte a String
val dateString = date.toString()  // "2026-02-03"
apiService.getDailyLog(dateString)

// 4. Backend recibe: GET /api/v1/daily-logs/2026-02-03

// 5. Response vuelve con: { "date": "2026-02-03", ... }

// 6. DTO parsea: LocalDate.parse("2026-02-03")

// 7. Domain Model tiene LocalDate
```

### 6.4 Parsing de Response

```kotlin
@Serializable
data class DailyLogDto(
    val id: Long,
    val date: String,  // "2026-02-03"
    val completionRate: Double
)

fun DailyLogDto.toDomain(): DailyLog {
    return DailyLog(
        id = id,
        date = LocalDate.parse(date),  // ✅ Parse directo de ISO-8601
        completionRate = completionRate
    )
}
```

### 6.5 Ventajas

- ✅ **Sin conversión adicional**: `LocalDate.toString()` = ISO-8601
- ✅ **Sin dependencias**: No se necesitan librerías de formateo
- ✅ **Type-safe**: `LocalDate` hasta la capa de Repository
- ✅ **Estándar internacional**: ISO-8601 es el estándar web

---

## 7. Cache con Room (Offline-First)

### 7.1 Estrategia de Cache

```kotlin
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService,
    private val dailyDao: DailyDao,
    private val dispatcher: DispatcherProvider
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                try {
                    // 1. Intentar obtener del cache
                    val cached = dailyDao.getDailyLog(date.toString())
                    
                    if (cached != null && cached.isFresh()) {
                        return@suspendResultOf cached.toDomain()
                    }
                    
                    // 2. Si no hay cache o está viejo, pedir al backend
                    val dateString = date.toString()
                    val response = apiService.getDailyLog(dateString)
                    
                    if (HttpCode.isSuccess(response.code)) {
                        val dailyLog = response.data?.toDomain()
                            ?: throw ApiException.NotFoundException("Daily log no encontrado")
                        
                        // 3. Guardar en cache
                        dailyDao.insertDailyLog(dailyLog.toEntity())
                        
                        return@suspendResultOf dailyLog
                    } else {
                        // 4. Si falla el backend, devolver cache viejo (si existe)
                        if (cached != null) {
                            return@suspendResultOf cached.toDomain()
                        }
                        
                        throw ApiException.fromCode(response.code, response.message)
                    }
                } catch (e: Exception) {
                    // 5. En caso de error de red, devolver cache
                    val cached = dailyDao.getDailyLog(date.toString())
                    if (cached != null) {
                        return@suspendResultOf cached.toDomain()
                    }
                    throw e
                }
            }
        }
    }
}
```

### 7.2 Entidades de Room

```kotlin
@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey val date: String,  // "2026-02-03" (ISO-8601)
    val userId: Long,
    val completionRate: Double,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun isFresh(): Boolean {
        val now = System.currentTimeMillis()
        val ageInMinutes = (now - cachedAt) / (1000 * 60)
        return ageInMinutes < 60  // Cache válido por 60 minutos
    }
}

@Entity(tableName = "daily_log_items")
data class DailyLogItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyLogDate: String,  // FK a daily_logs
    val itemType: String,      // "TASK", "HABIT", "WORKOUT", "MEAL"
    val referenceId: Long,
    val scheduledTime: String?,
    val status: String,
    val title: String,
    val description: String?
)
```

### 7.3 DAO

```kotlin
@Dao
interface DailyDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getDailyLog(date: String): DailyLogEntity?
    
    @Query("SELECT * FROM daily_log_items WHERE dailyLogDate = :date")
    suspend fun getDailyLogItems(date: String): List<DailyLogItemEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(dailyLog: DailyLogEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLogItems(items: List<DailyLogItemEntity>)
    
    @Transaction
    suspend fun insertDailyLogWithItems(
        dailyLog: DailyLogEntity,
        items: List<DailyLogItemEntity>
    ) {
        insertDailyLog(dailyLog)
        insertDailyLogItems(items)
    }
}
```

### 7.4 Política de Cache

| Escenario | Comportamiento |
|-----------|----------------|
| Cache fresco (< 60 min) | Devolver cache, no hacer request |
| Cache viejo (> 60 min) | Request al backend, actualizar cache |
| Sin cache + Red OK | Request al backend, guardar cache |
| Sin cache + Red FAIL | Lanzar excepción |
| Con cache + Red FAIL | Devolver cache (modo offline) |

---

## 8. Endpoints Consumidos

### 8.1 Authentication

| Método | Endpoint | DTO Request | DTO Response | Estado |
|--------|----------|-------------|--------------|--------|
| POST | `/token` | `LoginRequest` | `AuthResponse` | ✅ Implementado |
| POST | `/register` | `RegisterRequest` | `RegisterResponse` | ✅ Implementado |
| POST | `/token` (refresh) | `RefreshTokenRequest` | `AuthResponse` | ✅ Implementado |

### 8.2 Daily Logs

| Método | Endpoint | DTO Request | DTO Response | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/api/v1/daily-logs/today` | - | `DailyLogDto` | 🟡 Pendiente |
| GET | `/api/v1/daily-logs/{date}` | - | `DailyLogDto` | 🟡 Pendiente |
| PATCH | `/api/v1/daily-logs/items/{itemId}/status` | Query: `status` | `DailyLogDto` | 🟡 Pendiente |

### 8.3 Tasks

| Método | Endpoint | DTO Request | DTO Response | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/api/v1/tasks` | - | `List<TaskDto>` | 🔴 No implementado |
| GET | `/api/v1/tasks/date/{date}` | - | `List<TaskDto>` | 🔴 No implementado |
| POST | `/api/v1/tasks` | `CreateTaskRequest` | `TaskDto` | 🔴 No implementado |
| PUT | `/api/v1/tasks/{id}` | `UpdateTaskRequest` | `TaskDto` | 🔴 No implementado |
| DELETE | `/api/v1/tasks/{id}` | - | - | 🔴 No implementado |

### 8.4 Habits

| Método | Endpoint | DTO Request | DTO Response | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/api/v1/habits` | - | `List<HabitDto>` | 🔴 No implementado |
| GET | `/api/v1/habits/date/{date}` | - | `List<HabitDto>` | 🔴 No implementado |

*Nota: Esta tabla se actualizará conforme se implementen los endpoints.*

---

## 9. DTOs y Mappers

### 9.1 Naming Convention

| Capa | Tipo | Ejemplo |
|------|------|---------|
| DTO (Data) | `*Dto` | `DailyLogDto`, `TaskDto` |
| Domain Model | Nombre simple | `DailyLog`, `Task` |
| Entity (Room) | `*Entity` | `DailyLogEntity`, `TaskEntity` |

### 9.2 Ejemplo Completo: Daily Log

#### DTO (from API)

```kotlin
@Serializable
data class DailyLogDto(
    val id: Long,
    val userId: Long,
    val date: String,  // "2026-02-03"
    val completionRate: Double,
    val items: List<DailyLogItemDto>
)

@Serializable
data class DailyLogItemDto(
    val id: Long,
    val itemType: String,  // "TASK", "HABIT", "WORKOUT", "MEAL"
    val referenceId: Long,
    val scheduledTime: String?,  // "08:00:00"
    val status: String,  // "PENDING", "COMPLETED", "SKIPPED"
    val task: TaskSummaryDto? = null,
    val habitLog: HabitLogSummaryDto? = null
)
```

#### Domain Model

```kotlin
data class DailyLog(
    val id: Long,
    val userId: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItem>
)

data class DailyItem(
    val id: Long,
    val type: DailyItemType,
    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,
    val title: String,
    val description: String?
)

enum class DailyItemType {
    TASK, HABIT, WORKOUT, MEAL
}

enum class ItemStatus {
    PENDING, COMPLETED, SKIPPED
}
```

#### Mapper

```kotlin
fun DailyLogDto.toDomain(): DailyLog {
    return DailyLog(
        id = id,
        userId = userId,
        date = LocalDate.parse(date),
        completionRate = completionRate,
        items = items.map { it.toDomain() }
    )
}

fun DailyLogItemDto.toDomain(): DailyItem {
    return DailyItem(
        id = id,
        type = DailyItemType.valueOf(itemType),
        referenceId = referenceId,
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
        status = ItemStatus.valueOf(status),
        title = extractTitle(),
        description = extractDescription()
    )
}

private fun DailyLogItemDto.extractTitle(): String {
    return when (itemType) {
        "TASK" -> task?.title ?: "Tarea sin título"
        "HABIT" -> habitLog?.habitName ?: "Hábito sin nombre"
        "WORKOUT" -> "Entrenamiento"
        "MEAL" -> "Comida"
        else -> "Item desconocido"
    }
}
```

---

## 10. Testing

### 10.1 Mock de ApiService

```kotlin
class FakeApiService : GoodLifeApiService {
    var dailyLogResponse: BaseResponse<DailyLogDto>? = null
    
    override suspend fun getDailyLog(date: String): BaseResponse<DailyLogDto> {
        return dailyLogResponse ?: BaseResponse(
            code = 200,
            data = DailyLogDto(
                id = 1,
                userId = 123,
                date = date,
                completionRate = 0.5,
                items = emptyList()
            )
        )
    }
}
```

### 10.2 Test de Repository

```kotlin
class DailyRepositoryTest {

    private lateinit var fakeApiService: FakeApiService
    private lateinit var fakeDao: FakeDailyDao
    private lateinit var repository: DailyRepositoryImpl
    
    @Before
    fun setup() {
        fakeApiService = FakeApiService()
        fakeDao = FakeDailyDao()
        repository = DailyRepositoryImpl(fakeApiService, fakeDao, TestDispatcherProvider())
    }
    
    @Test
    fun `getDailyLog devuelve datos del backend cuando cache está vacío`() = runTest {
        // Given
        val date = LocalDate(2026, 2, 3)
        fakeApiService.dailyLogResponse = BaseResponse(
            code = 200,
            data = DailyLogDto(
                id = 1,
                userId = 123,
                date = "2026-02-03",
                completionRate = 0.5,
                items = emptyList()
            )
        )
        
        // When
        val result = repository.getDailyLog(date)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(date, result.getOrNull()?.date)
    }
    
    @Test
    fun `getDailyLog devuelve cache cuando está fresco`() = runTest {
        // Given
        val date = LocalDate(2026, 2, 3)
        fakeDao.insertDailyLog(DailyLogEntity(
            date = "2026-02-03",
            userId = 123,
            completionRate = 0.75,
            cachedAt = System.currentTimeMillis()  // Recién cacheado
        ))
        
        // When
        val result = repository.getDailyLog(date)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(0.75, result.getOrNull()?.completionRate)
    }
}
```

---

## 11. Mejores Prácticas

### 11.1 DOs ✅

- ✅ **Usar `LocalDate` hasta el Repository**: Solo convertir a String en la capa de datos
- ✅ **Cachear responses exitosos**: Guardar en Room para modo offline
- ✅ **Mapear errores a `ApiException`**: Facilita el manejo en ViewModels
- ✅ **Validar `response.code`**: No asumir que siempre es 200
- ✅ **Usar `suspendResultOf`**: Para wrappear errores en `Result<T>`
- ✅ **DTOs separados de Domain**: Nunca exponer DTOs a la capa de presentación

### 11.2 DON'Ts ❌

- ❌ **No formatear fechas fuera de Repository**: `LocalDate.toString()` es suficiente
- ❌ **No usar `try-catch` en ViewModels**: Delegar a `Result<T>`
- ❌ **No exponer DTOs a UI**: Siempre mapear a Domain Models
- ❌ **No hardcodear mensajes de error**: Usar `response.message` del backend
- ❌ **No ignorar el cache**: Modo offline es crítico para UX

---

## 12. Roadmap

| Fase | Feature | Estado |
|------|---------|--------|
| 1 | Authentication (login, register, refresh) | 🚧 En Desarrollo |
| 1.1 | Session Renewal automático por `401` | 🟡 Pendiente |
| 2 | Daily Logs (today, by date, update status) | 🟡 En Desarrollo |
| 3 | Tasks (CRUD, by date) | 🔴 Pendiente |
| 4 | Habits (CRUD, by date, update progress) | 🔴 Pendiente |
| 5 | Workouts (routines, logs, finish) | 🔴 Pendiente |
| 6 | Meals (catalog, plans) | 🔴 Pendiente |

---

## 12.1 Pendiente de Auth: Refresh Token Automático en `401`

### Objetivo

Implementar renovación automática de sesión para que la app:
- reintente requests fallidos por `401` usando refresh token
- evite logout inmediato cuando solo expiró access token
- mantenga UX fluida y robusta

### Estado

🟡 **Pendiente** (documentado para implementación posterior)

### Diseño propuesto (alto nivel)

1. Interceptar respuestas `401` en capa de network.
2. Excluir endpoints de auth (`/token`, `/register`, `/token/refresh`) para evitar loops.
3. Leer refresh token desde `TokenManager`.
4. Si refresh token válido:
   - llamar endpoint de refresh
   - persistir nuevo access token (`TokenManager.updateAccessToken(...)`)
   - reintentar request original una sola vez.
5. Si falla refresh:
   - limpiar sesión local
   - notificar `Unauthorized` para navegar a login.

### Checklist técnico (pendiente)

- [ ] Definir estrategia: `Authenticator` de OkHttp vs manejo manual en `Interceptor`
- [ ] Implementar guard anti-loop (máximo 1 retry por request)
- [ ] Implementar exclusión de endpoints auth
- [ ] Integrar llamada a `refreshToken(...)`
- [ ] Persistir nuevo access token y expiración
- [ ] Reintentar request original con token renovado
- [ ] Manejar fallback logout cuando refresh falle
- [ ] Agregar tests:
  - [ ] `401` + refresh OK => retry exitoso
  - [ ] `401` + refresh FAIL => sesión inválida
  - [ ] endpoint auth no dispara refresh
  - [ ] no hay loop de reintentos

### Riesgos a cubrir

- Condiciones de carrera (múltiples requests 401 simultáneos)
- Loop infinito de refresh
- Retry sobre requests no idempotentes
- Inconsistencias de token en memoria vs SharedPreferences

---

## 13. Referencias

### 13.1 Archivos del Proyecto

| Archivo | Ubicación |
|---------|-----------|
| `HttpCode.kt` | `core/network/HttpCode.kt` |
| `ApiException.kt` | `core/network/ApiException.kt` |
| `BaseResponse.kt` | `data/remote/dto/response/BaseResponse.kt` |
| `GoodLifeApiService.kt` | `data/remote/api/GoodLifeApiService.kt` |
| `DateProvider.kt` | `core/datetime/DateProvider.kt` |

### 13.2 SPECs Relacionados

- `SPEC-004-date-provider.md`: Manejo de fechas y timezone
- `SPEC-003-main-scaffold.md`: Arquitectura de navegación
- Backend API Docs: `/back-end/GoodLife-backend-v2/docs/API_ENDPOINTS.md`

---

**FIN DE SPEC-005**

---

**Autor:** GoodLife Development Team  
**Fecha:** 2026-02-04  
**Versión:** 1.0  
**Estado:** 📝 En Desarrollo
