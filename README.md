# GoodLife Android 🏋️‍♂️

Aplicación Android para gestión de bienestar personal: tareas diarias, hábitos, entrenamientos y alimentación.

## ✨ Features Implementadas

- ✅ **Autenticación completa** con backend real (JWT)
- ✅ **Login/Logout** funcional
- ✅ **Login Biométrico** (huella, Face ID, PIN, patrón)
- ✅ **Registro de usuarios** con validación en tiempo real
- ✅ **Persistencia local** con Room + SWR cache (fallback offline)
- ✅ **Navegación type-safe** con Compose Navigation
- ✅ **Daily Tab** con `DailyItemCard`, refresh automático y SWR
- ✅ **Create Task** (SPEC-010) — modos once/recurrente, DatePicker, TimePicker
- ✅ **Create Habit** — categoría, meta numérica, días de la semana
- ✅ **Create Routine** (SPEC-011) — wizard 4 pasos, catálogo paginado de ejercicios, sets
- ✅ **Create Meal Plan** (SPEC-012) — wizard 3 pasos, ingredientes, macros, scheduling
- ✅ **UI optimizada** con `@Stable` states, `ImmutableList`, `@Immutable`
- ✅ **Arquitectura KMP-Ready** (domain layer en Kotlin puro)
- ✅ **163 tests unitarios** y gate JaCoCo de lógica aprobado (80,07% líneas)
- ✅ **Auto-login / chequeo de sesión** con token cifrado
- 🚧 **Detalles y acciones de tabs**: ver [readiness de reingreso](docs/REVIVAL-READINESS-2026-09.md)

## 🚀 Quick Start

### Requisitos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Kotlin 2.0+
- Android SDK 35

### Instalación

```bash
# Clonar el repositorio
git clone <repository-url>

# Abrir en Android Studio
# File → Open → Seleccionar carpeta Android

# Sincronizar Gradle
# Click en "Sync Now" o Ctrl+Shift+O

# Ejecutar
# Click en Run (▶) o Shift+F10
```

### Credenciales de Prueba (DEV, tras migración Flyway V23+)
```
Usuario: [configurar cuenta de smoke fuera del repositorio]
Password: [configurar cuenta de smoke fuera del repositorio]
```
Rol JWT incluye `USER`, `ADMIN`, `PREMIUM` y `FULL_ADMIN` (este último solo en este usuario de seed).

## 📁 Estructura del Proyecto

```
app/src/main/java/com/agusstkd/goodlife/
├── core/                    # Utilidades compartidas (KMP-ready)
│   ├── dispatcher/          # CoroutineDispatchers
│   ├── network/             # Interceptor, HttpCodes, ApiException
│   ├── result/              # Result<T> wrapper
│   └── storage/             # TokenManager (SharedPreferences)
│
├── data/                    # Capa de datos
│   ├── local/               # Room: Database, DAOs, Entities
│   ├── remote/              # Retrofit: ApiService, DataSources, DTOs
│   └── repository/          # Implementaciones de Repository
│
├── domain/                  # Lógica de negocio (Kotlin puro)
│   ├── model/               # Modelos de dominio
│   ├── repository/          # Interfaces de Repository
│   └── usecase/             # Casos de uso
│
├── presentation/            # UI Layer
│   ├── components/          # Componentes reutilizables
│   ├── navigation/          # Sistema de navegación reactivo
│   ├── screen/              # pantallas: splash, login, register, main, tabs/daily, add/*
│   └── theme/               # Material3 Theme
│
├── di/                      # Koin: AppModule(core), Network, Database, Biometric, Auth, Daily, Task, Habit, Routine, Nutrition
```

## 🏗️ Arquitectura

- **Clean Architecture** con capas Domain, Data, Presentation
- **MVVM** para la capa de presentación
- **Koin** para inyección de dependencias
- **Navegación Reactiva** con SharedFlow
- **Type-safe Routes** con kotlinx-serialization
- **KMP Ready** - Domain y Core son Kotlin puro

Ver [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) y el inventario [docs/IMPLEMENTATION-STATUS.md](docs/IMPLEMENTATION-STATUS.md).

## 🔐 Autenticación

### Flujo de Login
```
LoginScreen → LoginViewModel → LoginUseCase → AuthRepository
                                    ↓
                            AuthRemoteDataSource → API
                                    ↓
                            TokenManager (guarda JWT)
                                    ↓
                            UserDao (guarda user en Room)
                                    ↓
                            Navega a HomeScreen
```

### Backend
- URL: `https://good-life.ddns.net/`
- Swagger: [API Documentation](https://good-life.ddns.net/swagger-ui/index.html)
- Estado de recuperación y validación Android: [REVIVAL-READINESS-2026-09.md](docs/REVIVAL-READINESS-2026-09.md)

## 🧭 Navegación

Sistema de navegación desacoplado y lifecycle-aware:

```kotlin
// En ViewModel
navigationController.navigateToMain()
navigationController.navigateToLoginFromMain() // Logout

// Rutas type-safe
AppRoute.Login      // Pantalla de login
AppRoute.Main       // Pantalla principal
TabRoute.WorkoutDetail(workoutId = 5)  // Con parámetros
```

## 📦 Dependencias Principales

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| Kotlin | 2.0.21 | Lenguaje |
| Compose BOM | 2024.04.01 | UI Framework |
| Navigation Compose | 2.8.5 | Navegación |
| Koin | 4.0.0 | DI |
| Kotlinx Serialization | 1.7.3 | Type-safe routes & JSON |
| Retrofit | 2.11.0 | HTTP Client |
| OkHttp | 4.12.0 | HTTP + Logging |
| Room | 2.7.0-alpha03 | Local Database |

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## 📝 Convenciones

### Commits
Seguimos [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bugs
- `docs:` Documentación
- `refactor:` Refactorización

### UI States
- Usar `sealed interface` para estados
- Marcar con `@Stable` para optimizar recomposición
- Extraer a carpeta `model/` dentro de cada screen

### Mappers
- Usar extension functions: `toAuthToken()`, `toDomain()`, `fromDomain()`

## 📄 Documentación

- [ARCHITECTURE.md](docs/ARCHITECTURE.md) - Arquitectura detallada
- [NAVIGATION.md](docs/NAVIGATION.md) - Sistema de navegación
- [CHANGELOG.md](CHANGELOG.md) - Historial de cambios

## 🗺️ Roadmap

- [x] Pantalla de Registro
- [x] Login Biométrico
- [x] Daily Tab completo (DailyItemCard + SWR)
- [x] Create Task (SPEC-010)
- [x] Create Habit
- [x] Create Routine (SPEC-011)
- [x] Create Meal Plan (SPEC-012)
- [x] 163 tests unitarios + gate JaCoCo de líneas >=80%
- [x] Auto-login con token guardado
- [x] Tabs Workouts, Meals y Settings con Owner/Screen/ViewModel
- [ ] Navegación y detalles reales para Daily, Workout y Meal (hoy hay placeholders)
- [ ] Sincronizar perfil con `GET /api/v1/me` en vez de derivarlo del JWT
- [ ] SWR Fase 2 — cola de sincronización offline
- [ ] Notificaciones push + Deep Links (SPEC-008)
- [ ] Dark mode completo
- [ ] Sistema de gamificación (XP, niveles)

## 📜 Licencia

Proyecto privado - Todos los derechos reservados.
