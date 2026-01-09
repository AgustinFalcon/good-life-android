# Arquitectura GoodLife Android

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
│   │   ├── User.kt
│   │   ├── AuthToken.kt
│   │   └── ValidationResult.kt
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
│   │   ├── ButtonComponent.kt
│   │   └── InputComponent.kt
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
│       ├── Color.kt
│       ├── Font.kt
│       ├── Theme.kt
│       └── Type.kt
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
