# SPEC-002: Login con Huella Digital (Biometría)

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-002 |
| **Tipo** | Feature |
| **Prioridad** | Media |
| **Estado** | ✅ COMPLETADO |
| **Fecha Creación** | 2026-01-09 |
| **Fecha Completado** | 2026-01-20 |
| **Librería** | `androidx.biometric:biometric:1.2.0-alpha05` |

---

## 🎯 Objetivo

Permitir a los usuarios iniciar sesión usando su huella digital, Face ID, PIN, patrón o contraseña del dispositivo después de haber iniciado sesión al menos una vez con email/contraseña.

---

## ✅ Funcionalidades Implementadas

### Características Principales
- ✅ **Autenticación biométrica** (huella digital, Face ID)
- ✅ **Métodos alternativos** (PIN, patrón, contraseña del dispositivo)
- ✅ **Auto-completar email** cuando biometría está activada
- ✅ **Prompt automático** al abrir la app si biometría está activada
- ✅ **Prompt manual** al tocar el ícono de huella
- ✅ **Almacenamiento seguro** con EncryptedSharedPreferences
- ✅ **Arquitectura KMP-ready** con interfaces en domain y implementaciones en platform

---

## 🖼️ Diseño Visual Final

### Estructura en Login Screen

```
┌─────────────────────────────────────┐
│           [Logo GoodLife]           │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 👤  Email (auto-completado) │   │ ← Se autocompleta si biometría está activada
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 🔒  Contraseña          👁️ │   │
│  └─────────────────────────────┘   │
│                                     │
│  ☑️ Activar login con huella [👆]  │ ← CheckboxComponent con ícono de huella
│                                     │
│  ┌─────────────────────────────┐   │
│  │          Login →            │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

### BiometricPrompt del Sistema

```
┌─────────────────────────────────────┐
│                                     │
│       Iniciar sesión en GoodLife    │
│                                     │
│    Usa tu huella digital para       │
│           continuar                 │
│                                     │
│            [👆]                     │  ← Sensor de huella
│                                     │
│    ─────────────────────────────    │
│    Usa contraseña del dispositivo   │  ← Permite PIN/patrón/password
│                                     │
└─────────────────────────────────────┘
```

---

## 🏗️ Arquitectura KMP-Ready

### Principio de Diseño

La implementación sigue el **Principio de Inversión de Dependencias (DIP)**:
- Las capas `domain` y `core` contienen **interfaces** (Kotlin puro, sin Android)
- La capa `platform` contiene **implementaciones específicas de Android**
- Koin inyecta las implementaciones concretas en las interfaces

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                              │
│                      (Kotlin Puro - KMP Ready)                   │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────┐  ┌──────────────────────────────┐  │
│  │  BiometricAuthenticator │  │  SecureCredentialsStorage    │  │
│  │       (interface)       │  │        (interface)           │  │
│  ├─────────────────────────┤  ├──────────────────────────────┤  │
│  │ + checkAvailability()   │  │ + saveCredentials()          │  │
│  │ + authenticate()        │  │ + getCredentials()           │  │
│  └─────────────────────────┘  │ + getSavedEmail()            │  │
│                               │ + setBiometricEnabled()      │  │
│  ┌─────────────────────────┐  │ + isBiometricEnabled()       │  │
│  │  BiometricPromptConfig  │  │ + hasCredentials()           │  │
│  │      (data class)       │  │ + clearCredentials()         │  │
│  └─────────────────────────┘  └──────────────────────────────┘  │
│                                                                  │
│  ┌─────────────────────────┐  ┌──────────────────────────────┐  │
│  │  BiometricAvailability  │  │     BiometricResult          │  │
│  │   (sealed interface)    │  │    (sealed interface)        │  │
│  └─────────────────────────┘  └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ implements
                                │
┌─────────────────────────────────────────────────────────────────┐
│                       PLATFORM LAYER                             │
│                  (Android Específico)                            │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────┐  ┌─────────────────────────┐  │
│  │ AndroidBiometricAuthenticator│  │AndroidSecureCredentials │  │
│  │        (class)               │  │      Storage            │  │
│  ├──────────────────────────────┤  ├─────────────────────────┤  │
│  │ - BiometricManager           │  │ - EncryptedShared       │  │
│  │ - BiometricPrompt            │  │   Preferences           │  │
│  │ - FragmentActivity           │  │ - MasterKey             │  │
│  └──────────────────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                ▲
                                │ provides
                                │
┌─────────────────────────────────────────────────────────────────┐
│                        DI LAYER (Koin)                           │
├─────────────────────────────────────────────────────────────────┤
│  biometricModule:                                                │
│    single<BiometricAuthenticator> { AndroidBiometricAuth... }   │
│    single<SecureCredentialsStorage> { AndroidSecureCred... }    │
└─────────────────────────────────────────────────────────────────┘
```

### Estructura de Archivos Final

```
domain/
├── biometric/
│   ├── BiometricAuthenticator.kt      # Interface para autenticación
│   ├── BiometricPromptConfig.kt       # Configuración del prompt
│   ├── BiometricAvailability.kt       # Estados de disponibilidad
│   └── BiometricResult.kt             # Resultados de autenticación
└── storage/
    └── SecureCredentialsStorage.kt    # Interface para credenciales

platform/
├── biometric/
│   └── AndroidBiometricAuthenticator.kt  # Impl. Android de BiometricAuthenticator
└── storage/
    └── AndroidSecureCredentialsStorage.kt # Impl. Android de SecureCredentialsStorage

di/
└── BiometricModule.kt                 # Módulo Koin para biometría

presentation/screen/login/
├── LoginScreenOwner.kt                # Maneja BiometricPrompt y Activity
├── LoginViewModel.kt                  # Lógica de biometría
├── LoginScreen.kt                     # UI con CheckboxComponent
└── model/
    ├── LoginUiState.kt                # Estados con campos de biometría
    └── LoginUiAction.kt               # Acciones de biometría
```

---

## 🔄 Flujos de Usuario Implementados

### Flujo 1: Primer Login + Activar Huella

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    Login     │    │   Ingresa    │    │   Activa     │
│   Screen     │───►│  Email/Pass  │───►│  Checkbox    │
└──────────────┘    └──────────────┘    │   Huella     │
                                         └──────┬───────┘
                                                │
                    ┌──────────────┐            │
                    │   Guarda     │◄───────────┘
                    │ Credenciales │  (al hacer login exitoso)
                    │  Encriptadas │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │    Home      │
                    └──────────────┘
```

### Flujo 2: Login Posterior con Huella Activada

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    Splash    │───►│    Login     │───►│  Biometric   │ (automático)
│              │    │   Screen     │    │   Prompt     │
└──────────────┘    │ (email auto- │    └──────┬───────┘
                    │  completado) │           │
                    └──────────────┘           │
                           ┌────────────────────┼────────────────────┐
                           │                    │                    │
                    ┌──────▼───────┐     ┌──────▼───────┐     ┌──────▼───────┐
                    │   Éxito      │     │   Cancelar   │     │   Error      │
                    │  (Login OK)  │     │  (Solo falta │     │  (Reintentar │
                    └──────┬───────┘     │  password)   │     │   o form)    │
                           │             └──────────────┘     └──────────────┘
                    ┌──────▼───────┐
                    │    Home      │
                    └──────────────┘
```

### Flujo 3: Usar Contraseña del Dispositivo

```
BiometricPrompt → "Usar contraseña" → Aparece PIN/Patrón/Password del dispositivo
```

---

## 📦 Código Final de Interfaces (Domain)

### BiometricAuthenticator.kt

```kotlin
package com.agusstkd.goodlife.domain.biometric

/**
 * Interfaz para la autenticación biométrica.
 * Define el contrato para las operaciones de biometría,
 * permitiendo diferentes implementaciones por plataforma (Android, iOS, etc.).
 */
interface BiometricAuthenticator {
    /**
     * Verifica la disponibilidad de biometría en el dispositivo.
     */
    fun checkAvailability(): BiometricAvailability

    /**
     * Inicia el flujo de autenticación biométrica.
     *
     * @param config Configuración del prompt (títulos, etc.).
     * @param onResult Callback con el resultado de la autenticación.
     */
    fun authenticate(
        config: BiometricPromptConfig,
        onResult: (BiometricResult) -> Unit
    )
}
```

### BiometricPromptConfig.kt

```kotlin
package com.agusstkd.goodlife.domain.biometric

/**
 * Configuración para el prompt de autenticación biométrica.
 * Contiene los textos que se mostrarán al usuario.
 */
data class BiometricPromptConfig(
    val title: String,
    val subtitle: String,
    val negativeButtonText: String
)
```

### SecureCredentialsStorage.kt

```kotlin
package com.agusstkd.goodlife.domain.storage

/**
 * Interfaz para el almacenamiento seguro de credenciales.
 * Define el contrato para guardar y recuperar credenciales de forma segura,
 * permitiendo diferentes implementaciones por plataforma.
 */
interface SecureCredentialsStorage {
    fun saveCredentials(email: String, password: String)
    fun getCredentials(): Pair<String, String>?
    fun getSavedEmail(): String?
    fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabled(): Boolean
    fun hasCredentials(): Boolean
    fun clearCredentials()
}
```

---

## 📱 Código Final de Implementaciones (Platform)

### AndroidBiometricAuthenticator.kt

```kotlin
package com.agusstkd.goodlife.platform.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.agusstkd.goodlife.domain.biometric.*

class AndroidBiometricAuthenticator(
    private val context: Context
) : BiometricAuthenticator {

    private val biometricManager = BiometricManager.from(context)
    private var currentActivity: FragmentActivity? = null

    /** Establece la FragmentActivity (específico de Android) */
    fun setActivity(activity: FragmentActivity?) {
        this.currentActivity = activity
    }

    override fun checkAvailability(): BiometricAvailability {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NoBiometricEnrolled
            else -> BiometricAvailability.Unknown
        }
    }

    override fun authenticate(
        config: BiometricPromptConfig,
        onResult: (BiometricResult) -> Unit
    ) {
        val activity = currentActivity
            ?: return onResult(BiometricResult.Error(-1, "FragmentActivity no disponible"))

        val executor = ContextCompat.getMainExecutor(context)
        
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val error = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.Cancelled
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.Lockout(errString.toString())
                    else -> BiometricResult.Error(errorCode, errString.toString())
                }
                onResult(error)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onResult(BiometricResult.Failed)
            }
        }

        // Permite biometría + PIN/Patrón/Password del dispositivo
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(config.title)
            .setSubtitle(config.subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }
}
```

### AndroidSecureCredentialsStorage.kt

```kotlin
package com.agusstkd.goodlife.platform.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage

class AndroidSecureCredentialsStorage(context: Context) : SecureCredentialsStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREFS_NAME = "goodlife_secure_credentials"
        private const val KEY_EMAIL = "biometric_email"
        private const val KEY_PASSWORD = "biometric_password"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    override fun saveCredentials(email: String, password: String) {
        prefs.edit {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
        }
    }

    override fun getCredentials(): Pair<String, String>? {
        val email = prefs.getString(KEY_EMAIL, null)
        val password = prefs.getString(KEY_PASSWORD, null)
        return if (email != null && password != null) Pair(email, password) else null
    }

    override fun getSavedEmail(): String? = prefs.getString(KEY_EMAIL, null)

    override fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_BIOMETRIC_ENABLED, enabled) }
    }

    override fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    override fun hasCredentials(): Boolean = getCredentials() != null

    override fun clearCredentials() {
        prefs.edit { clear() }
    }
}
```

---

## 📦 Módulo Koin (BiometricModule.kt)

```kotlin
package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import com.agusstkd.goodlife.platform.biometric.AndroidBiometricAuthenticator
import com.agusstkd.goodlife.platform.storage.AndroidSecureCredentialsStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val biometricModule = module {
    single<BiometricAuthenticator> { AndroidBiometricAuthenticator(androidContext()) }
    single<SecureCredentialsStorage> { AndroidSecureCredentialsStorage(androidContext()) }
}
```

---

## 🔧 LoginScreenOwner - Manejo del BiometricPrompt

```kotlin
@Composable
fun LoginScreenOwner(viewModel: LoginViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Obtener Activity usando extension function
    val activity = context.findActivity()
    
    // Obtener BiometricAuthenticator de Koin
    val biometricAuthenticator: BiometricAuthenticator = koinInject()
    
    // Strings localizados
    val biometricTitle = stringResource(R.string.biometric_prompt_title)
    val biometricSubtitle = stringResource(R.string.biometric_prompt_subtitle)

    // Pasar Activity al authenticator (específico de Android)
    LaunchedEffect(activity) {
        (biometricAuthenticator as? AndroidBiometricAuthenticator)?.setActivity(activity)
    }

    // Mostrar BiometricPrompt cuando sea necesario
    LaunchedEffect(uiState) {
        val content = uiState as? LoginUiState.Content ?: return@LaunchedEffect
        
        if (content.shouldShowBiometricPrompt && activity != null) {
            biometricAuthenticator.authenticate(
                config = BiometricPromptConfig(
                    title = biometricTitle,
                    subtitle = biometricSubtitle,
                    negativeButtonText = ""  // Se ignora con DEVICE_CREDENTIAL
                ),
                onResult = { result ->
                    viewModel.onAction(LoginUiAction.OnBiometricResult(result))
                }
            )
        }
    }

    LoginScreen(uiState = uiState, onAction = viewModel::onAction)
}

// Extension para obtener FragmentActivity desde Context
private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}
```

---

## 📦 Dependencias

```kotlin
// build.gradle.kts (app)
dependencies {
    // Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    
    // Encrypted SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

---

## 🔒 Consideraciones de Seguridad

1. **Credenciales encriptadas**: `EncryptedSharedPreferences` con AES256_GCM
2. **Fallback**: Siempre permite login con email/password
3. **Métodos alternativos**: PIN, patrón, contraseña del dispositivo
4. **Logout**: Limpiar credenciales en `clearCredentials()`
5. **Lockout**: Manejo de demasiados intentos fallidos

---

## 📱 Compatibilidad

| Android Version | Soporte |
|-----------------|---------|
| API 23-27 | Solo huella digital |
| API 28+ | Huella + Face ID (según hardware) |
| API 30+ | BiometricPrompt mejorado |

---

## ✅ Checklist de Implementación

### Core/Domain ✅
- [x] Crear `BiometricAvailability.kt` (sealed interface)
- [x] Crear `BiometricResult.kt` (sealed interface)
- [x] Crear `BiometricAuthenticator.kt` (interface)
- [x] Crear `BiometricPromptConfig.kt` (data class)
- [x] Crear `SecureCredentialsStorage.kt` (interface)

### Platform ✅
- [x] Crear `AndroidBiometricAuthenticator.kt`
- [x] Crear `AndroidSecureCredentialsStorage.kt`

### Presentation ✅
- [x] Actualizar `LoginUiState.kt` con campos biometría
- [x] Actualizar `LoginUiAction.kt` con acciones biometría
- [x] Agregar `CheckboxComponent` para biometría a `LoginScreen`
- [x] Actualizar `LoginViewModel` con lógica biometría
- [x] Actualizar `LoginScreenOwner` para mostrar prompt

### DI ✅
- [x] Crear `BiometricModule.kt`
- [x] Registrar `BiometricAuthenticator` → `AndroidBiometricAuthenticator`
- [x] Registrar `SecureCredentialsStorage` → `AndroidSecureCredentialsStorage`

### Testing ✅
- [x] Verificar en dispositivo con huella
- [x] Probar PIN/patrón/password como alternativa
- [x] Probar flujo completo: login → activar → cerrar app → abrir → biometric prompt
- [x] Verificar auto-completar email

---

## 🔑 Mantenimiento Futuro

### Para agregar soporte iOS (KMP):

1. Crear `IOSBiometricAuthenticator` en el módulo iOS
2. Usar `LAContext` de LocalAuthentication framework
3. Crear `IOSSecureCredentialsStorage` usando Keychain
4. Configurar Koin para inyectar implementaciones por plataforma

### Para agregar nuevos métodos de autenticación:

1. Extender `BiometricResult` con nuevos casos
2. Modificar `AndroidBiometricAuthenticator.authenticate()`
3. Actualizar `LoginViewModel.handleBiometricResult()`

---

**Creado por:** Android Team  
**Completado:** 2026-01-20  
**Arquitectura:** KMP-Ready con interfaces en Domain
