# SPEC-001: Pantalla de Registro

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-001 |
| **Tipo** | Feature |
| **Prioridad** | Alta |
| **Estado** | 🔜 Pendiente |
| **Fecha Creación** | 2026-01-09 |
| **Dependencia** | Login completado ✅ |

---

## 🎯 Objetivo

Crear la pantalla de registro de usuarios que permita crear nuevas cuentas en GoodLife, reutilizando la arquitectura y componentes ya establecidos.

---

## 🖼️ Diseño Visual

### Estructura de la Pantalla

```
┌─────────────────────────────────────┐
│           [Logo pequeño]            │
│                                     │
│          Crear Cuenta               │
│  Crea tu cuenta para empezar       │
│       tu vida saludable            │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 👤  Nombre completo         │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ ✉️  Correo electrónico      │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 🔒  Contraseña          👁️ │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 🔒  Confirmar contraseña 👁️│   │
│  └─────────────────────────────┘   │
│                                     │
│  ☑️ Acepto los Términos y          │
│     Condiciones y la Política      │
│     de Privacidad                  │
│                                     │
│  ┌─────────────────────────────┐   │
│  │      Registrarme →          │   │  ← ButtonComponent con isLoading
│  └─────────────────────────────┘   │
│                                     │
│  ¿Ya tienes cuenta? Iniciar Sesión │
│                                     │
└─────────────────────────────────────┘
```

---

## 📝 Campos del Formulario

### 1. Nombre Completo
| Propiedad | Valor |
|-----------|-------|
| **Tipo** | `TextFieldType.USER` |
| **Placeholder** | "Nombre completo" |
| **Validación** | Mínimo 2 caracteres, solo letras y espacios |
| **Ícono** | Persona (👤) |
| **Obligatorio** | Sí |

### 2. Correo Electrónico
| Propiedad | Valor |
|-----------|-------|
| **Tipo** | `TextFieldType.EMAIL` |
| **Placeholder** | "Correo electrónico" |
| **Validación** | Formato email válido (regex) |
| **Ícono** | Sobre (✉️) |
| **Teclado** | `KeyboardType.Email` |
| **Obligatorio** | Sí |
| **UseCase** | `ValidateEmailUseCase` (ya existe ✅) |

### 3. Contraseña
| Propiedad | Valor |
|-----------|-------|
| **Tipo** | `TextFieldType.PASSWORD` |
| **Placeholder** | "Contraseña" |
| **Validación** | Mínimo 6 caracteres |
| **Ícono izquierdo** | Candado (🔒) |
| **Ícono derecho** | Toggle visibilidad (👁️) |
| **Obligatorio** | Sí |
| **UseCase** | `ValidatePasswordUseCase` (ya existe ✅) |

### 4. Confirmar Contraseña
| Propiedad | Valor |
|-----------|-------|
| **Tipo** | `TextFieldType.PASSWORD` |
| **Placeholder** | "Confirmar contraseña" |
| **Validación** | Debe coincidir con "Contraseña" |
| **Ícono izquierdo** | Candado (🔒) |
| **Ícono derecho** | Toggle visibilidad (👁️) |
| **Obligatorio** | Sí |
| **UseCase** | `ValidatePasswordMatchUseCase` (NUEVO) |

### 5. Términos y Condiciones
| Propiedad | Valor |
|-----------|-------|
| **Componente** | `CheckboxComponent` (ya existe ✅) |
| **Texto** | "Acepto los **Términos y Condiciones** y la **Política de Privacidad**" |
| **Links** | Términos y Condiciones, Política de Privacidad (clickeables) |
| **Obligatorio** | Sí (debe estar marcado para registrar) |

---

## 🎨 Estilos

### Colores
- **Background**: Gradiente GoodLife (igual que Login)
- **Inputs**: Borde con gradiente (igual que Login)
- **Botón principal**: Gradiente horizontal DarkGreen → LightGreen
- **Textos**: TextPrimary, TextSecondary, TextLink (ya definidos)
- **Checkbox checked**: LightGreen
- **Links**: TextLink con underline

### Tipografía
- **Título**: `GoodLifeTypography.headlineMedium` con gradiente
- **Subtítulo**: `GoodLifeTypography.bodyMedium`, color TextSecondary
- **Inputs**: `GoodLifeTypography.bodyMedium`
- **Botón**: `GoodLifeTypography.labelLarge`
- **Link inferior**: `GoodLifeTypography.bodySmall`

---

## 🔄 Estados UI

```kotlin
@Stable
sealed interface RegisterUiState {
    data object Loading : RegisterUiState
    
    data class Content(
        val fullName: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val acceptedTerms: Boolean = false,
        val isFullNameError: Boolean = false,
        val fullNameErrorMessage: String? = null,
        val isEmailError: Boolean = false,
        val emailErrorMessage: String? = null,
        val isPasswordError: Boolean = false,
        val passwordErrorMessage: String? = null,
        val isConfirmPasswordError: Boolean = false,
        val confirmPasswordErrorMessage: String? = null,
        val passwordsMatch: Boolean = true,
        val isLoading: Boolean = false,  // Para el botón con spinner
        val errorMessage: String? = null  // Error general del servidor
    ) : RegisterUiState
    
    data object Success : RegisterUiState
}
```

---

## 🎬 Acciones UI

```kotlin
@Stable
sealed interface RegisterUiAction {
    // Cambios de campos
    data class OnFullNameChange(val value: String) : RegisterUiAction
    data class OnEmailChange(val value: String) : RegisterUiAction
    data class OnPasswordChange(val value: String) : RegisterUiAction
    data class OnConfirmPasswordChange(val value: String) : RegisterUiAction
    
    // Toggles
    data object OnTermsToggle : RegisterUiAction
    data object OnPasswordVisibilityToggle : RegisterUiAction
    data object OnConfirmPasswordVisibilityToggle : RegisterUiAction
    
    // Acciones principales
    data object OnRegisterClick : RegisterUiAction
    data object OnLoginClick : RegisterUiAction
    
    // Links
    data object OnTermsClick : RegisterUiAction
    data object OnPrivacyClick : RegisterUiAction
    
    // Error handling
    data object OnDismissError : RegisterUiAction
}
```

---

## 📦 Archivos a Crear

### Estructura de Carpetas
```
presentation/screen/register/
├── RegisterScreen.kt           # UI pura (RegisterContent)
├── RegisterScreenOwner.kt      # Composable con ViewModel
├── RegisterViewModel.kt        # Lógica de presentación
└── model/
    ├── RegisterUiState.kt      # Estados UI
    └── RegisterUiAction.kt     # Acciones UI

domain/usecase/auth/
├── ValidateFullNameUseCase.kt      # NUEVO: Validar nombre
├── ValidatePasswordMatchUseCase.kt # NUEVO: Validar que coincidan
└── RegisterUseCase.kt              # NUEVO: Orquesta registro

data/remote/dto/request/
└── RegisterRequest.kt          # (ya existe ✅)

data/remote/dto/response/
└── RegisterResponse.kt         # (ya existe ✅)
```

---

## 🔌 Integración con Backend

### Endpoint
```http
POST /api/v1/register
Content-Type: application/json
```

### Request Body
```json
{
  "username": "agustin123",
  "email": "agustin@example.com",
  "password": "securePassword123"
}
```

### Response (200)
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "agustin123",
    "email": "agustin@example.com",
    "createdAt": "2026-01-09T10:00:00Z"
  }
}
```

### Response Error (400)
```json
{
  "code": 400,
  "message": "El email ya está registrado"
}
```

---

## 🧪 Validaciones

### En Tiempo Real (mientras escribe)
| Campo | Validación | Mensaje de Error |
|-------|------------|------------------|
| Nombre | Mínimo 2 caracteres | "El nombre es muy corto" |
| Email | Formato válido | "Ingresa un email válido" |
| Contraseña | Mínimo 6 caracteres | "Mínimo 6 caracteres" |
| Confirmar | Coincide con contraseña | "Las contraseñas no coinciden" |

### Al Presionar Registrar
1. Validar todos los campos (UseCases)
2. Verificar checkbox de términos marcado
3. Mostrar loading en botón (`isLoading = true`)
4. Llamar al backend via `RegisterUseCase`
5. Si éxito → Navegar a Login
6. Si error → Mostrar mensaje del backend

---

## 🧭 Navegación

### Flujo
```
[Register] ─── OnRegisterClick (éxito) ───→ [Login]
     │
     └─── OnLoginClick ─────────────────→ [Login] (back)
```

### Extensiones de Navegación
```kotlin
// En NavigationExtensions.kt
fun ComposeNavigationController.navigateToLoginFromRegister() {
    navigateTo(
        route = AppRoute.Login,
        navOptions = navOptions {
            popUpTo<AppRoute.Register> { inclusive = true }
            launchSingleTop = true
        }
    )
}
```

---

## ✅ Checklist de Implementación

### UI
- [ ] Crear `RegisterUiState.kt`
- [ ] Crear `RegisterUiAction.kt`
- [ ] Crear `RegisterScreen.kt` (RegisterContent + RegisterScreenPreview)
- [ ] Crear `RegisterScreenOwner.kt`
- [ ] Crear `RegisterViewModel.kt`
- [ ] Agregar ruta en `AppGraph.kt`
- [ ] Agregar `navigateToLoginFromRegister()` en `NavigationExtensions.kt`

### Domain
- [ ] Crear `ValidateFullNameUseCase.kt`
- [ ] Crear `ValidatePasswordMatchUseCase.kt`
- [ ] Crear `RegisterUseCase.kt`

### Data
- [ ] Verificar `RegisterRequest.kt` (ya existe)
- [ ] Verificar `RegisterResponse.kt` (ya existe)
- [ ] Agregar `register()` en `AuthRemoteDataSource` (si no existe)
- [ ] Agregar `register()` en `AuthRepository` (si no existe)

### DI
- [ ] Registrar `RegisterViewModel` en `AppModule`
- [ ] Registrar nuevos UseCases en `AppModule`

### Testing
- [ ] Preview de cada estado (Empty, Filled, Errors, Loading)
- [ ] Validar errores visuales con InputBorderColors
- [ ] Probar con backend real

---

## 📱 Previews Requeridos

```kotlin
@Preview fun RegisterScreenEmptyPreview()
@Preview fun RegisterScreenFilledPreview()
@Preview fun RegisterScreenWithErrorsPreview()
@Preview fun RegisterScreenLoadingPreview()
```

---

## 🔗 Componentes Reutilizados

| Componente | Uso |
|------------|-----|
| `TextFieldComponent` | Inputs de nombre, email, contraseñas |
| `ButtonComponent` | Botón "Registrarme" con `isLoading` |
| `CheckboxComponent` | Aceptar términos |
| `TitleComponent` | Título "Crear Cuenta" con gradiente |
| `BackgroundGradient` | Fondo de pantalla |

---

**Creado por:** Android Team  
**Última actualización:** 2026-01-20  
**Próximo paso:** Ver SPEC-003-plan-implementation-register.md
