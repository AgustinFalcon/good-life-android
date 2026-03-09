package com.agusstkd.goodlife.domain.auth

import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Encapsula la state machine de autenticación biométrica para Login.
 *
 * Extrae de [com.agusstkd.goodlife.presentation.screen.login.LoginViewModel]
 * toda la lógica biométrica, dejando al ViewModel como orquestador.
 *
 * ## Responsabilidades
 * - Verificar disponibilidad y estado de biometría del dispositivo.
 * - Gestionar el toggle de habilitación biométrica.
 * - Emitir [Event.CredentialsReady] cuando la autenticación biométrica fue exitosa,
 *   para que el ViewModel ejecute el login con las credenciales guardadas.
 *
 * ## Lo que NO hace
 * - No llama a [com.agusstkd.goodlife.domain.usecase.login.LoginUseCase] directamente.
 *   Eso es responsabilidad del ViewModel, que reacciona al evento [Event.CredentialsReady].
 * - No actualiza el [com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState].
 *   El ViewModel es dueño del estado de UI.
 *
 * ## Flujo de autenticación biométrica exitosa
 * ```
 * Usuario toca ícono biométrico
 *     → LoginViewModel.handleBiometricIconClick()
 *     → LoginUiState.Content(shouldShowBiometricPrompt = true)   ← ViewModel
 *     → BiometricPrompt se muestra en pantalla                   ← UI
 *     → BiometricResult.Success
 *     → LoginViewModel.handleBiometricResult(Success)
 *     → BiometricLoginHandler.handleResult(Success)
 *     → Event.CredentialsReady(email, password)                  ← este handler
 *     → LoginViewModel.performLoginWithCredentials(email, pass)   ← ViewModel reacciona
 * ```
 *
 * @param biometricAuthenticator Verifica disponibilidad de biometría en el dispositivo.
 * @param credentialsStorage Almacenamiento seguro de credenciales del usuario.
 */
class BiometricLoginHandler(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val credentialsStorage: SecureCredentialsStorage
) {

    /**
     * Eventos emitidos por este handler hacia el ViewModel.
     */
    sealed interface Event {
        /**
         * La autenticación biométrica fue exitosa y las credenciales guardadas están listas.
         * El ViewModel debe llamar a [LoginUseCase] con estos datos.
         *
         * @param email Email recuperado del almacenamiento seguro.
         * @param password Contraseña recuperada del almacenamiento seguro.
         */
        data class CredentialsReady(val email: String, val password: String) : Event
    }

    /**
     * Estado inicial de biometría para poblar [LoginUiState.Content].
     *
     * @param isAvailable Si el hardware biométrico está disponible y configurado.
     * @param isEnabled Si el usuario activó el login biométrico en esta app.
     * @param savedEmail Email guardado para auto-completar el campo, o null.
     * @param shouldShowPrompt Si debe mostrarse el prompt biométrico automáticamente al abrir.
     */
    data class InitialState(
        val isAvailable: Boolean,
        val isEnabled: Boolean,
        val savedEmail: String?,
        val shouldShowPrompt: Boolean
    )

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    /**
     * Flow de eventos que el ViewModel debe colectar en su `init {}`.
     */
    val events: SharedFlow<Event> = _events.asSharedFlow()

    /**
     * Calcula el estado biométrico inicial al abrir la pantalla de Login.
     */
    fun buildInitialState(): InitialState {
        val isAvailable = biometricAuthenticator.checkAvailability() == BiometricAvailability.Available
        val isEnabled = credentialsStorage.isBiometricEnabled()
        val hasCredentials = credentialsStorage.hasCredentials()
        val effectiveEnabled = isEnabled && hasCredentials

        return InitialState(
            isAvailable = isAvailable,
            isEnabled = effectiveEnabled,
            savedEmail = if (effectiveEnabled) credentialsStorage.getSavedEmail() else null,
            shouldShowPrompt = isAvailable && effectiveEnabled
        )
    }

    /**
     * Alterna la habilitación de login biométrico.
     *
     * @param currentEnabled Estado actual del toggle.
     * @return Nuevo estado del toggle (true = habilitado).
     */
    fun handleToggle(currentEnabled: Boolean): Boolean {
        val newEnabled = !currentEnabled
        credentialsStorage.setBiometricEnabled(newEnabled)
        return newEnabled
    }

    /**
     * Verifica si hay credenciales guardadas para mostrar el prompt biométrico.
     */
    fun canShowPrompt(): Boolean = credentialsStorage.hasCredentials()

    /**
     * Guarda las credenciales en almacenamiento seguro si la biometría está habilitada.
     * Llamar después de un login exitoso con contraseña.
     *
     * @param email Email del usuario.
     * @param password Contraseña del usuario.
     * @param isEnabled Si la biometría está habilitada para este usuario.
     */
    fun saveCredentialsIfEnabled(email: String, password: String, isEnabled: Boolean) {
        if (isEnabled) {
            credentialsStorage.saveCredentials(email, password)
        }
    }

    /**
     * Procesa el resultado de la autenticación biométrica del sistema.
     *
     * En caso de éxito, recupera las credenciales guardadas y emite [Event.CredentialsReady].
     * Los casos de cancelación, fallo y lockout no emiten evento — el ViewModel
     * actualiza el estado de UI directamente.
     *
     * @param result Resultado de la autenticación biométrica del sistema.
     */
    fun handleResult(result: BiometricResult) {
        if (result is BiometricResult.Success) {
            val credentials = credentialsStorage.getCredentials()
            if (credentials != null) {
                _events.tryEmit(Event.CredentialsReady(credentials.first, credentials.second))
            }
        }
    }
}
