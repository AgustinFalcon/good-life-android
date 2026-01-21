# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

## [0.5.0] - 2026-01-20

### Added

#### 📝 Módulo de Registro Completo

- **Pantalla de Registro** con formulario completo
  - Nombre completo (guardado localmente, pendiente backend)
  - Nombre de usuario (username)
  - Email
  - Contraseña con toggle de visibilidad
  - Confirmar contraseña con toggle
  - Checkbox de términos y condiciones

- **Validación en tiempo real**
  - Las contraseñas se comparan mientras el usuario escribe
  - Borde rojo inmediato si no coinciden
  - Botón deshabilitado si las contraseñas no coinciden

- **UseCases nuevos:**
  - `ValidateFullNameUseCase` - Valida nombre completo (min 2 chars, solo letras)
  - `ValidateUserNameUseCase` - Valida username (min 2 chars)
  - `ValidatePasswordMatchUseCase` - Valida que las contraseñas coincidan
  - `RegisterUseCase` - Orquesta el proceso de registro

#### 📁 Nuevos Archivos

**Domain Layer:**
- `domain/usecase/validation/ValidateFullNameUseCase.kt`
- `domain/usecase/validation/ValidateUserNameUseCase.kt`
- `domain/usecase/validation/ValidatePasswordMatchUseCase.kt`
- `domain/usecase/validation/RegisterUseCase.kt`

**Presentation Layer:**
- `presentation/screen/register/RegisterScreen.kt`
- `presentation/screen/register/RegisterScreenOwner.kt`
- `presentation/screen/register/RegisterViewModel.kt`
- `presentation/screen/register/model/RegisterUiState.kt`
- `presentation/screen/register/model/RegisterUiAction.kt`

**Navigation:**
- `navigateToLoginFromRegister()` en `NavigationExtensions.kt`

### Changed

- **RegisterResponse.kt**: Ajustado para coincidir con backend
  - Solo `username`, `email`, `message` (sin `id` ni `createdAt`)
  
- **AppModule.kt**: Registrados todos los UseCases y `RegisterViewModel`

- **AppGraph.kt**: `RegisterScreenOwner` reemplaza placeholder

- **TextFieldComponent**: Maneja visibilidad de password internamente (simplificado)

### Fixed

- Validación de contraseñas ahora funciona en tiempo real
- Botón "Registrarme" se deshabilita correctamente si contraseñas no coinciden
- Parseo correcto de respuesta del backend sin campo `id`

### Documentation

- **SPEC-001-register-screen.md**: Marcado como ✅ Completado
- **CHANGELOG.md**: Actualizado con v0.5.0
- **README.md**: Features actualizadas

---

## [0.4.0] - 2026-01-20

### Added

#### 🔐 Autenticación Biométrica (KMP-Ready)

- **Login con huella digital**: Autenticación biométrica completa
  - Huella digital (Fingerprint)
  - Face ID (en dispositivos compatibles)
  - Métodos alternativos: PIN, patrón, contraseña del dispositivo
  - Prompt automático al abrir la app si está activado
  - Prompt manual al tocar el ícono de huella

- **Auto-completar email**: El campo de email se autocompleta automáticamente cuando la biometría está activada, ahorrando tiempo al usuario

- **Arquitectura KMP-Ready**:
  - Interfaces en capa `domain` (Kotlin puro, sin Android)
  - Implementaciones en capa `platform` (Android específico)
  - Inversión de dependencias con Koin

#### 📁 Nuevos Archivos (Domain Layer - Kotlin Puro)

- `domain/biometric/BiometricAuthenticator.kt` - Interface para autenticación
- `domain/biometric/BiometricPromptConfig.kt` - Configuración del prompt
- `domain/biometric/BiometricAvailability.kt` - Estados de disponibilidad
- `domain/biometric/BiometricResult.kt` - Resultados de autenticación
- `domain/storage/SecureCredentialsStorage.kt` - Interface para credenciales

#### 📁 Nuevos Archivos (Platform Layer - Android)

- `platform/biometric/AndroidBiometricAuthenticator.kt` - Impl. de BiometricAuthenticator
- `platform/storage/AndroidSecureCredentialsStorage.kt` - Impl. de SecureCredentialsStorage

#### 📁 Nuevos Archivos (DI)

- `di/BiometricModule.kt` - Módulo Koin para biometría

#### 📱 Cambios en UI

- **LoginScreen**: 
  - Nuevo `CheckboxComponent` para activar login con huella
  - Ícono de huella digital clickeable para activar prompt manual
  - Email pre-rellenado cuando biometría está activada

- **LoginUiState.Content**:
  - `isBiometricAvailable: Boolean` - Hardware soporta biometría
  - `isBiometricEnabled: Boolean` - Usuario activó biometría
  - `shouldShowBiometricPrompt: Boolean` - Mostrar prompt biométrico

- **LoginUiAction**:
  - `OnBiometricToggle` - Toggle del checkbox
  - `OnBiometricIconClick` - Click en ícono de huella
  - `OnBiometricResult(result)` - Resultado del prompt

#### 🔧 Cambios Técnicos

- **MainActivity**: Cambió de `ComponentActivity` a `FragmentActivity` (requerido por BiometricPrompt)
- **LoginScreenOwner**: 
  - Maneja el ciclo de vida del BiometricPrompt
  - Usa `findActivity()` extension para obtener FragmentActivity desde Compose
  - Pasa Activity al `AndroidBiometricAuthenticator` vía `setActivity()`

- **LoginViewModel**:
  - `checkBiometricStatus()` - Verifica disponibilidad y estado
  - `handleBiometricToggle()` - Maneja toggle de checkbox
  - `handleBiometricIconClick()` - Activa prompt manual
  - `handleBiometricResult()` - Procesa resultado del prompt
  - `performLoginWithCredentials()` - Login con credenciales guardadas

### Changed

- **GoodLifeApp.kt**: Incluye `biometricModule` en la inicialización de Koin
- **strings.xml**: Nuevos strings para prompt biométrico
  - `biometric_prompt_title`
  - `biometric_prompt_subtitle`
  - `biometric_checkbox_label`

### Dependencies

```kotlin
// build.gradle.kts (app)
implementation("androidx.biometric:biometric:1.2.0-alpha05")
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

### Security

- **EncryptedSharedPreferences**: Credenciales almacenadas con AES256_GCM
- **MasterKey**: AES256_GCM para encriptación de claves
- **BiometricPrompt**: Usa `BIOMETRIC_STRONG | DEVICE_CREDENTIAL`

### Documentation

- **SPEC-002-biometric-login.md**: Actualizado a estado ✅ COMPLETADO
- **SPEC-001-register-screen.md**: Actualizado con componentes reutilizables
- **SPEC-003-plan-implementation-register.md**: Nuevo plan de implementación paso a paso

---

## [0.3.0] - 2026-01-09

### Added

#### 🔐 Authentication System
- **Login completo** con backend real
  - Conexión a `https://devtukychloe.ddns.net/api/v1/token`
  - Form-urlencoded authentication
  - Manejo de tokens JWT (access + refresh)
  - Almacenamiento seguro con SharedPreferences

- **LoginUseCase**: Orquesta validación y autenticación
- **ValidateEmailUseCase**: Validación de email
- **ValidatePasswordUseCase**: Validación de contraseña
- **GetCurrentUserUseCase**: Obtiene usuario logueado
- **LogoutUseCase**: Cierre de sesión

#### 🌐 Network Layer (Retrofit + OkHttp)
- **GoodLifeApiService**: Interface Retrofit con endpoints
  - `POST /api/v1/token` - Login
  - `POST /api/v1/register` - Registro
  
- **GoodLifeInterceptor**: Interceptor personalizado
  - Agrega `Authorization: Bearer` automáticamente
  - Logging detallado de URL, request y response
  - Emojis para fácil identificación en Logcat
  - Skip de endpoints públicos

- **NetworkConstants**: URL base y endpoints públicos
- **HttpCode**: Enum con códigos HTTP del backend
- **ApiException**: Excepción personalizada para errores de API

- **DTOs**:
  - `BaseResponse<T>`: Wrapper genérico del backend
  - `AuthResponse`: Respuesta de login (accessToken, refreshToken)
  - `RegisterRequest/Response`: Registro de usuario

#### 💾 Database Layer (Room)
- **GoodLifeDatabase**: Base de datos con patrón Singleton thread-safe
  - `@Volatile` + `synchronized` para thread safety
  - `getInstance(context)` para acceso global
  - `fallbackToDestructiveMigration()` para desarrollo

- **UserEntity**: Entidad para almacenar usuario logueado
- **UserDao**: Operaciones CRUD de usuario
  - `insertUser()`, `getUser()`, `deleteUser()`

- **Mappers como extension functions**:
  - `UserEntity.toDomain()` → `User`
  - `User.fromDomain()` → `UserEntity`
  - `AuthResponse.toAuthToken()` → `AuthToken`
  - `RegisterResponse.toUser()` → `User`

#### 🏠 Home Screen
- **HomeViewModel**: Carga usuario y maneja logout
- **HomeScreen**: Pantalla de bienvenida
  - Mensaje "¡Bienvenido, [nombre]!"
  - Botón de cerrar sesión
  - Estados: Loading, Content, Error

- **HomeUiState** / **HomeUiAction**: Extraídos a archivos separados

#### 📦 Dependency Injection
- **NetworkModule**: OkHttpClient, Retrofit, ApiService, Json
- **DatabaseModule**: GoodLifeDatabase, UserDao
- **AppModule actualizado**: UseCases, DataSources, Repository

#### 🎨 Compose Optimizations
- **@Stable** en todos los UI States y Actions
  - `LoginUiState`, `LoginUiAction`
  - `HomeUiState`, `HomeUiAction`
  - `SplashUiState`, `SplashUiAction`
  
- Previene recomposiciones innecesarias

#### 🔧 Infrastructure
- **TokenManager**: Almacena tokens en SharedPreferences
- **AuthRemoteDataSource**: Ejecuta llamadas API y mapea responses
- **AuthRepositoryImpl**: Implementación completa del repositorio
  - Login → API → Save tokens → Save user to Room
  - Logout → Clear tokens → Delete user from Room

### Changed
- **AndroidManifest.xml**: Agregados permisos INTERNET y ACCESS_NETWORK_STATE
- **build.gradle.kts**: Agregadas dependencias Room, OkHttp, KSP
- **libs.versions.toml**: Versiones de Room 2.7.0-alpha03, OkHttp 4.12.0

### Technical Decisions
- **Singleton en GoodLifeDatabase**: Thread-safe con double-checked locking
- **Extension functions para mappers**: Más idiomático en Kotlin
- **@Stable annotations**: Optimización de recomposición en Compose
- **Mensajes de error del backend**: No hardcodeados, vienen del BaseResponse

---

## [0.2.0] - 2026-01-08

### Added

#### Theme System
- **Font.kt**: Familias de fuentes personalizadas
  - `UbuntuFontFamily`: Light, Regular, Medium, Bold (para títulos)
  - `InterFontFamily`: Light, Regular, Medium, SemiBold, Bold (para body text)

- **res/font/**: 9 archivos de fuentes TTF
  - Ubuntu: `ubuntu_light`, `ubuntu_regular`, `ubuntu_medium`, `ubuntu_bold`
  - Inter: `inter_light`, `inter_regular`, `inter_medium`, `inter_semibold`, `inter_bold`

- **Color.kt**: Paleta de colores completa
  - Colores primarios: `DarkGreen`, `LightGreen`, `GreenSelected`, `EmeraldGreen`
  - Gradientes de fondo: `DegradeBackground1-5`
  - Colores semánticos: `SuccessGreen`, `ErrorRed`, `WarningOrange`, `InfoBlue`
  - Colores de UI: texto, superficies, bordes, botones
  - Colores para modo oscuro
  - Brushes reutilizables: `ButtonEnabledGradientBrush`, `InputBorderNormalGradientBrush`, etc.
  - Listas de colores para estados: `ButtonColorsEnabled`, `InputBorderColorsNormal`, etc.

- **Type.kt**: Sistema tipográfico Material 3 completo
  - Display, Headline, Title con Ubuntu
  - Body, Label con Inter
  - Estilos personalizados: `AppTitleStyle`, `PlaceholderStyle`, `ButtonTextStyle`, `LinkTextStyle`

- **Theme.kt**: Temas claro y oscuro
  - `GoodLifeTheme()`: Composable principal
  - `LightColorScheme` y `DarkColorScheme` completos
  - Configuración automática de status bar
  - `ExtendedColors` para colores adicionales

### Changed
- Deshabilitado Dynamic Color para mantener identidad de marca

---

## [0.1.0] - 2026-01-07

### Added

#### Core Layer
- **Result.kt**: Wrapper genérico para operaciones que pueden fallar (Success, Error, Loading)
  - Funciones de extensión: `map()`, `resultOf()`, `suspendResultOf()`
  - Métodos encadenables: `onSuccess()`, `onError()`, `onLoading()`

- **DispatcherProvider.kt**: Abstracción de dispatchers de coroutines
  - Interface para inyección de dependencias y testing
  - Implementación Android con Dispatchers.Main, IO, Default
  - Preparado para Kotlin Multiplatform

#### Navigation System (Reactive Pattern)
- **NavigationAction.kt**: Sealed class para acciones de navegación
  - `NavigateTo<T>`: Navegación type-safe con NavOptions
  - `NavigateUp`: Retroceso en el stack
  - `PopBackTo<T>`: Pop hasta una ruta específica

- **ComposeNavigationController.kt**: Interface para navegación reactiva
  - Desacopla ViewModels del NavController
  - Expone SharedFlow para eventos de navegación

- **ComposeNavigationControllerImpl.kt**: Implementación con SharedFlow
  - Thread-safe con CoroutineScope
  - NonCancellable para garantizar emisión de eventos

- **SmartNavHost.kt**: NavHost lifecycle-aware
  - Observa SharedFlow con `repeatOnLifecycle(STARTED)`
  - Animaciones de transición preconfiguradas (slide + fade)
  - Previene navegación cuando Activity está en background

#### Type-Safe Routes
- **AppRoute.kt**: Rutas de nivel aplicación
  - Splash, Login, Register, Main
  - Todas con `@Serializable` para type-safety

- **TabRoute.kt**: Rutas dentro del sistema de tabs
  - Home, Workouts, Meals, Settings
  - Rutas con parámetros (WorkoutDetail, MealDetail, TaskDetail)
  - TabGraphRoute para grafos anidados

- **NavigationExtensions.kt**: Extension functions para navegación común
  - `navigateToLogin()`, `navigateToMain()`, `navigateToRegister()`
  - `navigateToWorkoutDetail()`, `navigateToMealDetail()`, etc.

- **AppGraph.kt**: Definición del grafo de navegación principal

#### Dependency Injection
- **AppModule.kt**: Módulo principal de Koin
  - Provisión de DispatcherProvider
  - Provisión de ComposeNavigationController

- **GoodLifeApp.kt**: Application class
  - Inicialización de Koin con androidContext

#### Infrastructure
- **MainActivity.kt**: Activity principal actualizada
  - Single Activity Architecture
  - Integración con SmartNavHost
  - Edge-to-edge display

- **AndroidManifest.xml**: Actualizado con GoodLifeApp

### Changed
- Movido theme de `ui/theme` a `presentation/theme`
- Actualizado `build.gradle.kts` con dependencias de navegación y serialización
- Actualizado `libs.versions.toml` con versiones de librerías

### Technical Decisions
- **SharedFlow vs StateFlow**: SharedFlow para eventos one-shot (navegación), StateFlow para UI state
- **repeatOnLifecycle**: Previene procesamiento de eventos cuando UI no está visible
- **Type-safe routes**: Eliminación de strings mágicos con `@Serializable`
- **Owner Pattern**: Separación de inyección de dependencias y UI pura

---

## Convenciones de Commits

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bugs
- `docs:` Cambios en documentación
- `style:` Formateo, sin cambios de código
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Tareas de mantenimiento
