# Arquitectura GoodLife Android

> **📚 Documento completo:** Para la guía exhaustiva de arquitectura con todos los principios, patrones y reglas, ver **[ARCHITECTURE_GUIDE.md](./ARCHITECTURE_GUIDE.md)**

## Visión General

GoodLife utiliza **Clean Architecture** con **MVVM** y está diseñada para ser **KMP-ready** (Kotlin Multiplatform).

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                           │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────┐│
│  │ Screens │  │ Owners  │  │ViewModels│ │   Navigation    ││
│  │@Stable  │  │         │  │          │ │   (SharedFlow)  ││
│  └─────────┘  └─────────┘  └─────────┘  └─────────────────┘│
├─────────────────────────────────────────────────────────────┤
│                        DOMAIN                               │
│  ┌─────────┐  ┌─────────┐  ┌─────────────────────────────┐ │
│  │ UseCases│  │ Models  │  │    Repository Interfaces    │ │
│  │         │  │(Kotlin) │  │                             │ │
│  └─────────┘  └─────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                         DATA                                │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────┐   │
│  │ Repositories│  │ DataSources │  │ Entities/DTOs     │   │
│  │  (Impl)     │  │Remote/Local │  │ + Mappers         │   │
│  └─────────────┘  └─────────────┘  └───────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│                         CORE                                │
│  ┌─────────┐  ┌─────────────────┐  ┌───────────────────┐   │
│  │ Result  │  │DispatcherProvider│ │ Network/Storage   │   │
│  └─────────┘  └─────────────────┘  └───────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Estructura de Paquetes (Actualizada)

```
com.agusstkd.goodlife/
├── core/                          # Utilidades compartidas (KMP-ready)
│   ├── biometric/                 # Biometría
│   │   ├── BiometricResult.kt
│   │   └── BiometricAvailability.kt
│   ├── datetime/                  # Manejo de fechas (KMP-ready)
│   │   ├── LocalDateExtensions.kt # isToday, isYesterday, toFriendlyString
│   │   └── language/
│   │       ├── AppLanguage.kt     # Spanish, English, Portuguese
│   │       └── DateFormats.kt     # DateFormats + RelativeDateTexts
│   ├── dispatcher/                # Abstracción de Dispatchers
│   │   └── DispatcherProvider.kt
│   ├── network/                   # Configuración de red
│   │   ├── NetworkConstants.kt    # BASE_URL, PUBLIC_ENDPOINTS
│   │   ├── HttpCode.kt            # Enum de códigos HTTP
│   │   ├── ApiException.kt        # Excepción personalizada
│   │   └── GoodLifeInterceptor.kt # OkHttp Interceptor + logging
│   ├── result/                    # Wrapper para operaciones
│   │   └── Result.kt
│   └── storage/                   # Almacenamiento local
│       └── TokenManager.kt        # JWT con SharedPreferences
│
├── data/                          # Capa de datos
│   ├── local/                     # Room database
│   │   ├── dao/
│   │   │   └── UserDao.kt
│   │   ├── database/
│   │   │   └── GoodLifeDatabase.kt  # Singleton thread-safe
│   │   └── entity/
│   │       └── UserEntity.kt        # + extension mappers
│   │
│   ├── remote/                    # Retrofit API
│   │   ├── api/
│   │   │   └── GoodLifeApiService.kt
│   │   ├── datasource/
│   │   │   └── AuthRemoteDataSource.kt
│   │   └── dto/
│   │       ├── request/
│   │       │   └── RegisterRequest.kt
│   │       └── response/
│   │           ├── BaseResponse.kt      # Wrapper del backend
│   │           ├── AuthResponse.kt      # + extension mapper
│   │           └── RegisterResponse.kt  # + extension mapper
│   │
│   └── repository/                # Implementaciones
│       └── AuthRepositoryImpl.kt
│
├── di/                            # Inyección de dependencias
│   ├── AppModule.kt               # Core, UseCases, ViewModels
│   ├── NetworkModule.kt           # OkHttp, Retrofit, ApiService
│   └── DatabaseModule.kt          # Room, DAOs
│
├── domain/                        # Lógica de negocio (Kotlin puro)
│   ├── model/                     # Modelos de dominio
│   │   ├── auth/
│   │   │   ├── User.kt
│   │   │   └── AuthToken.kt
│   │   ├── daily/
│   │   │   ├── DailyItemType.kt   # TASK, HABIT, WORKOUT, MEAL
│   │   │   └── ItemStatus.kt      # PENDING, IN_PROGRESS, COMPLETED, SKIPPED
│   │   ├── nutrition/
│   │   │   └── MealType.kt        # BREAKFAST, LUNCH, DINNER, etc.
│   │   └── validation/
│   │       └── ValidationResult.kt
│   ├── repository/                # Interfaces de repositorio
│   │   └── AuthRepository.kt
│   └── usecase/                   # Casos de uso
│       ├── home/
│       │   ├── GetCurrentUserUseCase.kt
│       │   └── LogoutUseCase.kt
│       ├── login/
│       │   └── LoginUseCase.kt
│       └── validation/
│           ├── ValidateEmailUseCase.kt
│           └── ValidatePasswordUseCase.kt
│
├── presentation/                  # UI y ViewModels
│   ├── components/                # Componentes reutilizables
│   │   ├── common/
│   │   │   ├── ButtonComponent.kt
│   │   │   ├── TextFieldComponent.kt
│   │   │   ├── CheckboxComponent.kt
│   │   │   ├── TitleComponent.kt
│   │   │   └── BackgroundGradientComponent.kt
│   │   ├── bottom/                # Bottom Navigation
│   │   │   ├── BottomNavigationComponent.kt
│   │   │   └── model/
│   │   │       ├── BottomMenuOption.kt
│   │   │       └── BottomNavItemModel.kt
│   │   ├── header/                # Headers dinámicos por tab
│   │   │   ├── DateHeaderComponent.kt      # Para Daily/Meals
│   │   │   ├── TabRowHeaderComponent.kt    # Para Workouts
│   │   │   └── CalendarDayIcon.kt          # Icono personalizado
│   │   ├── modal/                 # Modal de acciones
│   │   │   ├── AddActionModalComponent.kt
│   │   │   └── model/
│   │   │       ├── QuickActionType.kt
│   │   │       ├── QuickActionItem.kt
│   │   │       └── MealOptionItem.kt
│   │   └── GradientIcon.kt
│   │
│   ├── navigation/                # Sistema de navegación
│   │   ├── core/
│   │   │   ├── NavigationAction.kt
│   │   │   ├── ComposeNavigationController.kt
│   │   │   └── ComposeNavigationControllerImpl.kt
│   │   ├── host/
│   │   │   └── SmartNavHost.kt
│   │   └── route/
│   │       ├── AppRoute.kt
│   │       ├── TabRoute.kt
│   │       ├── NavigationExtensions.kt
│   │       └── AppGraph.kt
│   │
│   ├── screen/                    # Pantallas (MVVM)
│   │   ├── splash/
│   │   │   ├── SplashScreen.kt
│   │   │   ├── SplashScreenOwner.kt
│   │   │   ├── SplashViewModel.kt
│   │   │   └── model/
│   │   │       ├── SplashUiState.kt    # @Stable
│   │   │       └── SplashUiEvent.kt    # @Stable
│   │   │
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   ├── LoginScreenOwner.kt
│   │   │   ├── LoginViewModel.kt
│   │   │   └── model/
│   │   │       ├── LoginUiState.kt     # @Stable
│   │   │       └── LoginUiAction.kt    # @Stable
│   │   │
│   │   └── home/
│   │       ├── HomeScreen.kt
│   │       ├── HomeViewModel.kt        # (Owner integrado)
│   │       └── model/
│   │           ├── HomeUiState.kt      # @Stable
│   │           └── HomeUiAction.kt     # @Stable
│   │
│   └── theme/                     # Material Theme
│       ├── Color.kt               # Paleta completa light/dark + brushes
│       ├── Font.kt                # FontFamily (Ubuntu)
│       ├── Shape.kt               # GoodLifeShapes (MaterialTheme.shapes)
│       ├── Theme.kt               # GoodLifeTheme (integra todo)
│       └── Type.kt                # GoodLifeTypography + estilos custom
│
├── GoodLifeApp.kt                 # Application class (Koin init)
└── MainActivity.kt                # Single Activity
```

## Sistema de Navegación

### Flujo de Navegación

```
┌──────────────┐     emit()      ┌─────────────────────────┐
│  ViewModel   │ ───────────────▶│ ComposeNavigationController│
│              │                 │      (SharedFlow)       │
└──────────────┘                 └───────────┬─────────────┘
                                             │
                                             │ collect()
                                             │ (repeatOnLifecycle)
                                             ▼
                                 ┌─────────────────────────┐
                                 │      SmartNavHost       │
                                 │                         │
                                 │  navController.navigate()│
                                 └─────────────────────────┘
```

### Componentes

| Componente | Responsabilidad |
|------------|-----------------|
| `NavigationAction` | Sealed class con tipos de navegación |
| `ComposeNavigationController` | Interface que usan los ViewModels |
| `ComposeNavigationControllerImpl` | Implementación con SharedFlow |
| `SmartNavHost` | Observa el flow y ejecuta navegación |
| `AppRoute` / `TabRoute` | Rutas type-safe con @Serializable |
| `NavigationExtensions` | Funciones de conveniencia |

### Uso en ViewModel

```kotlin
class LoginViewModel(
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    fun onLoginSuccess() {
        // Navega a Main limpiando el stack
        navigationController.navigateToMain()
    }
    
    fun onRegisterClicked() {
        // Navega a Register
        navigationController.navigateToRegister()
    }
}
```

### ¿Por qué SharedFlow y no StateFlow?

| Característica | StateFlow | SharedFlow |
|----------------|-----------|------------|
| Retiene último valor | ✅ Sí | ❌ No |
| Replay en rotación | ✅ Sí | ❌ No |
| Uso ideal | UI State | One-shot events |

**Navegación = One-shot event** → SharedFlow evita re-navegación al rotar.

## Patrón Owner

Separa la inyección de dependencias de la UI pura para mejor testabilidad.

```kotlin
// Owner: Maneja inyección y coordina
@Composable
fun LoginScreenOwner(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    LoginScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

// Content: UI pura, sin dependencias externas
@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onAction: (LoginUiAction) -> Unit
) {
    // Composables puros
}

// Preview: Usa Content con datos mock
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        state = LoginUiState(email = "test@test.com"),
        onAction = {}
    )
}
```

## Result Wrapper

Manejo uniforme de operaciones que pueden fallar.

```kotlin
// En UseCase
suspend fun login(email: String, password: String): Result<User> {
    return suspendResultOf {
        repository.login(email, password)
    }
}

// En ViewModel
viewModelScope.launch {
    loginUseCase(email, password)
        .onSuccess { user -> 
            navigationController.navigateToMain()
        }
        .onError { error ->
            _uiState.update { it.copy(error = error.message) }
        }
}
```

## 🔐 Flujo de Autenticación

### Login Flow

```
┌────────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────────┐
│LoginScreen │───▶│LoginViewModel│───▶│ LoginUseCase│───▶│AuthRepository│
└────────────┘    └──────────────┘    └─────────────┘    └──────┬───────┘
                                                                 │
                         ┌───────────────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────┐
            │ AuthRemoteDataSource   │
            │   POST /api/v1/token   │
            └───────────┬────────────┘
                        │
          ┌─────────────┴─────────────┐
          │                           │
          ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│  TokenManager   │         │    UserDao      │
│ (SharedPrefs)   │         │    (Room)       │
│ save JWT tokens │         │ save UserEntity │
└─────────────────┘         └─────────────────┘
          │                           │
          └─────────────┬─────────────┘
                        │
                        ▼
            ┌────────────────────────┐
            │    Navigate to Home    │
            │ navigateToMain()       │
            └────────────────────────┘
```

### Componentes de Auth

| Componente | Responsabilidad |
|------------|-----------------|
| `TokenManager` | Guarda/lee JWT en SharedPreferences |
| `GoodLifeInterceptor` | Añade `Authorization: Bearer` a requests |
| `AuthRemoteDataSource` | Llamadas API de login/register/refresh |
| `AuthRepositoryImpl` | Orquesta DataSource + TokenManager + UserDao |
| `LoginUseCase` | Valida campos + ejecuta login |
| `GetCurrentUserUseCase` | Obtiene usuario de Room |
| `LogoutUseCase` | Limpia tokens + Room |

## 📊 Mappers (Extension Functions)

Conversión entre capas usando extension functions:

```kotlin
// DTO → Domain
fun AuthResponse.toAuthToken(): AuthToken

// DTO → Domain
fun RegisterResponse.toUser(): User

// Entity ↔ Domain
fun UserEntity.toDomain(): User
fun User.fromDomain(): UserEntity
```

## 🎯 @Stable Annotation

Todos los UI states usan `@Stable` para optimizar recomposición:

```kotlin
@Stable
sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Content(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    ) : LoginUiState
    data object Success : LoginUiState
}
```

**Beneficios:**
- Compose sabe que el objeto es estable
- Evita recomposiciones innecesarias
- Mejor performance en listas y estados complejos

## Dependencias

| Librería | Versión | Propósito |
|----------|---------|-----------|
| Koin | 4.0.0 | Inyección de dependencias |
| Navigation Compose | 2.8.5 | Navegación |
| Kotlinx Serialization | 1.7.3 | Type-safe routes + JSON |
| Kotlinx DateTime | 0.7.1 | Fechas KMP-ready |
| Lifecycle Runtime | 2.8.7 | repeatOnLifecycle |
| Material 3 | (BOM) | UI Components |
| Retrofit | 2.11.0 | HTTP Client |
| OkHttp | 4.12.0 | HTTP + Logging Interceptor |
| Room | 2.7.0-alpha03 | Local Database |

## Preparación KMP

El código está diseñado para migrar a Kotlin Multiplatform:

| Componente | KMP-ready | Notas |
|------------|-----------|-------|
| `Result.kt` | ✅ | Kotlin puro |
| `DispatcherProvider` | ✅ | Interface abstracta |
| `core/datetime/*` | ✅ | kotlinx-datetime (KMP nativo) |
| `domain/model/*` | ✅ | Sin dependencias Android |
| `domain/repository/*` | ✅ | Interfaces puras |
| `domain/usecase/*` | ✅ | Kotlin puro |
| Navigation | ⚠️ | Requiere expect/actual |
| Room | ❌ | Reemplazar con SQLDelight |
| Retrofit | ❌ | Reemplazar con Ktor |
| SharedPreferences | ❌ | Reemplazar con DataStore/Settings |

### Estrategia de Migración KMP

```
shared/
├── commonMain/
│   └── domain/      # Mover tal cual
│   └── core/result/ # Mover tal cual
│
├── androidMain/
│   └── data/        # Room + Retrofit
│
└── iosMain/
    └── data/        # SQLDelight + Ktor
```

## 🎨 Design System

### Patrón Simple (MaterialTheme)

Todo sale de `MaterialTheme`:

```kotlin
// Shapes
shape = MaterialTheme.shapes.extraLarge  // Botones pill
shape = MaterialTheme.shapes.medium      // Cards
shape = MaterialTheme.shapes.small       // Chips

// Colors
color = MaterialTheme.colorScheme.primary
color = MaterialTheme.colorScheme.onSurface
color = MaterialTheme.colorScheme.surface

// Typography
style = MaterialTheme.typography.bodyLarge
style = MaterialTheme.typography.titleMedium
style = MaterialTheme.typography.labelMedium

// Dimensiones: valores directos
.padding(16.dp)
.size(24.dp)
.height(56.dp)
```

### Escala de Shapes

```kotlin
val GoodLifeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Badges
    small = RoundedCornerShape(8.dp),        // Chips
    medium = RoundedCornerShape(12.dp),      // Cards
    large = RoundedCornerShape(16.dp),       // Bottom sheets
    extraLarge = RoundedCornerShape(50.dp)   // Botones pill
)
```

### Colores Custom (fuera de ColorScheme)

```kotlin
// Gradientes para botones
val AuthButtonColors = listOf(LightGreen, GreenSelected)
val ButtonColorsDisabled = listOf(ButtonDisabledBg, Color(0xFF9E9E9E))

// Colores específicos
val GreenSelected = Color(0xFF00D26B)  // Items seleccionados
val DividerColor = Color(0xFFE0E0E0)   // Divisores
```

## 📅 Date Handling (KMP-Ready)

### Librería: kotlinx-datetime

```kotlin
// Dependencia
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
```

### Clock Inyectado (Testabilidad)

```kotlin
// Producción
single<Clock> { Clock.System }

// Tests
val fakeClock = object : Clock {
    override fun now() = Instant.parse("2026-01-29T12:00:00Z")
}

// Uso en componentes
@Composable
fun DateHeader(
    date: LocalDate,
    clock: Clock,  // Inyectado
    language: AppLanguage
)
```

### Multi-Idioma

```kotlin
sealed interface AppLanguage {
    val monthNames: MonthNames
    val dayNamesShort: DayOfWeekNames
    val formats: DateFormats
    val relativeTexts: RelativeDateTexts
    
    data object Spanish : AppLanguage { /* ... */ }
    data object English : AppLanguage { /* ... */ }
    data object Portuguese : AppLanguage { /* ... */ }
}
```

### Extension Functions

```kotlin
fun LocalDate.isToday(clock: Clock): Boolean
fun LocalDate.isYesterday(clock: Clock): Boolean
fun LocalDate.isTomorrow(clock: Clock): Boolean
fun LocalDate.toFriendlyString(clock: Clock, language: AppLanguage): String
fun LocalDate.toModalHeaderString(clock: Clock, language: AppLanguage): String
```
