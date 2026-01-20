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
| **Tiempo Estimado** | 2-3 horas |

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
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isFullNameError: Boolean = false,
        val fullNameErrorMessage: String? = null,
        val isEmailError: Boolean = false,
        val emailErrorMessage: String? = null,
        val isPasswordError: Boolean = false,
        val passwordErrorMessage: String? = null,
        val isConfirmPasswordError: Boolean = false,
        val confirmPasswordErrorMessage: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
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

## 🔗 Componentes Reutilizados

| Componente | Uso |
|------------|-----|
| `TextFieldComponent` | Inputs de nombre, email, contraseñas |
| `ButtonComponent` | Botón "Registrarme" con `isLoading` |
| `CheckboxComponent` | Aceptar términos |
| `TitleComponent` | Título "Crear Cuenta" con gradiente |
| `BackgroundGradient` | Fondo de pantalla |

---

## 📋 Pre-requisitos

Antes de empezar, verificar que estos archivos ya existen:

| Archivo | Estado |
|---------|--------|
| `RegisterRequest.kt` | ✅ Existe en `data/remote/dto/request/` |
| `RegisterResponse.kt` | ✅ Existe en `data/remote/dto/response/` |
| `ValidateEmailUseCase.kt` | ✅ Existe en `domain/usecase/auth/` |
| `ValidatePasswordUseCase.kt` | ✅ Existe en `domain/usecase/auth/` |
| `GoodLifeApiService.kt` | ✅ Con endpoint `register()` |
| `AuthRemoteDataSource.kt` | ✅ Con método `register()` |
| `AuthRepository.kt` | ✅ Con método `register()` |
| `TextFieldComponent.kt` | ✅ Componente reutilizable |
| `ButtonComponent.kt` | ✅ Con soporte `isLoading` |
| `CheckboxComponent.kt` | ✅ Componente reutilizable |

---

## 🗂️ Orden de Implementación

### Fase 1: Domain Layer (UseCases)
```
📁 domain/usecase/auth/
├── ValidateFullNameUseCase.kt      ← NUEVO
├── ValidatePasswordMatchUseCase.kt ← NUEVO
└── RegisterUseCase.kt              ← NUEVO
```

### Fase 2: Presentation Layer (Models)
```
📁 presentation/screen/register/model/
├── RegisterUiState.kt              ← NUEVO
└── RegisterUiAction.kt             ← NUEVO
```

### Fase 3: Presentation Layer (ViewModel)
```
📁 presentation/screen/register/
└── RegisterViewModel.kt            ← NUEVO
```

### Fase 4: Presentation Layer (UI)
```
📁 presentation/screen/register/
├── RegisterScreen.kt               ← NUEVO
└── RegisterScreenOwner.kt          ← NUEVO
```

### Fase 5: Navigation & DI
```
📁 presentation/navigation/route/
├── AppGraph.kt                     ← ACTUALIZAR
└── NavigationExtensions.kt         ← ACTUALIZAR

📁 di/
└── AppModule.kt                    ← ACTUALIZAR
```

---

## 📝 Código de Implementación

### PASO 1: ValidateFullNameUseCase.kt

**Ubicación:** `domain/usecase/auth/ValidateFullNameUseCase.kt`

```kotlin
package com.agusstkd.goodlife.domain.usecase.auth

import com.agusstkd.goodlife.domain.model.ValidationResult

class ValidateFullNameUseCase {
    
    operator fun invoke(fullName: String): ValidationResult {
        if (fullName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre no puede estar vacío"
            )
        }
        
        if (fullName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre es muy corto"
            )
        }
        
        if (!fullName.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+\$"))) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre solo puede contener letras"
            )
        }
        
        return ValidationResult(successful = true)
    }
}
```

---

### PASO 2: ValidatePasswordMatchUseCase.kt

**Ubicación:** `domain/usecase/auth/ValidatePasswordMatchUseCase.kt`

```kotlin
package com.agusstkd.goodlife.domain.usecase.auth

import com.agusstkd.goodlife.domain.model.ValidationResult

class ValidatePasswordMatchUseCase {
    
    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        if (password != confirmPassword) {
            return ValidationResult(
                successful = false,
                errorMessage = "Las contraseñas no coinciden"
            )
        }
        
        return ValidationResult(successful = true)
    }
}
```

---

### PASO 3: RegisterUseCase.kt

**Ubicación:** `domain/usecase/auth/RegisterUseCase.kt`

```kotlin
package com.agusstkd.goodlife.domain.usecase.auth

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User
import com.agusstkd.goodlife.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val validateFullName: ValidateFullNameUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val validatePasswordMatch: ValidatePasswordMatchUseCase
) {
    
    sealed interface RegisterResult {
        data class Success(val user: User) : RegisterResult
        data class ValidationError(val field: Field, val message: String) : RegisterResult
        data class ServerError(val message: String) : RegisterResult
        
        enum class Field { FULL_NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }
    }
    
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterResult {
        
        val nameResult = validateFullName(fullName)
        if (!nameResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.FULL_NAME,
                nameResult.errorMessage ?: "Nombre inválido"
            )
        }
        
        val emailResult = validateEmail(email)
        if (!emailResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.EMAIL,
                emailResult.errorMessage ?: "Email inválido"
            )
        }
        
        val passwordResult = validatePassword(password)
        if (!passwordResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.PASSWORD,
                passwordResult.errorMessage ?: "Contraseña inválida"
            )
        }
        
        val matchResult = validatePasswordMatch(password, confirmPassword)
        if (!matchResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.CONFIRM_PASSWORD,
                matchResult.errorMessage ?: "Las contraseñas no coinciden"
            )
        }
        
        return when (val result = authRepository.register(fullName, email, password)) {
            is Result.Success -> RegisterResult.Success(result.data)
            is Result.Error -> RegisterResult.ServerError(
                result.exception.message ?: "Error al registrar"
            )
            is Result.Loading -> RegisterResult.ServerError("Operación en curso")
        }
    }
}
```

---

### PASO 4: RegisterViewModel.kt

**Ubicación:** `presentation/screen/register/RegisterViewModel.kt`

```kotlin
package com.agusstkd.goodlife.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.domain.usecase.auth.RegisterUseCase
import com.agusstkd.goodlife.presentation.navigation.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLoginFromRegister
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Content())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnFullNameChange -> updateFullName(action.value)
            is RegisterUiAction.OnEmailChange -> updateEmail(action.value)
            is RegisterUiAction.OnPasswordChange -> updatePassword(action.value)
            is RegisterUiAction.OnConfirmPasswordChange -> updateConfirmPassword(action.value)
            is RegisterUiAction.OnTermsToggle -> toggleTerms()
            is RegisterUiAction.OnPasswordVisibilityToggle -> togglePasswordVisibility()
            is RegisterUiAction.OnConfirmPasswordVisibilityToggle -> toggleConfirmPasswordVisibility()
            is RegisterUiAction.OnRegisterClick -> performRegister()
            is RegisterUiAction.OnLoginClick -> navigateToLogin()
            is RegisterUiAction.OnDismissError -> dismissError()
        }
    }

    private fun updateFullName(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(fullName = value, isFullNameError = false, fullNameErrorMessage = null)
    }

    private fun updateEmail(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(email = value, isEmailError = false, emailErrorMessage = null)
    }

    private fun updatePassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(password = value, isPasswordError = false, passwordErrorMessage = null)
    }

    private fun updateConfirmPassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(confirmPassword = value, isConfirmPasswordError = false, confirmPasswordErrorMessage = null)
    }

    private fun toggleTerms() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(acceptedTerms = !current.acceptedTerms)
    }

    private fun togglePasswordVisibility() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(isPasswordVisible = !current.isPasswordVisible)
    }

    private fun toggleConfirmPasswordVisibility() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(isConfirmPasswordVisible = !current.isConfirmPasswordVisible)
    }

    private fun performRegister() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        
        if (!current.acceptedTerms) {
            _uiState.value = current.copy(errorMessage = "Debes aceptar los términos y condiciones")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)

            when (val result = registerUseCase(current.fullName, current.email, current.password, current.confirmPassword)) {
                is RegisterUseCase.RegisterResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    navigationController.navigateToLoginFromRegister()
                }
                is RegisterUseCase.RegisterResult.ValidationError -> {
                    _uiState.value = when (result.field) {
                        RegisterUseCase.RegisterResult.Field.FULL_NAME -> current.copy(isLoading = false, isFullNameError = true, fullNameErrorMessage = result.message)
                        RegisterUseCase.RegisterResult.Field.EMAIL -> current.copy(isLoading = false, isEmailError = true, emailErrorMessage = result.message)
                        RegisterUseCase.RegisterResult.Field.PASSWORD -> current.copy(isLoading = false, isPasswordError = true, passwordErrorMessage = result.message)
                        RegisterUseCase.RegisterResult.Field.CONFIRM_PASSWORD -> current.copy(isLoading = false, isConfirmPasswordError = true, confirmPasswordErrorMessage = result.message)
                    }
                }
                is RegisterUseCase.RegisterResult.ServerError -> {
                    _uiState.value = current.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun navigateToLogin() {
        navigationController.navigateToLoginFromRegister()
    }

    private fun dismissError() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(errorMessage = null)
    }
}
```

---

### PASO 5: Actualizar AppModule.kt

```kotlin
// Agregar imports
import com.agusstkd.goodlife.domain.usecase.auth.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.auth.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.auth.RegisterUseCase
import com.agusstkd.goodlife.presentation.screen.register.RegisterViewModel

// Agregar en el module
factory { ValidateFullNameUseCase() }
factory { ValidatePasswordMatchUseCase() }
factory { RegisterUseCase(get(), get(), get(), get(), get()) }
viewModel { RegisterViewModel(get(), get()) }
```

---

## ✅ Checklist de Implementación

### Domain Layer
- [ ] Crear `ValidateFullNameUseCase.kt`
- [ ] Crear `ValidatePasswordMatchUseCase.kt`
- [ ] Crear `RegisterUseCase.kt`

### Presentation Layer
- [ ] Crear `RegisterUiState.kt`
- [ ] Crear `RegisterUiAction.kt`
- [ ] Crear `RegisterViewModel.kt`
- [ ] Crear `RegisterScreen.kt` (RegisterContent + Previews)
- [ ] Crear `RegisterScreenOwner.kt`

### Navigation
- [ ] Agregar ruta en `AppGraph.kt`
- [ ] Agregar `navigateToLoginFromRegister()` en `NavigationExtensions.kt`

### DI
- [ ] Registrar UseCases en `AppModule`
- [ ] Registrar `RegisterViewModel` en `AppModule`

### Testing
- [ ] Preview de cada estado (Empty, Filled, Errors, Loading)
- [ ] Navegación Login ↔ Register funciona
- [ ] Registro con backend funciona
- [ ] Validaciones en tiempo real funcionan
- [ ] Loading en botón funciona

---

## 📱 Previews Requeridos

```kotlin
@Preview fun RegisterScreenEmptyPreview()
@Preview fun RegisterScreenFilledPreview()
@Preview fun RegisterScreenWithErrorsPreview()
@Preview fun RegisterScreenLoadingPreview()
```

---

## 📝 Notas Importantes

1. **@Stable en Estados**: Todos los sealed interfaces llevan `@Stable`
2. **Patrón Owner**: Separa inyección de dependencias (Owner) de UI pura (Screen)
3. **Reutilización**: Usar componentes existentes (TextFieldComponent, ButtonComponent, etc.)
4. **Mensajes del Backend**: Los errores vienen del `AuthRemoteDataSource`, no hardcodeados
5. **Navegación**: Usar `navigateToLoginFromRegister()` para limpiar el backstack

---

**Creado por:** Android Team  
**Última actualización:** 2026-01-20
