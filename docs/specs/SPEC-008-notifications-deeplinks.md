# SPEC-008: Notificaciones y Deep Links

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-008 |
| **Tipo** | Feature |
| **Prioridad** | Media |
| **Estado** | 📝 Planificado |
| **Fecha Creación** | 2026-02-24 |
| **Backend Spec** | `back-end/docs/specs/modules/notifications.spec.md` |
| **Dependencias** | SPEC-005 (Network), SPEC-007 (AppLanguage) |

---

## 🎯 Objetivo

Implementar en el cliente Android:

1. **Notificaciones en tiempo real (WebSocket/STOMP)** — mientras la app está abierta, recibir recordatorios de tareas/hábitos y eventos de gamificación enviados por el backend.
2. **Notificaciones del sistema Android** — mostrar una `Notification` nativa cuando la app está en segundo plano.
3. **Deep links** — que una notificación (o una URL externa) abra directamente la pantalla correcta dentro de la app.

---

## 📦 Alcance

### ✅ Fase 1 — v2.0 (WebSockets + Deep links)
- Conexión STOMP al WebSocket del backend
- Recibir y procesar los 12 tipos de `NotificationType` definidos en el backend
- Mostrar notificaciones nativas de Android con `NotificationCompat`
- Registrar deep links en `AndroidManifest.xml` para todas las rutas relevantes
- Guard de autenticación cuando llega un deep link con sesión inválida

### 📋 Fase 2 — v3.0 (Push Notifications)
- Firebase Cloud Messaging para notificaciones cuando la app está cerrada
- FCM token → backend para targeting por usuario

### ❌ No cubre este SPEC
- Pantalla de centro de notificaciones (lista de notificaciones leídas/no leídas)
- Configuración de preferencias de notificación por tipo
- Emails transaccionales (responsabilidad del backend)

---

## 🏗️ Arquitectura

### Flujo completo — Notificación en tiempo real

```
Backend (Spring WebSocket)
        │
        │  STOMP /user/queue/notifications
        ▼
WebSocketDataSource         ← data/ — gestiona conexión STOMP
        │
        │  NotificationMessageDto (JSON)
        ▼
NotificationRepositoryImpl  ← data/ — mapea DTO → Domain Model
        │
        │  Flow<NotificationMessage>
        ▼
NotificationRepository      ← domain/ — interfaz
        │
        ├──► MainScaffoldViewModel  ← presentation/ — escucha el Flow
        │           │
        │           ▼
        │    Mostrar In-App Notification Banner (Snackbar/Toast)
        │
        └──► NotificationDisplayService  ← core/ — notificación del sistema Android
                    │
                    ▼
             NotificationCompat  (visible en status bar)
             con PendingIntent → deep link
```

### Flujo completo — Deep Link

```
Fuente externa (notificación tap / URL / ADB)
        │
        │  Intent con URI goodlife://...
        ▼
MainActivity.onCreate() / onNewIntent()
        │
        ▼
GoodLifeNavHost             ← presentation/navigation/
        │
        ├── Sesión válida → navegar a la ruta directamente
        │
        └── Sesión inválida → ir a Login → post-login navegar al deep link
```

---

## 📡 WebSocket / STOMP

### Dependencia a agregar en `build.gradle`

```kotlin
// Krossbow STOMP (Kotlin-first, coroutines-native, sin Gson/SockJS legacy)
implementation("org.hildan.krossbow:krossbow-stomp-core:7.x")
implementation("org.hildan.krossbow:krossbow-websocket-okhttp:7.x")
```

> **Por qué Krossbow y no la librería STOMP de Genymotion/UA:**
> La librería `ua.naiksoftware:stomp-protocolandroid` usa callbacks y no tiene soporte oficial de coroutines. Krossbow es Kotlin-first con `Flow` y `suspend fun`, compatible con KMP.

### Modelo de dominio

```kotlin
// domain/model/notification/NotificationMessage.kt
data class NotificationMessage(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val data: Map<String, String>,   // Kotlin puro — String values para KMP
    val timestamp: LocalDateTime     // kotlinx-datetime
)

// domain/model/notification/NotificationType.kt
enum class NotificationType {
    // Recordatorios
    TASK_REMINDER,
    HABIT_REMINDER,
    WORKOUT_REMINDER,
    MEAL_REMINDER,

    // Logros
    STREAK_ACHIEVED,
    STREAK_BROKEN,
    LEVEL_UP,
    BADGE_UNLOCKED,
    PERFECT_DAY,
    NEW_RECORD,

    // Sistema
    SYSTEM_INFO,
    SYSTEM_WARNING
}
```

> **Nota sobre `data: Map<String, String>`:**
> El backend define `data: Map<String, Any>?`. En el DTO Android se usa `Map<String, String>` —
> todos los valores se serializan como String en JSON de todas formas, y evita `Any` en Kotlin
> (que no es KMP-friendly).

### DTO (capa data)

```kotlin
// data/websocket/dto/NotificationMessageDto.kt
@Serializable
data class NotificationMessageDto(
    val id: String,
    val type: String,                       // String porque viene como enum name del backend
    val title: String,
    val message: String,
    val data: Map<String, String>? = null,
    val timestamp: String                   // ISO-8601 string del backend
)
```

### Mapper DTO → Domain

```kotlin
// data/websocket/dto/NotificationMessageDto.kt (extension)
fun NotificationMessageDto.toDomain(): NotificationMessage {
    return NotificationMessage(
        id        = id,
        type      = runCatching { NotificationType.valueOf(type) }
                        .getOrDefault(NotificationType.SYSTEM_INFO),
        title     = title,
        message   = message,
        data      = data ?: emptyMap(),
        timestamp = LocalDateTime.parse(timestamp)
    )
}
```

### DataSource

```kotlin
// data/websocket/WebSocketDataSource.kt
class WebSocketDataSource(
    private val tokenManager: TokenManager,
    private val baseUrl: String
) {
    private val client = StompClient(OkHttpWebSocketClient())

    /**
     * Conecta al WebSocket del backend y emite notificaciones como Flow.
     * El Flow se cancela automáticamente cuando se cancela la coroutine padre.
     */
    fun observeNotifications(): Flow<NotificationMessageDto> = flow {
        val headers = StompSendHeaders(
            customHeaders = mapOf("Authorization" to "Bearer ${tokenManager.getAccessToken()}")
        )
        client.connect("$baseUrl/ws", headers).use { session ->
            session.subscribe("/user/queue/notifications")
                .collect { frame ->
                    val dto = Json.decodeFromString<NotificationMessageDto>(frame.bodyAsText)
                    emit(dto)
                }
        }
    }
}
```

### Repository

```kotlin
// domain/repository/NotificationRepository.kt
interface NotificationRepository {
    /**
     * Flow de notificaciones en tiempo real desde el backend.
     * El Flow activo mantiene la conexión WebSocket abierta.
     */
    fun observeNotifications(): Flow<NotificationMessage>
}

// data/repository/NotificationRepositoryImpl.kt
class NotificationRepositoryImpl(
    private val dataSource: WebSocketDataSource
) : NotificationRepository {

    override fun observeNotifications(): Flow<NotificationMessage> =
        dataSource.observeNotifications().map { it.toDomain() }
}
```

---

## 🔔 Notificaciones del Sistema Android

Cuando la app está en background o la notificación requiere persistencia en la status bar,
se usa `NotificationCompat` con un `PendingIntent` que contiene el deep link.

### Canales de notificación (Android 8+)

```kotlin
// core/notification/NotificationChannels.kt
object NotificationChannels {
    const val REMINDERS  = "goodlife_reminders"   // Recordatorios de tareas/hábitos
    const val GAMIFICATION = "goodlife_gamification" // Logros, niveles, rachas
    const val SYSTEM     = "goodlife_system"      // Mensajes del sistema

    fun createAll(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(REMINDERS, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Recordatorios de tareas y hábitos" }
        )
        manager.createNotificationChannel(
            NotificationChannel(GAMIFICATION, "Logros", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Niveles, rachas y badges" }
        )
        manager.createNotificationChannel(
            NotificationChannel(SYSTEM, "Sistema", NotificationManager.IMPORTANCE_LOW)
        )
    }
}
```

> Llamar `NotificationChannels.createAll(context)` en `GoodLifeApp.onCreate()`.

### Selector de canal por tipo

```kotlin
fun NotificationType.toChannelId(): String = when (this) {
    NotificationType.TASK_REMINDER,
    NotificationType.HABIT_REMINDER,
    NotificationType.WORKOUT_REMINDER,
    NotificationType.MEAL_REMINDER    -> NotificationChannels.REMINDERS
    NotificationType.STREAK_ACHIEVED,
    NotificationType.STREAK_BROKEN,
    NotificationType.LEVEL_UP,
    NotificationType.BADGE_UNLOCKED,
    NotificationType.PERFECT_DAY,
    NotificationType.NEW_RECORD       -> NotificationChannels.GAMIFICATION
    NotificationType.SYSTEM_INFO,
    NotificationType.SYSTEM_WARNING   -> NotificationChannels.SYSTEM
}
```

---

## 🔗 Deep Links

### Esquema de URL

```
goodlife://{ruta}/{parámetros}
```

### Tabla de rutas

| Deep Link | Pantalla destino | Parámetros |
|-----------|-----------------|------------|
| `goodlife://daily` | Tab Daily — hoy | — |
| `goodlife://daily/{date}` | Tab Daily — fecha específica | `date` = YYYY-MM-DD |
| `goodlife://tasks/{id}` | Detalle de tarea | `id` = Long |
| `goodlife://habits/{id}` | Detalle de hábito | `id` = Long |
| `goodlife://workouts/{id}` | Detalle de rutina/workout | `id` = Long |
| `goodlife://gamification` | Pantalla gamificación (niveles, badges) | — |
| `goodlife://login` | Pantalla de login | — |
| `goodlife://reset-password` | Flujo de recuperación de contraseña | — |

### Mapping NotificationType → Deep Link

El backend incluye el campo `deepLink` en el `data` map de cada `NotificationMessage`.

| `NotificationType` | Deep link esperado en `data["deepLink"]` |
|---|---|
| `TASK_REMINDER` | `goodlife://tasks/{data["itemId"]}` |
| `HABIT_REMINDER` | `goodlife://habits/{data["habitId"]}` |
| `WORKOUT_REMINDER` | `goodlife://daily` |
| `MEAL_REMINDER` | `goodlife://daily` |
| `STREAK_ACHIEVED` | `goodlife://habits/{data["habitId"]}` |
| `STREAK_BROKEN` | `goodlife://habits/{data["habitId"]}` |
| `LEVEL_UP` | `goodlife://gamification` |
| `BADGE_UNLOCKED` | `goodlife://gamification` |
| `PERFECT_DAY` | `goodlife://daily/{data["date"]}` |
| `NEW_RECORD` | `goodlife://gamification` |
| `SYSTEM_INFO` / `SYSTEM_WARNING` | Sin deep link (notificación informativa) |

### Registro en `AndroidManifest.xml`

```xml
<activity
    android:name=".MainActivity"
    android:launchMode="singleTask">

    <!-- Launcher intent normal -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>

    <!-- Deep links con esquema custom goodlife:// -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="goodlife" />
    </intent-filter>

</activity>
```

> **`android:launchMode="singleTask"`:** Evita que se creen múltiples instancias de la
> Activity cuando llega un deep link con la app ya abierta. El Intent llega a `onNewIntent()`.

### Cambios en Compose Navigation

```kotlin
// presentation/navigation/graph/TabNavGraphs.kt — agregar deepLinks
composable<TabRoute.Daily>(
    deepLinks = listOf(
        navDeepLink { uriPattern = "goodlife://daily" },
        navDeepLink { uriPattern = "goodlife://daily/{date}" }
    )
) { backStackEntry ->
    val date = backStackEntry.arguments?.getString("date")
    DailyScreenOwner(initialDate = date)
}

// presentation/navigation/host/GoodLifeNavHost.kt — manejar deepLink pendiente
@Composable
fun GoodLifeNavHost(
    navController: NavHostController,
    navigationController: ComposeNavigationController,
    checkSession: () -> Boolean
) {
    // Capturar el deep link del Intent inicial
    val activity = LocalContext.current as? Activity
    val initialDeepLink = remember {
        activity?.intent?.data?.toString()
    }

    LaunchedEffect(Unit) {
        if (initialDeepLink != null && !checkSession()) {
            // Sesión inválida → ir a Login y guardar el deep link pendiente
            // El NavigationController lo resolverá post-login
        }
    }
    // ... resto del NavHost
}
```

---

## 📁 Archivos del Sistema

### Archivos a crear

| Archivo | Capa | Descripción |
|---------|------|-------------|
| `domain/model/notification/NotificationMessage.kt` | Domain | Modelo de dominio |
| `domain/model/notification/NotificationType.kt` | Domain | Enum de tipos |
| `domain/repository/NotificationRepository.kt` | Domain | Interfaz del repositorio |
| `data/websocket/dto/NotificationMessageDto.kt` | Data | DTO + mapper toDomain() |
| `data/websocket/WebSocketDataSource.kt` | Data | Conexión STOMP |
| `data/repository/NotificationRepositoryImpl.kt` | Data | Implementación |
| `core/notification/NotificationChannels.kt` | Core | Canales Android |
| `di/NotificationModule.kt` | DI | Módulo Koin |

### Archivos a modificar

| Archivo | Cambio |
|---------|--------|
| `GoodLifeApp.kt` | Llamar `NotificationChannels.createAll()` en `onCreate()` y agregar `notificationModule` |
| `AndroidManifest.xml` | Agregar `intent-filter` de deep links + `singleTask` |
| `presentation/navigation/graph/TabNavGraphs.kt` | Agregar `deepLinks` a las rutas existentes |
| `presentation/navigation/host/GoodLifeNavHost.kt` | Manejar Intent de deep link inicial |
| `presentation/screen/main/MainScaffoldViewModel.kt` | Escuchar el `Flow<NotificationMessage>` del repositorio para mostrar banners |
| `build.gradle` (app) | Agregar dependencia Krossbow |

---

## 🔧 Módulo Koin

```kotlin
// di/NotificationModule.kt
val notificationModule = module {

    factory {
        WebSocketDataSource(
            tokenManager = get(),
            baseUrl = BuildConfig.BASE_URL   // ej: "http://10.0.2.2:9090"
        )
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(dataSource = get())
    }
}
```

---

## 🏛️ Reglas Arquitectónicas

| Regla | Descripción |
|-------|-------------|
| ✅ OBLIGATORIO | `NotificationMessage` y `NotificationType` son Kotlin puro (KMP-ready). Sin imports de Android. |
| ✅ OBLIGATORIO | El `Flow<NotificationMessage>` lo observa `MainScaffoldViewModel`, no las Screens directamente. |
| ✅ OBLIGATORIO | Los deep links se registran en las rutas de navegación, NO en los Owners o Screens. |
| ✅ OBLIGATORIO | La guard de autenticación se maneja en `GoodLifeNavHost`, no en cada pantalla destino. |
| ❌ PROHIBIDO | Usar `Gson` para deserializar mensajes STOMP. Usar `kotlinx-serialization`. |
| ❌ PROHIBIDO | Guardar el `StompSession` como variable global o singleton. Usarlo dentro de un `flow { }` con ciclo de vida acotado. |
| ❌ PROHIBIDO | `NotificationType.valueOf()` sin try-catch. El backend puede agregar tipos nuevos — usar `runCatching { }.getOrDefault(SYSTEM_INFO)`. |

---

## 🚀 Cómo Extender

### Agregar un nuevo tipo de notificación

1. Agregar el nuevo valor al `enum class NotificationType` en domain.
2. Actualizar `NotificationType.toChannelId()` en `core/notification/`.
3. Agregar la entrada en la tabla de Mapping (sección deep links) de este spec.
4. Si requiere deep link nuevo, agregar la ruta en `TabNavGraphs.kt` o `AppGraph.kt`.
5. Actualizar el backend spec (`notifications.spec.md`) para incluir el campo `deepLink` en el `data` map.

### Agregar una nueva ruta de deep link

1. Definir el patrón `goodlife://{ruta}` en la tabla de este SPEC.
2. Agregar `deepLinks = listOf(navDeepLink { uriPattern = "goodlife://..." })` al `composable` correspondiente en `TabNavGraphs.kt`.
3. Verificar con ADB:

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "goodlife://daily/2026-02-24" \
  com.agusstkd.goodlife
```

---

## 🧪 Checklist de Validación

### WebSocket
- [ ] La conexión STOMP se establece correctamente al iniciar la app con sesión válida
- [ ] La conexión se cierra limpiamente cuando el ViewModel se destruye (scope cancelado)
- [ ] Si el token expira durante la conexión, el `GoodLifeAuthenticator` hace refresh y la conexión se restablece
- [ ] `NotificationType` desconocido → fallback a `SYSTEM_INFO` sin crash
- [ ] Notificación recibida con app en foreground → banner visible en la pantalla actual

### Android Notifications
- [ ] Canal `goodlife_reminders` creado en el primer launch
- [ ] Canal `goodlife_gamification` creado en el primer launch
- [ ] Notificación visible en status bar cuando la app está en background
- [ ] Tap en notificación del sistema → abre la app en la pantalla correcta via deep link

### Deep Links
- [ ] `goodlife://daily` → abre el Tab Daily mostrando hoy
- [ ] `goodlife://daily/2026-02-24` → abre el Tab Daily en esa fecha
- [ ] `goodlife://tasks/123` → abre detalle de la tarea con id 123
- [ ] `goodlife://habits/45` → abre detalle del hábito con id 45
- [ ] `goodlife://gamification` → abre pantalla de gamificación
- [ ] Deep link con sesión inválida → redirige a Login, post-login navega al destino correcto
- [ ] Deep link con app cerrada → lanza la app y navega a la pantalla correcta

---

## 📎 Referencias

- **Backend Spec:** `back-end/GoodLife-backend-v2/docs/specs/modules/notifications.spec.md`
- **API Endpoints:** `back-end/GoodLife-backend-v2/docs/API_ENDPOINTS.md`
- **SPEC-005 Network:** Configuración de OkHttp/Retrofit (el mismo `OkHttpClient` se reutiliza para el WebSocket)
- **Android Docs:** [Notification Channels](https://developer.android.com/develop/ui/views/notifications/channels)
- **Krossbow:** [github.com/joffrey-bion/krossbow](https://github.com/joffrey-bion/krossbow)
- **Compose Navigation Deep Links:** [developer.android.com/jetpack/compose/navigation#deeplinks](https://developer.android.com/jetpack/compose/navigation#deeplinks)

---

**Última actualización:** 2026-02-24
