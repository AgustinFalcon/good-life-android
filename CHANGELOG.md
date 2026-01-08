# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

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
