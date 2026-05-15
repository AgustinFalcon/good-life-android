# SPEC-007: Sistema de Localización KMP-Ready (AppLanguage)

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-007 |
| **Tipo** | Core Architecture |
| **Prioridad** | Crítica |
| **Estado** | ✅ Completado |
| **Fecha Creación** | 2026-02-03 |
| **Fecha Completado** | 2026-02-03 |
| **Dependencia** | DateProvider (SPEC-004), Main Scaffold (SPEC-003) |

---

## 🎯 Objetivo

Crear un sistema de internacionalización (i18n) que:
- **Elimine `stringResource()` y `R.string`** de Screens y Components
- **Elimine `koinInject()` de todo excepto Owners** (y solo para BiometricAuthenticator)
- **Sea 100% KMP-ready** (pure Kotlin, sin Android Context)
- **Encapsule textos en clases Params** para firmas de función limpias
- **Soporte 3 idiomas**: Español, English, Português
- **Detecte automáticamente** el locale del dispositivo
- **Sea exhaustivo por compilador**: agregar un idioma obliga a implementar todos los textos

---

## ❌ Problemas que Resolvió

### 1. `stringResource()` en Components (bloqueaba KMP)

```kotlin
// ❌ ANTES — dependencia Android en componente "puro"
@Composable
fun BottomNavigationComponent(...) {
    Text(text = stringResource(item.label))  // R.string.xxx
}
```

`stringResource` y `R.string` son APIs exclusivas de Android. Cualquier componente que las use no puede moverse a `commonMain` en KMP.

### 2. `koinInject()` en Screens y Components

```kotlin
// ❌ ANTES — DI directa en Screens
@Composable
fun LoginScreen(
    language: AppLanguage = koinInject()  // ❌ Screen inyecta
) { ... }

// ❌ ANTES — DI directa en Components
@Composable
fun TextFieldComponent(
    accessibility: AccessibilityTexts = koinInject<AppLanguage>().accessibilityTexts  // ❌
) { ... }
```

Esto viola Clean Architecture:
- Los Screens deben ser **renderers puros** que reciben datos por parámetro
- Los Components no deben conocer el contenedor DI
- Imposibilita previews y tests sin Koin arrancado

### 3. Textos sueltos fuera de Params

```kotlin
// ❌ ANTES — parámetros de texto sueltos
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    auth: AuthTexts,                    // suelto
    accessibility: AccessibilityTexts,  // suelto
    modifier: Modifier = Modifier
)

@Composable
fun BottomNavigationComponent(
    params: BottomNavigationParams,
    fabContentDescription: String = ""  // fuera del Params
)
```

Firmas sucias con mezcla de Params + textos individuales.

---

## ✅ Solución Implementada

### Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────────┐
│                        UiTexts.kt                                    │
│  data class AuthTexts(...)                                           │
│  data class HomeTexts(...)                                           │
│  data class DailyTexts(...)                                          │
│  data class ValidationTexts(...)                                     │
│  data class ErrorTexts(...)                                          │
│  data class MainScaffoldTexts(...)                                   │
│  data class AccessibilityTexts(...)                                  │
│  data class AuthScreenTexts(auth, accessibility)  ← Screen wrappers │
└────────────────────────────────────┬────────────────────────────────┘
                                     │
┌────────────────────────────────────▼────────────────────────────────┐
│                       AppLanguage.kt                                 │
│  sealed interface AppLanguage {                                      │
│      val authTexts: AuthTexts                                        │
│      val homeTexts: HomeTexts                                        │
│      val dailyTexts: DailyTexts                                      │
│      val validationTexts: ValidationTexts                            │
│      val errorTexts: ErrorTexts                                      │
│      val mainScaffoldTexts: MainScaffoldTexts                        │
│      val accessibilityTexts: AccessibilityTexts                      │
│      val dailyItemLabels: DailyItemLabels                            │
│      val monthNames, dayNamesShort, formats, relativeTexts           │
│                                                                      │
│      data object Spanish : AppLanguage { ... }                       │
│      data object English : AppLanguage { ... }                       │
│      data object Portuguese : AppLanguage { ... }                    │
│  }                                                                   │
└────────────────────────────────────┬────────────────────────────────┘
                                     │
┌────────────────────────────────────▼────────────────────────────────┐
│                        AppModule.kt                                  │
│  single<AppLanguage> {                                               │
│      val locale = java.util.Locale.getDefault().language             │
│      when (locale) {                                                 │
│          "es" -> AppLanguage.Spanish                                 │
│          "pt" -> AppLanguage.Portuguese                              │
│          else -> AppLanguage.English                                 │
│      }                                                               │
│  }                                                                   │
└─────────────────────────────────────────────────────────────────────┘
```

### Flujo de datos completo

```
Koin DI (singleton)
  │  AppLanguage (detecta locale automáticamente)
  │
  ▼
ViewModel (constructor injection)
  │  private val language: AppLanguage
  │
  │  Expone textos agrupados:
  │  val screenTexts: AuthScreenTexts
  │      get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)
  │
  ▼
Owner (@Composable — único punto con koinViewModel)
  │  val viewModel = koinViewModel()
  │
  │  Screen(uiState, onAction, texts = viewModel.screenTexts)
  │
  ▼
Screen (UI pura — NO inyecta nada)
  │  fun XxxScreen(uiState, onAction, texts: AuthScreenTexts)
  │      val auth = texts.auth
  │      val accessibility = texts.accessibility
  │
  │  Pasa textos dentro de Params:
  │  TextFieldComponent(params = TextFieldParams(..., passwordToggleHide = accessibility.hide))
  │
  ▼
Component (puro — todo via Params)
      fun TextFieldComponent(params: TextFieldParams, onValueChange)
      fun DateHeaderComponent(params: DateHeaderParams, onPreviousDay, onNextDay)
```

---

## 📁 Archivos del Sistema

### Core — Definición de textos

| Archivo | Ubicación | Descripción |
|---------|-----------|-------------|
| `UiTexts.kt` | `core/datetime/language/` | Todos los `data class` de textos + screen wrappers |
| `AppLanguage.kt` | `core/datetime/language/` | Sealed interface + 3 idiomas con todas las traducciones |
| `DateFormats.kt` | `core/datetime/language/` | `DateFormats` + `RelativeDateTexts` + `DailyItemLabels` |

### Presentation — Clases Params con textos integrados

| Archivo | Clase Params | Textos dentro |
|---------|-------------|---------------|
| `TextFieldComponent.kt` | `TextFieldParams` | `passwordToggleHide`, `passwordToggleShow` |
| `BottomNavigationComponent.kt` | `BottomNavigationParams` | `fabContentDescription` |
| `DateHeaderComponent.kt` | `DateHeaderParams` | `openCalendarLabel`, `previousDayLabel`, `nextDayLabel`, `notificationsLabel` |
| `TabRowHeaderComponent.kt` | `WorkoutTabHeaderParams` | `filterContentDescription` |

### Presentation — Screen Wrappers

| Wrapper | Screens que lo usan | Contenido |
|---------|---------------------|-----------|
| `AuthScreenTexts` | `LoginScreen`, `RegisterScreen` | `auth: AuthTexts` + `accessibility: AccessibilityTexts` |

### ViewModels — Exposición de textos

| ViewModel | Propiedad expuesta | Tipo |
|-----------|--------------------|------|
| `LoginViewModel` | `screenTexts` | `AuthScreenTexts` |
| `RegisterViewModel` | `screenTexts` | `AuthScreenTexts` |
| `HomeViewModel` | `homeTexts` | `HomeTexts` |
| `DailyTabViewModel` | `dailyTexts` + `accessibilityTexts` | Directos |
| `MainScaffoldViewModel` | (via UiState) | `fabContentDescription` en `MainScaffoldUiState` |

---

## 📐 Reglas Arquitectónicas

### Regla 1: Screens y Components NUNCA inyectan

```kotlin
// ✅ CORRECTO
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    texts: AuthScreenTexts         // recibido por parámetro
)

// ❌ PROHIBIDO
@Composable
fun LoginScreen(
    language: AppLanguage = koinInject()  // NUNCA koinInject en Screen
)
```

### Regla 2: Textos siempre dentro de Params

```kotlin
// ✅ CORRECTO — texto dentro del Params
TextFieldComponent(
    params = TextFieldParams(
        value = password,
        placeholder = auth.password,
        type = TextFieldType.PASSWORD,
        passwordToggleHide = accessibility.hide,
        passwordToggleShow = accessibility.show
    ),
    onValueChange = { ... }
)

// ❌ PROHIBIDO — texto fuera del Params
TextFieldComponent(
    params = TextFieldParams(value = password, ...),
    passwordToggleHide = accessibility.hide  // fuera del Params
)
```

### Regla 3: ViewModel expone un solo objeto de textos

```kotlin
// ✅ CORRECTO — un solo accessor
class LoginViewModel(private val language: AppLanguage) {
    val screenTexts: AuthScreenTexts
        get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)
}

// ❌ EVITAR — múltiples accessors
class LoginViewModel(private val language: AppLanguage) {
    val authTexts get() = language.authTexts
    val accessibilityTexts get() = language.accessibilityTexts
}
```

### Regla 4: Owner es el puente y nada más

```kotlin
// ✅ CORRECTO
@Composable
fun RegisterScreenOwner(viewModel: RegisterViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        texts = viewModel.screenTexts        // del ViewModel, no de koinInject
    )
}
```

### Regla 5: Fechas formateadas van en el UiState

```kotlin
// ✅ CORRECTO — ViewModel formatea, Screen solo pinta
data class MainScaffoldUiState(
    val currentDateFormatted: String = "",  // "Hoy, 3 de febrero de 2026"
    val fabContentDescription: String = ""  // "Agregar"
)

// ❌ PROHIBIDO — Screen formatea
@Composable
fun MainScaffoldScreen(uiState: MainScaffoldUiState) {
    val formatted = date.format(language.formats.full)  // NUNCA en Screen
}
```

---

## 🔄 Cómo Agregar una Nueva Vista

### Checklist paso a paso

```
1. UiTexts.kt        → data class ProfileTexts(title, editProfile, ...)
2. AppLanguage.kt     → val profileTexts: ProfileTexts + 3 idiomas
3. UiTexts.kt         → data class ProfileScreenTexts(profile, accessibility)
                        (wrapper, solo si la Screen necesita >1 grupo de textos)
4. ProfileUiState.kt  → sealed interface con Loading/Content/Error
5. ProfileUiAction.kt → sealed interface con acciones del user
6. ProfileViewModel.kt:
     - constructor: private val language: AppLanguage
     - val screenTexts: ProfileScreenTexts
         get() = ProfileScreenTexts(language.profileTexts, language.accessibilityTexts)
7. AppModule.kt       → viewModel { ProfileViewModel(language = get(), ...) }
8. ProfileScreen.kt   → fun ProfileScreen(uiState, onAction, texts: ProfileScreenTexts)
9. ProfileScreenOwner → koinViewModel(), pasa viewModel.screenTexts
10. NavGraph           → ruta al Owner
```

---

## 🔄 Cómo Agregar un Nuevo Idioma

```kotlin
// 1. Agregar data object en AppLanguage.kt
data object Italian : AppLanguage {
    override val monthNames = MonthNames("gennaio", "febbraio", ...)
    override val authTexts = AuthTexts(login = "Accedi", ...)
    // ... el compilador marca error en CADA propiedad faltante
}

// 2. Agregar case en AppModule.kt
single<AppLanguage> {
    when (java.util.Locale.getDefault().language) {
        "es" -> AppLanguage.Spanish
        "pt" -> AppLanguage.Portuguese
        "it" -> AppLanguage.Italian    // ← NUEVO
        else -> AppLanguage.English
    }
}
```

El compilador de Kotlin **garantiza exhaustividad**: no compila hasta que el nuevo idioma implemente todas las propiedades de la sealed interface.

---

## 📊 Grupos de Textos

| Grupo | Cantidad de strings | Usado por |
|-------|--------------------:|-----------|
| `AuthTexts` | 24 | LoginScreen, RegisterScreen, LoginScreenOwner |
| `ValidationTexts` | 20 | ValidateEmail/Password/UserName/FullName UseCases |
| `ErrorTexts` | 12 | LoginUseCase, RegisterUseCase, ViewModels |
| `DailyTexts` | 11 | DailyScreen, DailyScreenOwner, DailyItemCard |
| `MainScaffoldTexts` | 16 | MainScaffoldViewModel, BottomNavItems, WorkoutTabs |
| `HomeTexts` | 5 | HomeScreen |
| `AccessibilityTexts` | 12 | DateHeader, TextFieldComponent, BottomNav, Tabs |
| `DailyItemLabels` | 4 | DailyTabViewModel → DailyItemUiModel |
| `RelativeDateTexts` | 3 | DailyTabViewModel (Hoy/Ayer/Mañana) |
| **Total** | **~107** | **× 3 idiomas = 321 traducciones** |

---

## 🔗 Relación con Otros SPECs

| SPEC | Relación |
|------|----------|
| SPEC-003 (Main Scaffold) | `MainScaffoldTexts` alimenta BottomNav, modal, tabs |
| SPEC-004 (DateProvider) | `AppLanguage.formats` formatea fechas, `relativeTexts` da "Hoy/Ayer/Mañana" |
| SPEC-001 (Register) | `AuthTexts` localiza la pantalla de registro |
| SPEC-002 (Biometric) | `AuthTexts.biometricPrompt*` localiza el prompt biométrico |
| SPEC-005 (Network) | `ErrorTexts` localiza mensajes de error de red |

---

## ✅ Estado Final

### Eliminados completamente

- [x] `stringResource()` en toda la capa de presentación (excepto comentarios)
- [x] `R.string` en toda la capa de presentación
- [x] `koinInject()` en Screens
- [x] `koinInject()` en Components
- [x] Textos hardcodeados en Screens y Components
- [x] Parámetros de texto sueltos fuera de Params

### Único `koinInject` restante (aceptable)

```kotlin
// LoginScreenOwner.kt — BiometricAuthenticator necesita Activity (Android-specific)
val biometricAuthenticator: BiometricAuthenticator = koinInject()
```

Esto es correcto: el Owner es el puente platform-specific, y `BiometricPrompt` de Android requiere `FragmentActivity`.

---

**Última actualización:** 2026-02-03
