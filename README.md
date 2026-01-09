# GoodLife Android 🏋️‍♂️

Aplicación Android para gestión de bienestar personal.

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

## 📁 Estructura del Proyecto

```
app/src/main/java/com/agusstkd/goodlife/
├── core/           # Utilidades compartidas (KMP-ready)
├── data/           # Capa de datos (Room, Retrofit)
├── di/             # Módulos de Koin
├── domain/         # Lógica de negocio
└── presentation/   # UI, ViewModels, Navegación
```

## 🏗️ Arquitectura

- **Clean Architecture** con capas Domain, Data, Presentation
- **MVVM** para la capa de presentación
- **Koin** para inyección de dependencias
- **Navegación Reactiva** con SharedFlow
- **Type-safe Routes** con kotlinx-serialization

Ver [ARCHITECTURE.md](docs/ARCHITECTURE.md) para detalles completos.

## 🧭 Navegación

Sistema de navegación desacoplado y lifecycle-aware:

```kotlin
// En ViewModel
navigationController.navigateToMain()

// Rutas type-safe
AppRoute.Login      // Pantalla de login
AppRoute.Main       // Pantalla principal con tabs
TabRoute.WorkoutDetail(workoutId = 5)  // Con parámetros
```

## 📦 Dependencias Principales

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| Kotlin | 2.0.21 | Lenguaje |
| Compose BOM | 2024.04.01 | UI Framework |
| Navigation Compose | 2.8.5 | Navegación |
| Koin | 4.0.0 | DI |
| Kotlinx Serialization | 1.7.3 | Type-safe routes |

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

### Branches
- `master` - Producción
- `develop` - Desarrollo
- `feature/*` - Nuevas funcionalidades
- `fix/*` - Correcciones

## 📄 Documentación

- [ARCHITECTURE.md](docs/ARCHITECTURE.md) - Arquitectura detallada
- [CHANGELOG.md](CHANGELOG.md) - Historial de cambios

## 🤝 Contribuir

1. Crear branch desde `develop`
2. Hacer cambios siguiendo convenciones
3. Crear Pull Request
4. Code Review
5. Merge

## 📜 Licencia

Proyecto privado - Todos los derechos reservados.
