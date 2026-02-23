# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

---

## [Unreleased] - 2026-02-03

### Added

#### 🌐 Sistema de Localización KMP-Ready (SPEC-007)

- **AppLanguage sealed interface** - Sistema completo de internacionalización
  - 107+ strings organizados en 9 grupos de textos
  - 3 idiomas: Español, English, Português
  - Detección automática de locale del dispositivo
  - 100% Kotlin puro — sin Android Context, sin `stringResource()`, sin `R.string`
  - Exhaustividad garantizada por compilador (sealed interface)

- **Grupos de textos (UiTexts.kt)**:
  - `AuthTexts` (24 strings) — Login, Register, biometric prompt
  - `ValidationTexts` (20 strings) — UseCases de validación
  - `ErrorTexts` (12 strings) — Mensajes de error genéricos
  - `DailyTexts` (11 strings) — Daily tab (progreso, errores)
  - `MainScaffoldTexts` (16 strings) — BottomNav, modal, tabs
  - `HomeTexts` (5 strings) — Home screen
  - `AccessibilityTexts` (12 strings) — Content descriptions
  - `DailyItemLabels` (4 strings) — Etiquetas de tipo de item
  - `RelativeDateTexts` (3 strings) — Hoy/Ayer/Mañana

- **Screen Wrappers**:
  - `AuthScreenTexts` — Agrupa `AuthTexts` + `AccessibilityTexts` para Login/Register

- **Params con textos integrados**:
  - `TextFieldParams` — `passwordToggleHide`, `passwordToggleShow`
  - `BottomNavigationParams` — `fabContentDescription`
  - `DateHeaderParams` (nuevo) — `openCalendarLabel`, `previousDayLabel`, `nextDayLabel`, `notificationsLabel`
  - `WorkoutTabHeaderParams` (nuevo) — `filterContentDescription`

#### 📁 Nuevos Archivos

**Core Layer:**
- `core/datetime/language/UiTexts.kt` - Todos los data class de textos + screen wrappers

**Documentación:**
- `docs/specs/SPEC-007-app-language.md` - Especificación completa

#### 🌐 Offline-First Architecture + SWR (SPEC-006)

- **Stale-While-Revalidate Pattern** - Patrón profesional para cache + sincronización
  - Cache-first: UI renderiza en 0ms
  - Background revalidation: Backend siempre consulta
  - Fallback offline: App funciona sin internet
  - Optimistic UI: Updates instantáneos

- **Documentación completa**:
  - `docs/specs/SPEC-006-offline-first-swr.md` - Especificación completa (800+ líneas)
  - Comparación TTL vs SWR
  - Implementación de Repository con SWR
  - Optimistic UI en ViewModels
  - Flujos de usuario (online/offline)
  - Optimizaciones avanzadas
  - Aplicación en todos los módulos (Daily, Tasks, Habits, Workouts, Meals)

- **Plan actualizado**:
  - `docs/plans/DAILY-IMPLEMENTATION-PLAN.md` - Actualizado con SWR
  - Eliminado `cachedAt` y `isFresh()` de entities
  - Repository con `fetchDailyLogWithSWR()`
  - Optimistic UI en `DailyTabViewModel`

#### 📅 DateProvider Pattern (SPEC-004)

- **DateProvider interface** - Abstracción para manejo de fechas (KMP-ready)
  - `today()`: Fecha actual en timezone local
  - `yesterday()`, `tomorrow()`: Métodos de conveniencia
  - `now()`: Timestamp UTC actual

- **RealDateProvider** - Implementación para producción
  - Usa `Clock.System` + `TimeZone.currentSystemDefault()`
  - Cachea TimeZone para optimizar performance
  - Encapsula `@OptIn(ExperimentalTime)` en un solo lugar

- **FakeDateProvider** - Implementación para tests
  - Fecha fija inyectada en constructor
  - Habilita tests deterministas sin depender del reloj del sistema

#### 📁 Nuevos Archivos

**Core Layer:**
- `core/datetime/DateProvider.kt` - Interface
- `core/datetime/RealDateProvider.kt` - Implementación Android
- `core/datetime/FakeDateProvider.kt` - Implementación para tests

**Documentación:**
- `docs/specs/SPEC-004-date-provider.md` - Especificación completa (1000+ líneas)
- `docs/archive/` - Documentación de proceso preservada
- `docs/README.md` - Índice actualizado

### Changed

#### 🏗️ Refactoring de Localización (SPEC-007)

- **LoginViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `screenTexts: AuthScreenTexts`
  - Biometric prompt texts desde `AuthTexts` (ya no desde `R.string`)

- **RegisterViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `screenTexts: AuthScreenTexts`

- **HomeViewModel**: Recibe `language: AppLanguage` por constructor
  - Expone `homeTexts: HomeTexts`

- **DailyTabViewModel**: Expone `dailyTexts` y `accessibilityTexts`

- **MainScaffoldViewModel**: `fabContentDescription` ahora es parte de `MainScaffoldUiState`
  - Inicializado desde `language.accessibilityTexts.add`

- **MainScaffoldUiState**: Agregado `fabContentDescription: String`

- **LoginScreen / RegisterScreen**: Reciben `texts: AuthScreenTexts` como parámetro
  - Eliminado `koinInject<AppLanguage>()`

- **HomeScreen**: Recibe `homeTexts: HomeTexts` como parámetro

- **DailyScreen**: Recibe `dateHeaderParams: DateHeaderParams` en lugar de `accessibilityTexts` directo

- **LoginScreenOwner**: Usa `viewModel.screenTexts` para biometric prompt strings
  - Eliminado `stringResource()` y `R.string` para biometric prompt

- **DailyScreenOwner**: Construye `DateHeaderParams` desde `viewModel.accessibilityTexts`

- **TextFieldComponent**: `passwordToggleHide/Show` movidos dentro de `TextFieldParams`

- **BottomNavigationComponent**: `fabContentDescription` movido dentro de `BottomNavigationParams`

- **DateHeaderComponent**: Refactorizado a recibir `DateHeaderParams`

- **TabRowHeaderComponent**: Refactorizado a recibir `WorkoutTabHeaderParams`

- **TitleComponent**: Ahora solo acepta `text: String` (eliminado `textId: Int?` y `stringResource`)

- **SplashScreenOwner**: Eliminado import de `koinInject` no utilizado

- **AppModule.kt**:
  - `single<AppLanguage>` con detección automática de locale (`Locale.getDefault().language`)
  - Todos los ViewModels reciben `language = get()`
  - Registrado `DateProvider` como singleton

#### 📅 DateProvider (SPEC-004)

- **DailyTabViewModel**: Ahora usa `DateProvider` en lugar de `Clock` directamente
  - Formatea fechas en el ViewModel (no en UI)
  - Compara fechas con `dateProvider.today()`, `yesterday()`, `tomorrow()`

- **MainScaffoldViewModel**: Integrado con DateProvider

- **LocalDateExtensions.kt**: Migradas funciones a ViewModels

### Removed

- ❌ `stringResource()` de toda la capa de presentación compartida
- ❌ `R.string` de toda la capa de presentación compartida
- ❌ `koinInject()` de todos los Screens y Components
- ❌ Textos hardcodeados de Screens y Components
- ❌ Parámetros de texto sueltos fuera de Params en Components
- ❌ `textId: Int?` en `TitleComponent` (ahora solo `text: String`)

### Fixed

#### 🐛 Bug de Timezone Resuelto (SPEC-004)
- **Problema**: App mostraba fecha +1 día en emuladores
  - `TimeZone.currentSystemDefault()` devolvía UTC en lugar de timezone local
  - Emuladores con hora 23:00 mostraban el día siguiente

- **Solución**: DateProvider encapsula correctamente Clock + TimeZone
  - Cachea `TimeZone.currentSystemDefault()` al inicializar
  - Usa `clock.now().toLocalDateTime(timeZone).date` para fecha local correcta
  - ✅ Validado: Si son las 23:00 del 3 de febrero → muestra "3" (no "4")

#### 🏗️ Violaciones arquitectónicas corregidas (SPEC-007)
- **Antes**: `koinInject<AppLanguage>()` en Screens y Components — violaba KMP-readiness
- **Ahora**: Textos fluyen del ViewModel al Screen como parámetro, Components via Params
- **Antes**: `stringResource(R.string.xxx)` en Components — bloqueaba KMP
- **Ahora**: Todos los textos vienen de `AppLanguage` (pure Kotlin)
- **Antes**: Textos de accessibility como parámetros sueltos
- **Ahora**: Encapsulados dentro de Params (`DateHeaderParams`, `TextFieldParams`, etc.)

#### ✅ Beneficios acumulados (SPEC-004 + SPEC-007)
- ✅ Timezone local correcto
- ✅ UI 100% pura (sin lógica de fechas, sin inyección directa, sin stringResource)
- ✅ Testeable con FakeDateProvider y textos mock
- ✅ KMP-compatible (solo `kotlinx.datetime`, textos en pure Kotlin)
- ✅ Encapsula `@OptIn(ExperimentalTime)` en `RealDateProvider.kt`
- ✅ Performance optimizada (cachea TimeZone)
- ✅ 3 idiomas con ~321 traducciones (107 strings × 3)
- ✅ Detección automática de locale

### Documentation

- **SPEC-007**: Especificación completa del sistema de localización
  - Problemas resueltos (koinInject, stringResource, textos sueltos)
  - Arquitectura y flujo de datos
  - Reglas estrictas con ejemplos
  - Cómo agregar nueva vista / nuevo idioma

- **SPEC-004**: Especificación completa del DateProvider pattern
  - Bug de timezone + solución
  - Before/After comparisons
  - Tests deterministas

- **ARCHITECTURE.md**: Actualizado con localización, DateProvider, Params
- **ARCHITECTURE_GUIDE.md**: v2.0 — sección 9 de localización, 6 reglas nuevas, checklist actualizado
- **specs/README.md**: Índice completo SPEC-001 a SPEC-007, diagrama de relaciones
- **PROGRESS.md**: Estado actualizado al 2026-02-03
- **SPEC-003**: Actualizado a 85% — fases 7 y 8a completadas

- **archive/**: Documentación de proceso preservada
  - `ANALISIS-CLOCK-Y-MEJORAS.md` - Análisis del problema
  - `DATEPROVIDER-IMPLEMENTACION-COMPLETA.md` - Tracking de implementación
  - `INSTRUCCIONES-VALIDACION.md` - Checklist de validación

---

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
