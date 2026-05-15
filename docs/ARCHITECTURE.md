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

## Estructura de Paquetes (Actualizada 2026-02-03)

```
com.agusstkd.goodlife/
├── core/                          # Utilidades compartidas (KMP-ready)
│   ├── biometric/                 # Biometría
│   │   ├── BiometricResult.kt
│   │   └── BiometricAvailability.kt
│   ├── datetime/                  # Manejo de fechas (KMP-ready)
│   │   ├── DateProvider.kt        # Interface abstraída (SPEC-004)
│   │   ├── RealDateProvider.kt    # Implementación Android (timezone-safe)
│   │   ├── LocalDateExtensions.kt # (vaciado — formateo migrado a ViewModels)
│   │   └── language/
│   │       ├── AppLanguage.kt     # Sealed interface: Spanish, English, Portuguese
│   │       ├── UiTexts.kt         # AuthTexts, HomeTexts, DailyTexts, etc.
│   │       └── DateFormats.kt     # DateFormats + RelativeDateTexts + DailyItemLabels
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
│   ├── local/                     # Room database (v2)
│   │   ├── dao/
│   │   │   ├── UserDao.kt
│   │   │   └── DailyDao.kt        # cache SWR daily logs
│   │   ├── database/
│   │   │   └── GoodLifeDatabase.kt
│   │   └── entity/
│   │       ├── UserEntity.kt
│   │       ├── DailyLogEntity.kt
│   │       └── DailyItemEntity.kt
│   │
│   ├── remote/                    # Retrofit (servicios por feature bajo api/)
│   │   ├── api/
│   │   │   ├── auth/ …
│   │   │   ├── daily/ …
│   │   │   ├── task/ …
│   │   │   ├── habit/ …
│   │   │   ├── training/ …
│   │   │   └── nutrition/ …
│   │   ├── datasource/remote/
│   │   └── dto/ (request / response + mappers .toDomain())
│   │
│   └── repository/                # *RepositoryImpl (8 interfaces en domain)
│       ├── AuthRepositoryImpl.kt
│       ├── DailyRepositoryImpl.kt
│       └── …
│
├── di/                            # Inyección de dependencias (Koin)
│   ├── AppModule.kt               # coreModule (Dispatcher, Navigation, DateProvider, AppLanguage)
│   ├── NetworkModule.kt           # OkHttp, Retrofit, ApiServices, TokenManager
│   ├── DatabaseModule.kt          # Room, DAOs
│   ├── BiometricModule.kt         # BiometricAuthenticator, SecureCredentialsStorage
│   ├── AuthModule.kt              # Auth DataSource / Repository / UseCases / ViewModels auth
│   ├── DailyModule.kt
│   ├── TaskModule.kt
│   ├── HabitModule.kt
│   ├── RoutineModule.kt
│   └── NutritionModule.kt
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
│           ├── ValidatePasswordUseCase.kt
│           ├── ValidateUserNameUseCase.kt
│           ├── ValidateFullNameUseCase.kt
│           └── ValidatePasswordMatchUseCase.kt
│
├── presentation/                  # UI y ViewModels
│   ├── components/                # Componentes reutilizables (todos con Params)
│   │   ├── common/
│   │   │   ├── ButtonComponent.kt           # ButtonParams
│   │   │   ├── TextFieldComponent.kt        # TextFieldParams (+ passwordToggle texts)
│   │   │   ├── CheckboxComponent.kt
│   │   │   ├── TitleComponent.kt            # text: String (no stringResource)
│   │   │   └── BackgroundGradientComponent.kt
│   │   ├── bottom/                          # Bottom Navigation
│   │   │   ├── BottomNavigationComponent.kt # BottomNavigationParams (+ fabContentDescription)
│   │   │   └── model/
│   │   │       ├── BottomMenuOption.kt
│   │   │       └── BottomNavItemModel.kt
│   │   ├── header/                          # Headers dinámicos por tab
│   │   │   ├── DateHeaderComponent.kt       # DateHeaderParams (+ accessibility labels)
│   │   │   ├── TabRowHeaderComponent.kt     # WorkoutTabHeaderParams (+ filterContentDescription)
│   │   │   └── CalendarDayIcon.kt
│   │   ├── modal/                           # Modal de acciones
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
│   │   │   └── GoodLifeNavHost.kt
│   │   └── route/
│   │       ├── AppRoute.kt
│   │       ├── TabRoute.kt
│   │       ├── TabNavGraph.kt
│   │       ├── NavigationExtensions.kt
│   │       └── AppGraph.kt
│   │
│   ├── screen/                    # Pantallas (Owner / Screen / ViewModel)
│   │   ├── splash/
│   │   │   ├── SplashScreen.kt
│   │   │   ├── SplashScreenOwner.kt
│   │   │   ├── SplashViewModel.kt
│   │   │   └── model/
│   │   │       ├── SplashUiState.kt        # @Stable
│   │   │       └── SplashUiEvent.kt        # @Stable
│   │   │
│   │   ├── login/
│   │   │   ├── LoginScreen.kt              # texts: AuthScreenTexts
│   │   │   ├── LoginScreenOwner.kt         # koinViewModel + biometric side effects
│   │   │   ├── LoginViewModel.kt           # screenTexts: AuthScreenTexts
│   │   │   └── model/
│   │   │       ├── LoginUiState.kt         # @Stable
│   │   │       └── LoginUiAction.kt        # @Stable
│   │   │
│   │   ├── register/
│   │   │   ├── RegisterScreen.kt           # texts: AuthScreenTexts
│   │   │   ├── RegisterScreenOwner.kt
│   │   │   ├── RegisterViewModel.kt        # screenTexts: AuthScreenTexts
│   │   │   └── model/
│   │   │       ├── RegisterUiState.kt      # @Stable
│   │   │       └── RegisterUiAction.kt     # @Stable
│   │   │
│   │   ├── main/
│   │   │   ├── MainScaffoldScreen.kt      # uiState contiene fabContentDescription
│   │   │   ├── MainScaffoldScreenOwner.kt
│   │   │   ├── MainScaffoldViewModel.kt   # fabContentDescription en UiState
│   │   │   └── model/
│   │   │       ├── MainScaffoldUiState.kt # @Stable + fabContentDescription: String
│   │   │       └── MainScaffoldUiAction.kt
│   │   │
│   │   ├── tabs/
│   │   │   └── daily/
│   │   │       ├── DailyScreen.kt          # dailyTexts + dateHeaderParams
│   │   │       ├── DailyScreenOwner.kt     # construye DateHeaderParams
│   │   │       ├── DailyTabViewModel.kt    # dailyTexts + accessibilityTexts
│   │   │       └── model/
│   │   │           ├── DailyUiState.kt     # @Stable + strings formateados
│   │   │           └── DailyUiAction.kt
│   │   │
│   │   └── add/                            # flujos full-screen desde AppRoute
│   │       ├── task/    (CreateTask*)
│   │       ├── habit/   (CreateHabit*)
│   │       ├── routine/ (CreateRoutine*)
│   │       └── mealplan/(CreateMealPlan*)
│   │
│   └── theme/                     # Material Theme
│       ├── Color.kt               # Paleta completa light/dark + brushes + DailyItemStyle
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
                                             │ (LaunchedEffect + collect)
                                             ▼
                                 ┌─────────────────────────┐
                                 │     GoodLifeNavHost     │
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
| `GoodLifeNavHost` | Observa el flow y ejecuta navegación |
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

La implementación usa `MutableSharedFlow(replay = 1)` para el arranque (evita condición de carrera con Splash). Ver `ComposeNavigationControllerImpl`.

Separa la inyección de dependencias de la UI pura. El Owner es el **único punto** donde se usa `koinViewModel()`. Los textos localizados fluyen del ViewModel al Screen como parámetro.

```kotlin
// Owner: Maneja inyección, textos y side-effects platform-specific
@Composable
fun LoginScreenOwner(
    viewModel: LoginViewModel = koinViewModel()   // único koinViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val texts = viewModel.screenTexts   // AuthScreenTexts (del ViewModel)

    // Side effects platform-specific (biometría, etc.)
    
    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        texts = texts                    // textos como parámetro
    )
}

// Screen: UI pura — NO inyecta nada, NO conoce Koin
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    texts: AuthScreenTexts                // wrapper de textos
) {
    // Usa texts.auth.login, texts.accessibility.hide, etc.
}

// Preview: Usa Screen con datos mock
@Preview
@Composable
fun LoginScreenPreview() {
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

### Reglas del Patrón Owner

| Regla | Descripción |
|-------|-------------|
| `koinViewModel()` solo en Owner | Screens y Components NO inyectan nada |
| Textos via ViewModel | `viewModel.screenTexts`, `viewModel.homeTexts`, etc. |
| Textos como parámetro | El Screen recibe textos por parameter, NO por `koinInject()` |
| Textos dentro de Params | Components reciben textos encapsulados en sus Params |
| Platform side-effects en Owner | BiometricPrompt, Context, Activity — solo aquí |

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
| `DateProvider` | ✅ | Interface + RealDateProvider (kotlinx-datetime) |
| `AppLanguage` + `UiTexts` | ✅ | Pure Kotlin, sin Android Context |
| `core/datetime/*` | ✅ | kotlinx-datetime (KMP nativo) |
| `domain/model/*` | ✅ | Sin dependencias Android |
| `domain/repository/*` | ✅ | Interfaces puras |
| `domain/usecase/*` | ✅ | Kotlin puro (usa ValidationTexts/ErrorTexts) |
| ViewModels | ✅ | No usan Context, textos via AppLanguage |
| Screens/Components | ✅ | Sin `stringResource()`, sin `koinInject()` |
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

## 📅 Date Handling — DateProvider Pattern (SPEC-004)

### Librería: kotlinx-datetime

```kotlin
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
```

### DateProvider (Abstracción KMP-ready)

```kotlin
// Interface (core/datetime/)
interface DateProvider {
    fun today(): LocalDate
    fun now(): Instant
    fun yesterday(): LocalDate = today().minus(1, DateTimeUnit.DAY)
    fun tomorrow(): LocalDate = today().plus(1, DateTimeUnit.DAY)
}

// Producción — timezone-safe
single<DateProvider> { RealDateProvider() }

// Tests — fecha fija, determinista
val fakeProvider = FakeDateProvider(LocalDate(2025, 12, 25))
```

### Formateo en ViewModel (NO en UI)

```kotlin
// ViewModel formatea todo internamente
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage
) {
    private fun buildUiState(date: LocalDate): DailyUiState {
        val headerText = when (date) {
            dateProvider.today() -> language.relativeTexts.today
            dateProvider.yesterday() -> language.relativeTexts.yesterday
            else -> date.format(language.formats.dayNameAndDate)
        }
        return DailyUiState(dayNumber = date.dayOfMonth, headerText = headerText, ...)
    }
}

// UI solo renderiza strings
DateHeaderComponent(params = DateHeaderParams(dayNumber = 3, headerText = "Hoy", ...))
```

## 🌐 Localización — AppLanguage System (SPEC-007)

### Arquitectura

```
UiTexts.kt → data classes de texto (AuthTexts, HomeTexts, DailyTexts, ...)
    ↓
AppLanguage.kt → sealed interface + 3 idiomas (Spanish, English, Portuguese)
    ↓
AppModule.kt → single<AppLanguage> { detecta locale automáticamente }
    ↓
ViewModel → constructor(language: AppLanguage) → expone screenTexts
    ↓
Owner → viewModel.screenTexts → pasa a Screen como parámetro
    ↓
Screen → textos via parameter, pasa a Components dentro de Params
```

### Grupos de textos

| Grupo | Uso |
|-------|-----|
| `AuthTexts` | Login, Register, biometric prompt |
| `HomeTexts` | Home screen |
| `DailyTexts` | Daily tab (progreso, errores) |
| `ValidationTexts` | UseCases de validación |
| `ErrorTexts` | Mensajes de error genéricos |
| `MainScaffoldTexts` | BottomNav, modal, tabs |
| `AccessibilityTexts` | Content descriptions (hide/show, calendar, nav) |
| `DailyItemLabels` | Etiquetas de tipo (Hábito, Tarea, ...) |

### Screen Wrappers

```kotlin
// Agrupa textos de un Screen en un solo objeto
data class AuthScreenTexts(
    val auth: AuthTexts,
    val accessibility: AccessibilityTexts
)

// ViewModel expone un solo accessor
class LoginViewModel(private val language: AppLanguage) {
    val screenTexts: AuthScreenTexts
        get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)
}
```

### Textos dentro de Params

```kotlin
// Textos de accessibility van DENTRO del Params del componente
TextFieldComponent(
    params = TextFieldParams(
        value = password,
        placeholder = auth.password,
        passwordToggleHide = accessibility.hide,     // dentro de Params
        passwordToggleShow = accessibility.show      // dentro de Params
    )
)
```

### Detección automática de locale

```kotlin
// di/AppModule.kt
single<AppLanguage> {
    when (java.util.Locale.getDefault().language) {
        "es" -> AppLanguage.Spanish
        "pt" -> AppLanguage.Portuguese
        else -> AppLanguage.English
    }
}
```
