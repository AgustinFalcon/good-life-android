# Guía de Arquitectura - GoodLife Android

> **Documento de referencia obligatorio** para todo desarrollo en el proyecto.
> Última actualización: 2026-02-03

---

## Tabla de Contenidos

1. [Visión General](#1-visión-general)
2. [Principios Fundamentales](#2-principios-fundamentales)
3. [Estructura de Capas](#3-estructura-de-capas)
4. [Patrones de Diseño](#4-patrones-de-diseño)
5. [Sistema de Navegación](#5-sistema-de-navegación)
6. [Convenciones de Código](#6-convenciones-de-código)
7. [Reglas KMP-Ready](#7-reglas-kmp-ready)
8. [Inyección de Dependencias](#8-inyección-de-dependencias)
9. [Sistema de Localización (AppLanguage)](#9-sistema-de-localización-applanguage)
10. [Checklist de Implementación](#10-checklist-de-implementación)

---

## 1. Visión General

GoodLife Android utiliza **Clean Architecture** con **MVVM** y está diseñada para ser **Kotlin Multiplatform Ready (KMP-Ready)**.

### Objetivo Arquitectónico

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         KOTLIN MULTIPLATFORM READY                       │
│                                                                          │
│   El código debe poder migrar a KMP con mínimos cambios:                │
│   - Domain layer: 100% portable (Kotlin puro)                           │
│   - Core utilities: 100% portable (Result, Dispatchers interfaces)      │
│   - Presentation: ViewModels portables, UI específica por plataforma    │
│   - Data: Interfaces en Domain, implementaciones platform-specific      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION                                │
│  ┌─────────┐  ┌─────────┐  ┌──────────┐  ┌─────────────────────┐│
│  │ Screens │  │ Owners  │  │ViewModels│  │   Navigation        ││
│  │(Content)│  │(Inject) │  │          │  │   (SharedFlow)      ││
│  │@Stable  │  │         │  │          │  │                     ││
│  └─────────┘  └─────────┘  └──────────┘  └─────────────────────┘│
├─────────────────────────────────────────────────────────────────┤
│                        DOMAIN                                    │
│  ┌─────────┐  ┌─────────┐  ┌─────────────────────────────────┐  │
│  │ UseCases│  │ Models  │  │    Repository Interfaces        │  │
│  │         │  │(Kotlin) │  │                                 │  │
│  └─────────┘  └─────────┘  └─────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                         DATA                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────────┐    │
│  │ Repositories│  │ DataSources │  │ Entities/DTOs         │    │
│  │  (Impl)     │  │Remote/Local │  │ + Extension Mappers   │    │
│  └─────────────┘  └─────────────┘  └───────────────────────┘    │
├─────────────────────────────────────────────────────────────────┤
│                         CORE                                     │
│  ┌─────────┐  ┌─────────────────┐  ┌───────────────────────┐    │
│  │ Result  │  │DispatcherProvider│ │ Network/Storage       │    │
│  └─────────┘  └─────────────────┘  └───────────────────────┘    │
├─────────────────────────────────────────────────────────────────┤
│                       PLATFORM                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  Implementaciones específicas de Android                     ││
│  │  (AndroidBiometricAuthenticator, AndroidSecureStorage, etc.) ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Principios Fundamentales

### 2.1 SOLID

| Principio | Aplicación en GoodLife |
|-----------|------------------------|
| **S**ingle Responsibility | Cada clase tiene UNA razón para cambiar. ViewModel maneja estado, UseCase maneja lógica, Repository maneja datos. |
| **O**pen/Closed | Extender via interfaces, no modificar clases existentes. Nuevos UseCases, nuevos Repositories. |
| **L**iskov Substitution | Cualquier implementación de `AuthRepository` debe funcionar donde se espera `AuthRepository`. |
| **I**nterface Segregation | Interfaces específicas: `BiometricAuthenticator`, `SecureCredentialsStorage`, no una interfaz gigante. |
| **D**ependency Inversion | Domain define interfaces, Data/Platform implementan. ViewModels dependen de abstracciones. |

### 2.2 Clean Architecture - Regla de Dependencia

```
REGLA DE ORO: Las dependencias SIEMPRE apuntan hacia adentro.

┌─────────────────────────────────────────────────────────┐
│                     PRESENTATION                         │
│                          │                               │
│                          ▼                               │
│  ┌───────────────────────────────────────────────────┐  │
│  │                     DOMAIN                         │  │
│  │                        │                           │  │
│  │                        ▼                           │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │                   DATA                       │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘

❌ PROHIBIDO: Domain importa de Data o Presentation
❌ PROHIBIDO: UseCase importa Retrofit, Room, Compose
✅ CORRECTO: UseCase solo usa interfaces de Domain + modelos de Domain
```

### 2.3 Separation of Concerns

| Capa | Responsabilidad | NO debe hacer |
|------|-----------------|---------------|
| **Screen/Content** | Renderizar UI | Lógica de negocio, llamadas API |
| **Owner** | Inyectar ViewModel, manejar side effects platform-specific | Lógica de UI |
| **ViewModel** | Mantener estado UI, procesar acciones | Llamadas HTTP directas |
| **UseCase** | Orquestar lógica de negocio | Conocer cómo se persisten datos |
| **Repository** | Orquestar fuentes de datos | Conocer UI o ViewModels |
| **DataSource** | Llamadas HTTP o queries DB | Lógica de negocio |

---

## 3. Estructura de Capas

### 3.1 Domain Layer (Kotlin Puro - 100% KMP-Ready)

```
domain/
├── model/                    # Modelos de negocio
│   ├── User.kt              # data class User(id, email, name)
│   ├── AuthToken.kt         # data class AuthToken(access, refresh)
│   └── ValidationResult.kt  # data class ValidationResult(isValid, error)
│
├── repository/              # INTERFACES de repositorios
│   └── AuthRepository.kt    # interface AuthRepository { suspend fun login() }
│
├── usecase/                 # Casos de uso
│   ├── login/
│   │   └── LoginUseCase.kt
│   ├── register/
│   │   └── RegisterUseCase.kt
│   └── validation/
│       ├── ValidateEmailUseCase.kt
│       └── ValidatePasswordUseCase.kt
│
├── biometric/               # INTERFACES de biometría (KMP-Ready)
│   └── BiometricAuthenticator.kt
│
├── storage/                 # INTERFACES de almacenamiento seguro
│   └── SecureCredentialsStorage.kt
│
└── exception/               # Excepciones de dominio
    └── AuthExceptions.kt
```

**Reglas del Domain:**
- ❌ NO importar nada de Android (`android.*`)
- ❌ NO importar Retrofit, Room, Compose
- ❌ NO importar clases de `data/` o `presentation/`
- ✅ Solo Kotlin stdlib + kotlinx (coroutines, serialization)

### 3.2 Data Layer

```
data/
├── remote/                  # Fuentes remotas (API)
│   ├── api/
│   │   └── GoodLifeApiService.kt    # interface Retrofit
│   ├── datasource/
│   │   └── AuthRemoteDataSource.kt  # Llamadas HTTP
│   └── dto/
│       ├── request/
│       │   └── RegisterRequest.kt
│       └── response/
│           ├── AuthResponse.kt      # + extension fun toDomain()
│           └── RegisterResponse.kt  # + extension fun toDomain()
│
├── local/                   # Fuentes locales (Room)
│   ├── dao/
│   │   └── UserDao.kt
│   ├── database/
│   │   └── GoodLifeDatabase.kt
│   └── entity/
│       └── UserEntity.kt            # + extension fun toDomain()/toEntity()
│
└── repository/              # IMPLEMENTACIONES
    └── AuthRepositoryImpl.kt        # implements AuthRepository
```

**Reglas del Data:**
- ✅ Implementa interfaces del Domain
- ✅ Convierte DTOs/Entities → Domain models via extension functions
- ❌ NO exponer DTOs o Entities fuera de esta capa
- ❌ NO importar de Presentation

### 3.3 Presentation Layer

```
presentation/
├── components/              # Componentes UI reutilizables
│   ├── common/
│   │   ├── ButtonComponent.kt       # Params + Component pattern
│   │   ├── TextFieldComponent.kt
│   │   └── CheckboxComponent.kt
│   ├── bottom/
│   │   ├── BottomMenuOption.kt      # enum
│   │   ├── BottomNavItemModel.kt    # data class + @Stable
│   │   └── BottomNavigationComponent.kt
│   ├── header/
│   │   └── DateHeaderComponent.kt
│   └── modal/
│       └── AddActionModalComponent.kt
│
├── navigation/              # Sistema de navegación reactivo
│   ├── core/
│   │   ├── NavigationAction.kt           # sealed class
│   │   ├── ComposeNavigationController.kt # interface
│   │   └── ComposeNavigationControllerImpl.kt
│   ├── host/
│   │   └── GoodLifeNavHost.kt
│   └── route/
│       ├── AppRoute.kt                    # @Serializable rutas app
│       ├── TabRoute.kt                    # @Serializable rutas tabs
│       ├── NavigationExtensions.kt        # Extension functions
│       └── AppGraph.kt                    # NavGraphBuilder.addAppGraph()
│
├── screen/                  # Pantallas (MVVM)
│   ├── login/
│   │   ├── LoginScreen.kt           # UI pura (LoginContent)
│   │   ├── LoginScreenOwner.kt      # Inyección + side effects
│   │   ├── LoginViewModel.kt        # Estado + lógica
│   │   └── model/
│   │       ├── LoginUiState.kt      # @Stable sealed interface
│   │       └── LoginUiAction.kt     # @Stable sealed interface
│   │
│   └── [otras pantallas siguiendo mismo patrón]
│
└── theme/                   # Material Theme
    ├── Color.kt
    ├── Font.kt
    ├── Theme.kt
    └── Type.kt
```

### 3.4 Core Layer (Utilities - 100% KMP-Ready)

```
core/
├── result/
│   └── Result.kt           # sealed class Result<T> { Success, Error, Loading }
│
├── dispatcher/
│   └── DispatcherProvider.kt  # interface + AndroidDispatcherProvider
│
├── network/
│   ├── NetworkConstants.kt
│   ├── HttpCode.kt
│   ├── ApiException.kt
│   └── GoodLifeInterceptor.kt
│
├── storage/
│   └── TokenManager.kt
│
├── biometric/              # Modelos de biometría (shared)
│   ├── BiometricAvailability.kt
│   └── BiometricResult.kt
│
└── util/
    └── JwtUtils.kt
```

### 3.5 Platform Layer (Android-Specific)

```
platform/
├── biometric/
│   └── AndroidBiometricAuthenticator.kt  # implements BiometricAuthenticator
│
└── storage/
    └── AndroidSecureCredentialsStorage.kt # implements SecureCredentialsStorage
```

**¿Por qué separar Platform de Data?**

Platform contiene implementaciones que usan APIs específicas de Android que NO tienen equivalente directo en otras plataformas (BiometricPrompt, EncryptedSharedPreferences). En KMP, cada plataforma tendría su propia implementación en `platform/`.

---

## 4. Patrones de Diseño

### 4.1 Patrón Owner (Screen Composition) — Actualizado SPEC-007

```kotlin
// ═══════════════════════════════════════════════════════════════════
// PATRÓN: Owner separa inyección de UI pura
// REGLA: Owner es el ÚNICO lugar con koinViewModel()
// REGLA: Textos localizados fluyen del ViewModel via parámetro
// ═══════════════════════════════════════════════════════════════════

/**
 * OWNER: Maneja inyección, textos y side-effects platform-specific
 */
@Composable
fun LoginScreenOwner(
    viewModel: LoginViewModel = koinViewModel()  // único koinViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val texts = viewModel.screenTexts  // AuthScreenTexts del ViewModel
    
    // Side effects que requieren Context/Activity (biometría, etc.)
    
    // Delegar a Screen puro con textos como parámetro
    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        texts = texts                   // ← textos como parámetro
    )
}

/**
 * SCREEN: UI pura, sin dependencias externas
 * - Recibe estado inmutable + textos localizados
 * - Emite acciones via callbacks
 * - NUNCA usa koinInject(), stringResource(), R.string
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    texts: AuthScreenTexts              // ← wrapper de textos
) {
    val auth = texts.auth
    val accessibility = texts.accessibility
    
    // Pasa textos a Components dentro de sus Params
    TextFieldComponent(
        params = TextFieldParams(
            value = uiState.password,
            placeholder = auth.password,
            passwordToggleHide = accessibility.hide,
            passwordToggleShow = accessibility.show
        ),
        onValueChange = { onAction(LoginUiAction.OnPasswordChange(it)) }
    )
}

/**
 * PREVIEW: Usa Screen con datos mock + textos reales
 */
@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginUiState.Content(email = "test@test.com"),
        onAction = {},
        texts = AuthScreenTexts(
            auth = AppLanguage.Spanish.authTexts,
            accessibility = AppLanguage.Spanish.accessibilityTexts
        )
    )
}
```

#### Reglas estrictas del patrón Owner

| # | Regla | Razón |
|---|-------|-------|
| 1 | **`koinViewModel()` solo en Owner** | Screens y Components son renderers puros |
| 2 | **`koinInject()` PROHIBIDO en Screens/Components** | Acopla UI al contenedor DI |
| 3 | **Textos del ViewModel, no de Koin** | `viewModel.screenTexts`, no `koinInject<AppLanguage>()` |
| 4 | **Textos como parámetro al Screen** | Screen recibe wrapper `AuthScreenTexts`, `HomeTexts`, etc. |
| 5 | **Textos dentro de Params para Components** | `TextFieldParams(passwordToggleHide = ...)` |
| 6 | **Único `koinInject` aceptable en Owner** | `BiometricAuthenticator` (requiere Activity) |

### 4.2 Patrón Component (UI Reusable)

```kotlin
// ═══════════════════════════════════════════════════════════════════
// PATRÓN: Params + Component para componentes reutilizables
// ═══════════════════════════════════════════════════════════════════

/**
 * PARAMS: Agrupa todos los parámetros de configuración
 */
data class ButtonParams(
    val text: String,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val variant: ButtonVariant = ButtonVariant.PRIMARY
)

/**
 * COMPONENT: Recibe Params + callbacks de eventos
 */
@Composable
fun ButtonComponent(
    params: ButtonParams,           // Configuración
    onClick: () -> Unit,            // Evento
    modifier: Modifier = Modifier   // Modificador
) {
    // Implementación...
}

// ═══════════════════════════════════════════════════════════════════
// USO EN SCREEN:
// ═══════════════════════════════════════════════════════════════════

ButtonComponent(
    params = ButtonParams(
        text = "Iniciar Sesión",
        enabled = uiState.isFormValid,
        isLoading = uiState.isLoading
    ),
    onClick = { onAction(LoginUiAction.OnLoginClick) }
)
```

### 4.3 Patrón UiState + UiAction

```kotlin
// ═══════════════════════════════════════════════════════════════════
// PATRÓN: Estado inmutable + Acciones como eventos
// ═══════════════════════════════════════════════════════════════════

/**
 * UI STATE: Representa TODO el estado de la pantalla
 * - @Stable para optimizar recomposición
 * - sealed interface para estados mutuamente excluyentes
 */
@Stable
sealed interface LoginUiState {
    data object Loading : LoginUiState
    
    data class Content(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isEmailError: Boolean = false
    ) : LoginUiState
    
    data object Success : LoginUiState
}

/**
 * UI ACTION: Representa TODAS las interacciones del usuario
 * - Una acción por cada evento de UI
 * - Nombres descriptivos: On[Elemento][Evento]
 */
@Stable
sealed interface LoginUiAction {
    data class OnEmailChange(val value: String) : LoginUiAction
    data class OnPasswordChange(val value: String) : LoginUiAction
    data object OnLoginClick : LoginUiAction
    data object OnRegisterClick : LoginUiAction
}

/**
 * VIEWMODEL: Procesa acciones y actualiza estado
 */
class LoginViewModel(...) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailChange -> updateEmail(action.value)
            is LoginUiAction.OnLoginClick -> performLogin()
            // ...
        }
    }
}
```

### 4.4 Patrón UseCase Result

```kotlin
// ═══════════════════════════════════════════════════════════════════
// PATRÓN: UseCase con Result propio para casos complejos
// ═══════════════════════════════════════════════════════════════════

class LoginUseCase(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
) {
    /**
     * Result específico del UseCase
     * Permite distinguir entre errores de validación vs errores de servidor
     */
    sealed interface LoginResult {
        data class Success(val user: User) : LoginResult
        data class ValidationError(
            val emailError: String?,
            val passwordError: String?
        ) : LoginResult
        data class Error(val message: String) : LoginResult
    }
    
    suspend operator fun invoke(email: String, password: String): LoginResult {
        // 1. Validar campos
        val emailResult = validateEmail(email)
        if (!emailResult.isValid) {
            return LoginResult.ValidationError(emailResult.errorMessage, null)
        }
        
        // 2. Ejecutar operación
        return when (val result = authRepository.login(email, password)) {
            is Result.Success -> LoginResult.Success(result.data)
            is Result.Error -> LoginResult.Error(result.message ?: "Error")
            is Result.Loading -> LoginResult.Error("Estado inesperado")
        }
    }
}
```

### 4.5 Patrón Mapper (Extension Functions)

```kotlin
// ═══════════════════════════════════════════════════════════════════
// PATRÓN: Extension functions para conversión entre capas
// ═══════════════════════════════════════════════════════════════════

// En AuthResponse.kt (data/remote/dto/response/)
fun AuthResponse.toDomain(): AuthToken {
    return AuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

// En UserEntity.kt (data/local/entity/)
fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    name = name
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    name = name
)

// ═══════════════════════════════════════════════════════════════════
// REGLA: El mapper vive JUNTO al DTO/Entity, no en archivo separado
// ═══════════════════════════════════════════════════════════════════
```

---

## 5. Sistema de Navegación

### 5.1 Arquitectura de Navegación Reactiva

```
┌──────────────┐     emit()      ┌─────────────────────────────┐
│  ViewModel   │ ───────────────▶│ ComposeNavigationController │
│              │                 │      (SharedFlow)           │
└──────────────┘                 └───────────┬─────────────────┘
                                             │
                                             │ collect()
                                             │ (repeatOnLifecycle)
                                             ▼
                                 ┌─────────────────────────────┐
                                 │      GoodLifeNavHost        │
                                 │                             │
                                 │  navController.navigate()   │
                                 └─────────────────────────────┘
```

### 5.2 ¿Por qué SharedFlow y NO StateFlow?

| Característica | StateFlow | SharedFlow |
|----------------|-----------|------------|
| Retiene último valor | ✅ Sí | ❌ No |
| Replay en rotación | ✅ Sí (problema!) | ❌ No |
| Uso ideal | UI State | One-shot events |

**Navegación = Evento one-shot** → SharedFlow evita re-navegación al rotar la pantalla.

### 5.3 Dos Niveles de Navegación

```kotlin
// ═══════════════════════════════════════════════════════════════════
// NIVEL 1: App Navigation (Login → Main)
// Usa: ComposeNavigationController + SharedFlow
// Controlado por: ViewModels de Login, Register, Splash
// ═══════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════
// NIVEL 2: Tab Navigation (Home ↔ Workouts ↔ Meals ↔ Settings)
// Usa: NavHostController interno (NO SharedFlow)
// Controlado por: MainScaffoldViewModel
// ═══════════════════════════════════════════════════════════════════

// ¿Cuándo usar cuál?
// - Navegación entre pantallas de AUTH (Splash, Login, Register, Main): SharedFlow
// - Navegación DENTRO de MainScaffold (tabs, detalles): NavController directo
```

### 5.4 Rutas Type-Safe

```kotlin
// Rutas de nivel App
@Serializable
sealed interface AppRoute {
    @Serializable data object Splash : AppRoute
    @Serializable data object Login : AppRoute
    @Serializable data object Register : AppRoute
    @Serializable data object Main : AppRoute
}

// Rutas de nivel Tab (dentro de Main)
@Serializable
sealed interface TabRoute {
    @Serializable data object Home : TabRoute
    @Serializable data class TaskDetail(val taskId: Long) : TabRoute
    @Serializable data object Workouts : TabRoute
    @Serializable data class WorkoutDetail(val workoutId: Long) : TabRoute
    // ...
}
```

---

## 6. Convenciones de Código

### 6.1 Nomenclatura

| Elemento | Convención | Ejemplo |
|----------|------------|---------|
| Screen Content | `[Feature]Content` | `LoginContent`, `HomeContent` |
| Screen Owner | `[Feature]ScreenOwner` | `LoginScreenOwner` |
| ViewModel | `[Feature]ViewModel` | `LoginViewModel` |
| UiState | `[Feature]UiState` | `LoginUiState` |
| UiAction | `[Feature]UiAction` | `LoginUiAction` |
| UseCase | `[Verbo][Sustantivo]UseCase` | `LoginUseCase`, `ValidateEmailUseCase` |
| Repository Interface | `[Domain]Repository` | `AuthRepository` |
| Repository Impl | `[Domain]RepositoryImpl` | `AuthRepositoryImpl` |
| Component | `[Nombre]Component` | `ButtonComponent`, `TextFieldComponent` |
| Component Params | `[Nombre]Params` | `ButtonParams` |
| DTO | `[Nombre]Request/Response` | `LoginRequest`, `AuthResponse` |
| Entity | `[Nombre]Entity` | `UserEntity` |
| Mapper | `[Clase].toDomain()` | `AuthResponse.toDomain()` |

### 6.2 Estructura de Archivos

```kotlin
// ═══════════════════════════════════════════════════════════════════
// ORDEN DENTRO DE UN ARCHIVO KOTLIN
// ═══════════════════════════════════════════════════════════════════

// 1. Package
package com.agusstkd.goodlife.presentation.screen.login

// 2. Imports (ordenados: Android, Compose, Kotlin, Proyecto)
import android.content.Context
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import com.agusstkd.goodlife.domain.usecase.LoginUseCase

// 3. Constantes de archivo (si las hay)
private const val TAG = "LoginViewModel"

// 4. Clases/Interfaces principales
class LoginViewModel(...) { }

// 5. Extension functions relacionadas
private fun Context.findActivity(): FragmentActivity? { }

// 6. Previews (al final)
@Preview
@Composable
private fun LoginScreenPreview() { }
```

### 6.3 Documentación

```kotlin
/**
 * Caso de uso para ejecutar el login.
 *
 * ## Responsabilidades:
 * 1. Validar los campos de entrada
 * 2. Llamar al repositorio para autenticar
 * 3. Retornar el resultado
 *
 * ## Flujo:
 * ```
 * LoginUseCase
 *     ├── ValidateEmailUseCase
 *     ├── ValidatePasswordUseCase
 *     └── AuthRepository.login()
 * ```
 *
 * @property validateEmail UseCase para validar email
 * @property authRepository Repositorio de autenticación
 */
class LoginUseCase(...)
```

---

## 7. Reglas KMP-Ready

### 7.1 Interfaces en Domain, Implementaciones en Platform

```kotlin
// ═══════════════════════════════════════════════════════════════════
// REGLA: Para funcionalidades platform-specific
// ═══════════════════════════════════════════════════════════════════

// 1. INTERFACE en domain/ (Kotlin puro)
interface BiometricAuthenticator {
    fun checkAvailability(): BiometricAvailability
    fun authenticate(config: BiometricPromptConfig, onResult: (BiometricResult) -> Unit)
}

// 2. IMPLEMENTACIÓN en platform/ (Android-specific)
class AndroidBiometricAuthenticator(
    private val context: Context
) : BiometricAuthenticator {
    // Usa BiometricManager, BiometricPrompt de Android
}

// 3. INYECCIÓN en di/
single<BiometricAuthenticator> { AndroidBiometricAuthenticator(androidContext()) }
```

### 7.2 DispatcherProvider Inyectado

```kotlin
// ═══════════════════════════════════════════════════════════════════
// REGLA: NUNCA usar Dispatchers.IO directamente en ViewModels
// ═══════════════════════════════════════════════════════════════════

// ❌ INCORRECTO
class LoginViewModel : ViewModel() {
    fun login() {
        viewModelScope.launch(Dispatchers.IO) {  // ❌ Hardcodeado
            // ...
        }
    }
}

// ✅ CORRECTO
class LoginViewModel(
    private val dispatcherProvider: DispatcherProvider  // ✅ Inyectado
) : ViewModel() {
    fun login() {
        viewModelScope.launch(dispatcherProvider.io) {  // ✅ Abstracto
            // ...
        }
    }
}
```

### 7.3 Result Wrapper

```kotlin
// ═══════════════════════════════════════════════════════════════════
// REGLA: Usar Result<T> para operaciones que pueden fallar
// ═══════════════════════════════════════════════════════════════════

// Result es Kotlin puro, portable a KMP
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

// Uso con extension functions
suspend inline fun <T> suspendResultOf(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
```

### 7.4 Modelos de Domain sin Android

```kotlin
// ═══════════════════════════════════════════════════════════════════
// REGLA: Domain models son Kotlin puro
// ═══════════════════════════════════════════════════════════════════

// ❌ INCORRECTO - Usa Android Parcelable
@Parcelize
data class User(
    val id: String,
    val name: String
) : Parcelable

// ✅ CORRECTO - Kotlin puro
data class User(
    val id: String,
    val name: String
)
// Si necesitas serialización, usa kotlinx.serialization (KMP-compatible)
```

---

## 8. Inyección de Dependencias

### 8.1 Módulos Koin

```kotlin
// ═══════════════════════════════════════════════════════════════════
// ESTRUCTURA DE MÓDULOS
// ═══════════════════════════════════════════════════════════════════

// appModule: Core, Navigation, Repositories, UseCases, ViewModels
// networkModule: OkHttp, Retrofit, ApiService
// databaseModule: Room, DAOs
// biometricModule: BiometricAuthenticator, SecureCredentialsStorage
```

### 8.2 Scopes

```kotlin
// ═══════════════════════════════════════════════════════════════════
// REGLAS DE SCOPE
// ═══════════════════════════════════════════════════════════════════

// SINGLETON: Estado global, costoso de crear, thread-safe requerido
single<ComposeNavigationController> { ComposeNavigationControllerImpl() }
single<AuthRepository> { AuthRepositoryImpl(...) }
single<GoodLifeDatabase> { ... }

// FACTORY: Sin estado interno, barato de crear
factory { ValidateEmailUseCase() }
factory { LoginUseCase(get(), get(), get()) }
factory { AuthRemoteDataSource(get()) }

// VIEWMODEL: Scope de Activity/Fragment
viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
```

---

## 9. Sistema de Localización (AppLanguage)

> Referencia completa: **[SPEC-007-app-language.md](./specs/SPEC-007-app-language.md)**

### 9.1 Principios

```
┌────────────────────────────────────────────────────────────────┐
│  REGLA DE ORO: Todo texto visible al usuario viene de          │
│  AppLanguage, NUNCA de stringResource(), R.string, ni hardcode │
└────────────────────────────────────────────────────────────────┘
```

### 9.2 Flujo de textos

```
AppLanguage (singleton Koin, detecta locale automáticamente)
    ↓ constructor injection
ViewModel (private val language: AppLanguage)
    ↓ expone propiedad
    val screenTexts: XxxScreenTexts
        get() = XxxScreenTexts(language.xxxTexts, language.accessibilityTexts)
    ↓ Owner lee
Owner (viewModel.screenTexts)
    ↓ pasa como parámetro
Screen (texts: XxxScreenTexts)
    ↓ extrae y pasa dentro de Params
Component (params: XxxParams con textos dentro)
```

### 9.3 Prohibiciones

```kotlin
// ❌ PROHIBIDO — koinInject en Screen
@Composable
fun LoginScreen(language: AppLanguage = koinInject()) { }

// ❌ PROHIBIDO — stringResource/R.string en cualquier lugar
Text(text = stringResource(R.string.login))

// ❌ PROHIBIDO — textos hardcodeados
Text(text = "Iniciar Sesión")

// ❌ PROHIBIDO — textos fuera de Params en Components
fun TextFieldComponent(params: TextFieldParams, hideLabel: String = "")

// ✅ CORRECTO — textos dentro de Params
fun TextFieldComponent(params: TextFieldParams(passwordToggleHide = "..."))
```

### 9.4 Agregar textos para una nueva vista

```
1. UiTexts.kt        → data class NuevoTexts(title: String, ...)
2. AppLanguage.kt     → val nuevoTexts: NuevoTexts
                        + implementar en Spanish, English, Portuguese
3. UiTexts.kt         → data class NuevoScreenTexts(nuevo: NuevoTexts, accessibility: AccessibilityTexts)
                        (solo si el Screen necesita >1 grupo)
4. NuevoViewModel.kt  → val screenTexts: NuevoScreenTexts
5. NuevoScreenOwner   → pasa viewModel.screenTexts al Screen
6. NuevoScreen        → recibe texts: NuevoScreenTexts como parámetro
```

### 9.5 Agregar un nuevo idioma

```kotlin
// 1. Nuevo data object en AppLanguage.kt
data object Italian : AppLanguage {
    override val authTexts = AuthTexts(login = "Accedi", ...)
    // el compilador marca error en CADA propiedad faltante
}

// 2. Nuevo case en AppModule.kt
"it" -> AppLanguage.Italian
```

---

## 10. Checklist de Implementación

### Al crear una nueva pantalla:

```
□ 1. Crear model/
    □ [Feature]UiState.kt (@Stable sealed interface)
    □ [Feature]UiAction.kt (@Stable sealed interface)

□ 2. Definir textos localizados
    □ data class [Feature]Texts(...) en UiTexts.kt
    □ Implementar en AppLanguage: Spanish, English, Portuguese
    □ (Opcional) data class [Feature]ScreenTexts wrapper si necesita >1 grupo

□ 3. Crear [Feature]ViewModel.kt
    □ Inyectar language: AppLanguage via constructor
    □ Exponer val screenTexts: [Feature]ScreenTexts (o textos directos)
    □ Exponer StateFlow<UiState>
    □ Implementar fun onAction(action: UiAction)
    □ Usar dispatcherProvider.io para operaciones async

□ 4. Crear [Feature]Screen.kt
    □ Recibe texts como parámetro (NUNCA koinInject)
    □ Pasa textos a Components dentro de sus Params
    □ Previews para cada estado con textos mock

□ 5. Crear [Feature]ScreenOwner.kt
    □ Inyectar ViewModel con koinViewModel()
    □ collectAsStateWithLifecycle()
    □ Pasar viewModel.screenTexts al Screen
    □ Manejar side effects platform-specific

□ 6. Actualizar DI
    □ viewModel { [Feature]ViewModel(language = get(), ...) }
    □ Registrar UseCases nuevos si los hay

□ 7. Actualizar Navegación
    □ Agregar ruta en AppRoute o TabRoute
    □ Agregar composable en AppGraph
    □ Agregar extension function si es navegación común
```

### Al crear un nuevo UseCase:

```
□ 1. Crear en domain/usecase/[feature]/
□ 2. Definir sealed interface Result si es complejo
□ 3. Inyectar dependencias via constructor (incluyendo language si necesita textos)
□ 4. Implementar operator fun invoke()
□ 5. Registrar como factory en AppModule
□ 6. Documentar responsabilidades y flujo
```

### Al crear un nuevo componente:

```
□ 1. Crear en presentation/components/[categoria]/
□ 2. Definir data class [Nombre]Params (incluir textos de accessibility aquí)
□ 3. Crear @Composable [Nombre]Component(params, callbacks, modifier)
□ 4. NUNCA usar koinInject(), stringResource() o R.string
□ 5. Agregar @Preview para cada variante
□ 6. Documentar uso con ejemplo
```

---

## Resumen de Reglas Críticas

| # | Regla | Razón |
|---|-------|-------|
| 1 | Domain NO importa de Data ni Presentation | Clean Architecture |
| 2 | Interfaces en Domain, implementaciones en Data/Platform | Dependency Inversion |
| 3 | DispatcherProvider inyectado, NUNCA Dispatchers.IO directo | Testing + KMP |
| 4 | @Stable en todos los UiState y UiAction | Optimización Compose |
| 5 | SharedFlow para navegación, StateFlow para UI state | Evitar re-emisión |
| 6 | Owner separa inyección de UI pura | Testabilidad |
| 7 | Mappers como extension functions junto al DTO/Entity | Cohesión |
| 8 | Result<T> para operaciones que pueden fallar | Manejo uniforme de errores |
| 9 | **`koinInject()` PROHIBIDO en Screens y Components** | KMP-ready + Composable puro (SPEC-007) |
| 10 | **`stringResource()` y `R.string` PROHIBIDO en capa compartida** | KMP-ready (SPEC-007) |
| 11 | **Textos localizados via AppLanguage → ViewModel → Screen** | Flujo unidireccional (SPEC-007) |
| 12 | **Textos de accessibility dentro de Params** | Firmas limpias (SPEC-007) |
| 13 | **DateProvider inyectado, NUNCA Clock.System directo** | Testing determinista (SPEC-004) |
| 14 | **Formateo de fechas en ViewModel, NUNCA en UI** | UI pura (SPEC-004) |

---

**Creado por:** Android Team  
**Versión:** 2.0  
**Última actualización:** 2026-02-03
