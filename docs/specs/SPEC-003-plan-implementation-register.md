# SPEC-003: Plan de Implementación - Pantalla de Registro

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-003 |
| **Tipo** | Implementation Plan |
| **Prioridad** | Alta |
| **Referencia** | SPEC-001-register-screen.md |
| **Fecha** | 2026-01-20 |
| **Tiempo Estimado** | 2-3 horas |

---

## 🎯 Objetivo

Guía paso a paso para implementar la pantalla de registro siguiendo Clean Architecture, MVVM y preparación para KMP.

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

### Fase 5: Navigation

```
📁 presentation/navigation/
├── route/AppGraph.kt               ← ACTUALIZAR
└── route/NavigationExtensions.kt   ← ACTUALIZAR
```

### Fase 6: DI

```
📁 di/
└── AppModule.kt                    ← ACTUALIZAR
```

---

## 📝 Paso a Paso Detallado

### PASO 1: ValidateFullNameUseCase.kt

**Ubicación:** `domain/usecase/auth/ValidateFullNameUseCase.kt`

```kotlin
package com.agusstkd.goodlife.domain.usecase.auth

import com.agusstkd.goodlife.domain.model.ValidationResult

/**
 * Valida el nombre completo del usuario.
 * 
 * Reglas:
 * - Mínimo 2 caracteres
 * - Solo letras y espacios
 */
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

/**
 * Valida que las contraseñas coincidan.
 */
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

/**
 * Orquesta el registro de usuario.
 * 
 * 1. Valida nombre
 * 2. Valida email
 * 3. Valida contraseña
 * 4. Valida que coincidan las contraseñas
 * 5. Llama al repositorio
 */
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
        
        // Validar nombre
        val nameResult = validateFullName(fullName)
        if (!nameResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.FULL_NAME,
                nameResult.errorMessage ?: "Nombre inválido"
            )
        }
        
        // Validar email
        val emailResult = validateEmail(email)
        if (!emailResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.EMAIL,
                emailResult.errorMessage ?: "Email inválido"
            )
        }
        
        // Validar contraseña
        val passwordResult = validatePassword(password)
        if (!passwordResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.PASSWORD,
                passwordResult.errorMessage ?: "Contraseña inválida"
            )
        }
        
        // Validar que coincidan
        val matchResult = validatePasswordMatch(password, confirmPassword)
        if (!matchResult.successful) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.CONFIRM_PASSWORD,
                matchResult.errorMessage ?: "Las contraseñas no coinciden"
            )
        }
        
        // Llamar al repositorio
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

### PASO 4: RegisterUiState.kt

**Ubicación:** `presentation/screen/register/model/RegisterUiState.kt`

```kotlin
package com.agusstkd.goodlife.presentation.screen.register.model

import androidx.compose.runtime.Stable

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

### PASO 5: RegisterUiAction.kt

**Ubicación:** `presentation/screen/register/model/RegisterUiAction.kt`

```kotlin
package com.agusstkd.goodlife.presentation.screen.register.model

import androidx.compose.runtime.Stable

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

### PASO 6: RegisterViewModel.kt

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
        _uiState.value = current.copy(
            fullName = value,
            isFullNameError = false,
            fullNameErrorMessage = null
        )
    }

    private fun updateEmail(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            email = value,
            isEmailError = false,
            emailErrorMessage = null
        )
    }

    private fun updatePassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            password = value,
            isPasswordError = false,
            passwordErrorMessage = null
        )
    }

    private fun updateConfirmPassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            confirmPassword = value,
            isConfirmPasswordError = false,
            confirmPasswordErrorMessage = null
        )
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
        
        // Verificar términos aceptados
        if (!current.acceptedTerms) {
            _uiState.value = current.copy(
                errorMessage = "Debes aceptar los términos y condiciones"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)

            when (val result = registerUseCase(
                fullName = current.fullName,
                email = current.email,
                password = current.password,
                confirmPassword = current.confirmPassword
            )) {
                is RegisterUseCase.RegisterResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    navigationController.navigateToLoginFromRegister()
                }
                
                is RegisterUseCase.RegisterResult.ValidationError -> {
                    _uiState.value = when (result.field) {
                        RegisterUseCase.RegisterResult.Field.FULL_NAME -> current.copy(
                            isLoading = false,
                            isFullNameError = true,
                            fullNameErrorMessage = result.message
                        )
                        RegisterUseCase.RegisterResult.Field.EMAIL -> current.copy(
                            isLoading = false,
                            isEmailError = true,
                            emailErrorMessage = result.message
                        )
                        RegisterUseCase.RegisterResult.Field.PASSWORD -> current.copy(
                            isLoading = false,
                            isPasswordError = true,
                            passwordErrorMessage = result.message
                        )
                        RegisterUseCase.RegisterResult.Field.CONFIRM_PASSWORD -> current.copy(
                            isLoading = false,
                            isConfirmPasswordError = true,
                            confirmPasswordErrorMessage = result.message
                        )
                    }
                }
                
                is RegisterUseCase.RegisterResult.ServerError -> {
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
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

### PASO 7: RegisterScreen.kt

**Ubicación:** `presentation/screen/register/RegisterScreen.kt`

```kotlin
package com.agusstkd.goodlife.presentation.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.components.*
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (RegisterUiAction) -> Unit
) {
    when (uiState) {
        is RegisterUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is RegisterUiState.Content -> {
            RegisterContent(
                state = uiState,
                onAction = onAction
            )
        }
        
        is RegisterUiState.Success -> {
            // Navegación manejada en ViewModel
        }
    }
}

@Composable
private fun RegisterContent(
    state: RegisterUiState.Content,
    onAction: (RegisterUiAction) -> Unit
) {
    BackgroundGradient {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo pequeño (opcional)
            // Image(...)
            
            // Título
            TitleComponent(
                params = TitleParams(
                    title = "Crear Cuenta",
                    subtitle = "Crea tu cuenta para empezar tu vida saludable"
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Campo: Nombre completo
            TextFieldComponent(
                params = TextFieldParams(
                    type = TextFieldType.USER,
                    value = state.fullName,
                    placeholder = "Nombre completo",
                    isError = state.isFullNameError,
                    errorMessage = state.fullNameErrorMessage
                ),
                onValueChange = { onAction(RegisterUiAction.OnFullNameChange(it)) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo: Email
            TextFieldComponent(
                params = TextFieldParams(
                    type = TextFieldType.EMAIL,
                    value = state.email,
                    placeholder = "Correo electrónico",
                    isError = state.isEmailError,
                    errorMessage = state.emailErrorMessage
                ),
                onValueChange = { onAction(RegisterUiAction.OnEmailChange(it)) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo: Contraseña
            TextFieldComponent(
                params = TextFieldParams(
                    type = TextFieldType.PASSWORD,
                    value = state.password,
                    placeholder = "Contraseña",
                    isPasswordVisible = state.isPasswordVisible,
                    isError = state.isPasswordError,
                    errorMessage = state.passwordErrorMessage
                ),
                onValueChange = { onAction(RegisterUiAction.OnPasswordChange(it)) },
                onPasswordVisibilityToggle = { onAction(RegisterUiAction.OnPasswordVisibilityToggle) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo: Confirmar contraseña
            TextFieldComponent(
                params = TextFieldParams(
                    type = TextFieldType.PASSWORD,
                    value = state.confirmPassword,
                    placeholder = "Confirmar contraseña",
                    isPasswordVisible = state.isConfirmPasswordVisible,
                    isError = state.isConfirmPasswordError,
                    errorMessage = state.confirmPasswordErrorMessage
                ),
                onValueChange = { onAction(RegisterUiAction.OnConfirmPasswordChange(it)) },
                onPasswordVisibilityToggle = { onAction(RegisterUiAction.OnConfirmPasswordVisibilityToggle) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Checkbox: Términos y condiciones
            CheckboxComponent(
                params = CheckboxParams(
                    id = CheckboxParamsId.TERMS,
                    text = "Acepto los Términos y Condiciones",
                    linkText = "Política de Privacidad",
                    enabledState = CheckboxColors.enabledState(),
                    disabledState = CheckboxColors.disabledState()
                ),
                checked = state.acceptedTerms,
                onClick = { onAction(RegisterUiAction.OnTermsToggle) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón: Registrarme
            ButtonComponent(
                params = ButtonParams(
                    text = "Registrarme",
                    isLoading = state.isLoading,
                    enabled = state.acceptedTerms
                ),
                onClick = { onAction(RegisterUiAction.OnRegisterClick) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Link: Ya tengo cuenta
            TextButton(onClick = { onAction(RegisterUiAction.OnLoginClick) }) {
                Text(
                    text = "¿Ya tienes cuenta? Iniciar Sesión",
                    // style = ...
                )
            }
            
            // Snackbar de error
            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Snackbar(
                    action = {
                        TextButton(onClick = { onAction(RegisterUiAction.OnDismissError) }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun RegisterScreenEmptyPreview() {
    GoodLifeTheme {
        RegisterScreen(
            uiState = RegisterUiState.Content(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenFilledPreview() {
    GoodLifeTheme {
        RegisterScreen(
            uiState = RegisterUiState.Content(
                fullName = "Agustin",
                email = "agustin@email.com",
                password = "123456",
                confirmPassword = "123456",
                acceptedTerms = true
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenWithErrorsPreview() {
    GoodLifeTheme {
        RegisterScreen(
            uiState = RegisterUiState.Content(
                fullName = "A",
                email = "invalid",
                password = "123",
                confirmPassword = "456",
                isFullNameError = true,
                fullNameErrorMessage = "El nombre es muy corto",
                isEmailError = true,
                emailErrorMessage = "Ingresa un email válido",
                isPasswordError = true,
                passwordErrorMessage = "Mínimo 6 caracteres",
                isConfirmPasswordError = true,
                confirmPasswordErrorMessage = "Las contraseñas no coinciden"
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenLoadingPreview() {
    GoodLifeTheme {
        RegisterScreen(
            uiState = RegisterUiState.Content(
                fullName = "Agustin",
                email = "agustin@email.com",
                password = "123456",
                confirmPassword = "123456",
                acceptedTerms = true,
                isLoading = true
            ),
            onAction = {}
        )
    }
}
```

---

### PASO 8: RegisterScreenOwner.kt

**Ubicación:** `presentation/screen/register/RegisterScreenOwner.kt`

```kotlin
package com.agusstkd.goodlife.presentation.screen.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenOwner(
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}
```

---

### PASO 9: Actualizar AppGraph.kt

**Ubicación:** `presentation/navigation/route/AppGraph.kt`

```kotlin
// Agregar import
import com.agusstkd.goodlife.presentation.screen.register.RegisterScreenOwner

// Agregar en el NavHost
composable<AppRoute.Register> {
    RegisterScreenOwner()
}
```

---

### PASO 10: Actualizar NavigationExtensions.kt

**Ubicación:** `presentation/navigation/route/NavigationExtensions.kt`

```kotlin
// Agregar función
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

### PASO 11: Actualizar AppModule.kt

**Ubicación:** `di/AppModule.kt`

```kotlin
// Agregar imports
import com.agusstkd.goodlife.domain.usecase.auth.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.auth.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.auth.RegisterUseCase
import com.agusstkd.goodlife.presentation.screen.register.RegisterViewModel

// Agregar en el module
// UseCases
factory { ValidateFullNameUseCase() }
factory { ValidatePasswordMatchUseCase() }
factory { RegisterUseCase(get(), get(), get(), get(), get()) }

// ViewModel
viewModel { RegisterViewModel(get(), get()) }
```

---

## ✅ Checklist Final

### Domain Layer
- [ ] `ValidateFullNameUseCase.kt` creado
- [ ] `ValidatePasswordMatchUseCase.kt` creado
- [ ] `RegisterUseCase.kt` creado

### Presentation Layer
- [ ] `RegisterUiState.kt` creado
- [ ] `RegisterUiAction.kt` creado
- [ ] `RegisterViewModel.kt` creado
- [ ] `RegisterScreen.kt` creado
- [ ] `RegisterScreenOwner.kt` creado

### Navigation
- [ ] `AppGraph.kt` actualizado
- [ ] `NavigationExtensions.kt` actualizado

### DI
- [ ] `AppModule.kt` actualizado con UseCases
- [ ] `AppModule.kt` actualizado con ViewModel

### Testing
- [ ] Previews funcionan correctamente
- [ ] Navegación Login ↔ Register funciona
- [ ] Registro con backend funciona
- [ ] Validaciones en tiempo real funcionan
- [ ] Loading en botón funciona

---

## 🔧 Comandos de Verificación

```bash
# Compilar el proyecto
./gradlew assembleDebug

# Verificar linter
./gradlew lint

# Ejecutar en dispositivo
./gradlew installDebug
```

---

## 📝 Notas Importantes

1. **@Stable en Estados**: Todos los sealed interfaces llevan `@Stable` para optimizar recomposiciones
2. **Patrón Owner**: Separa inyección de dependencias (Owner) de UI pura (Screen)
3. **Reutilización**: Usar componentes existentes (TextFieldComponent, ButtonComponent, etc.)
4. **Mensajes del Backend**: Los errores vienen del `AuthRemoteDataSource`, no hardcodeados
5. **Navegación**: Usar `navigateToLoginFromRegister()` para limpiar el backstack

---

**Creado por:** Android Team  
**Fecha:** 2026-01-20  
**Tiempo estimado:** 2-3 horas
