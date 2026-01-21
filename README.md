# GoodLife Android 🏋️‍♂️

Aplicación Android para gestión de bienestar personal: tareas diarias, hábitos, entrenamientos y alimentación.

## ✨ Features Implementadas

- ✅ **Autenticación completa** con backend real (JWT)
- ✅ **Login/Logout** funcional
- ✅ **Login Biométrico** (huella, Face ID, PIN, patrón)
- ✅ **Registro de usuarios** con validación en tiempo real
- ✅ **Persistencia local** con Room (usuario logueado)
- ✅ **Navegación type-safe** con Compose Navigation
- ✅ **UI optimizada** con `@Stable` states
- ✅ **Arquitectura KMP-Ready** (domain layer en Kotlin puro)
- 🚧 Auto-login con token guardado

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

### Credenciales de Prueba
```
Usuario: agusstkd
Password: 2209
```

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
│   ├── screen/              # Pantallas (MVVM)
│   │   ├── splash/model/    # UiState, UiAction (@Stable)
│   │   ├── login/model/     # UiState, UiAction (@Stable)
│   │   └── home/model/      # UiState, UiAction (@Stable)
│   └── theme/               # Material3 Theme
│
└── di/                      # Koin modules
    ├── AppModule.kt
    ├── NetworkModule.kt
    └── DatabaseModule.kt
```

## 🏗️ Arquitectura

- **Clean Architecture** con capas Domain, Data, Presentation
- **MVVM** para la capa de presentación
- **Koin** para inyección de dependencias
- **Navegación Reactiva** con SharedFlow
- **Type-safe Routes** con kotlinx-serialization
- **KMP Ready** - Domain y Core son Kotlin puro

Ver [ARCHITECTURE.md](docs/ARCHITECTURE.md) para detalles completos.

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
- URL: `https://devtukychloe.ddns.net/`
- Swagger: [API Documentation](https://devtukychloe.ddns.net/swagger-ui/index.html)

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

- [x] Pantalla de Registro ✅
- [x] Login Biométrico ✅
- [ ] Auto-login con token guardado
- [ ] Refresh token automático
- [ ] Modo offline con caché
- [ ] Pantallas principales (Tasks, Habits, Workouts, Meals)
- [ ] Sistema de gamificación (XP, niveles)
- [ ] Notificaciones push

## 📜 Licencia

Proyecto privado - Todos los derechos reservados.
