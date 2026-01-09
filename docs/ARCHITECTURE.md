# Arquitectura GoodLife Android

## Visión General

GoodLife utiliza **Clean Architecture** con **MVVM** y está diseñada para ser **KMP-ready** (Kotlin Multiplatform).

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                           │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────┐│
│  │ Screens │  │ Owners  │  │ViewModels│ │   Navigation    ││
│  └─────────┘  └─────────┘  └─────────┘  └─────────────────┘│
├─────────────────────────────────────────────────────────────┤
│                        DOMAIN                               │
│  ┌─────────┐  ┌─────────┐  ┌─────────────────────────────┐ │
│  │ UseCases│  │ Models  │  │    Repository Interfaces    │ │
│  └─────────┘  └─────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                         DATA                                │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────┐   │
│  │ Repositories│  │ DataSources │  │ Entities/DTOs     │   │
│  └─────────────┘  └─────────────┘  └───────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│                         CORE                                │
│  ┌─────────┐  ┌─────────────────┐  ┌───────────────────┐   │
│  │ Result  │  │DispatcherProvider│ │     Utils         │   │
│  └─────────┘  └─────────────────┘  └───────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Estructura de Paquetes

```
com.agusstkd.goodlife/
├── core/                          # Utilidades compartidas (KMP-ready)
│   ├── dispatcher/                # Abstracción de Dispatchers
│   │   └── DispatcherProvider.kt
│   └── result/                    # Wrapper para operaciones
│       └── Result.kt
│
├── data/                          # Capa de datos (futuro)
│   ├── local/                     # Room database
│   │   ├── dao/
│   │   ├── entity/
│   │   └── datasource/
│   ├── remote/                    # Retrofit API
│   │   ├── api/
│   │   ├── dto/
│   │   └── datasource/
│   └── repository/                # Implementaciones
│
├── di/                            # Inyección de dependencias
│   └── AppModule.kt               # Módulo principal de Koin
│
├── domain/                        # Lógica de negocio (futuro)
│   ├── model/                     # Modelos de dominio
│   ├── repository/                # Interfaces de repositorio
│   └── usecase/                   # Casos de uso
│
├── presentation/                  # UI y ViewModels
│   ├── navigation/                # Sistema de navegación
│   │   ├── core/                  # Core del sistema
│   │   │   ├── NavigationAction.kt
│   │   │   ├── ComposeNavigationController.kt
│   │   │   └── ComposeNavigationControllerImpl.kt
│   │   ├── host/                  # NavHost wrapper
│   │   │   └── SmartNavHost.kt
│   │   └── route/                 # Rutas y grafos
│   │       ├── AppRoute.kt
│   │       ├── TabRoute.kt
│   │       ├── NavigationExtensions.kt
│   │       └── AppGraph.kt
│   │
│   ├── screens/                   # Pantallas (futuro)
│   │   ├── splash/
│   │   ├── auth/
│   │   ├── home/
│   │   └── ...
│   │
│   └── theme/                     # Material Theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── GoodLifeApp.kt                 # Application class
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

## Dependencias

| Librería | Propósito |
|----------|-----------|
| Koin | Inyección de dependencias |
| Navigation Compose | Navegación |
| Kotlinx Serialization | Type-safe routes |
| Lifecycle Runtime | repeatOnLifecycle |
| Material 3 | UI Components |

## Preparación KMP

El código está diseñado para migrar a Kotlin Multiplatform:

| Componente | KMP-ready | Notas |
|------------|-----------|-------|
| `Result.kt` | ✅ | Kotlin puro |
| `DispatcherProvider` | ✅ | Interface abstracta |
| Domain layer | ✅ | Sin dependencias Android |
| Navigation | ⚠️ | Requiere expect/actual |
| Room/Retrofit | ❌ | Reemplazar con SQLDelight/Ktor |
