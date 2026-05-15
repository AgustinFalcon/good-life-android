package com.agusstkd.goodlife.domain.auth

import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Encapsula la state machine de autenticación biométrica para Login.
 *
 * Extrae de [com.agusstkd.goodlife.presentation.screen.login.LoginViewModel]
 * toda la lógica biométrica, dejando al ViewModel como orquestador.
 *
 * ## Responsabilidades
 * - Verificar disponibilidad y estado de biometría del dispositivo.
 * - Gestionar el toggle de habilitación biométrica.
 * - Orquestar la autenticación: recibe [platformContext], delega al [BiometricAuthenticator],
 *   y emite [Event.CredentialsReady] cuando la autenticación fue exitosa.
 *
 * ## Lo que NO hace
 * - No llama a [com.agusstkd.goodlife.domain.usecase.login.LoginUseCase] directamente.
 *   El ViewModel reacciona al evento [Event.CredentialsReady].
 * - No actualiza el [com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState].
 *   El ViewModel es dueño del estado de UI.
 *
 * ## Flujo de autenticación biométrica exitosa
 * ```
 * Usuario toca ícono biométrico
 *     → LoginViewModel.handleBiometricIconClick()
 *     → LoginUiState.Content(shouldShowBiometricPrompt = true)
 *     → Owner observa flag, envía OnBiometricAuthenticate(activity)
 *     → LoginViewModel → BiometricLoginHandler.authenticate(config, platformContext, onUiResult)
 *     → BiometricPrompt en pantalla
 *     → BiometricResult.Success
 *     → Handler emite Event.CredentialsReady(email, password) via Channel
 *     → LoginViewModel.collectBiometricEvents → performLoginWithCredentials
 * ```
 *
 * @param biometricAuthenticator Verifica disponibilidad y ejecuta autenticación.
 * @param credentialsStorage Almacenamiento seguro de credenciales del usuario.
 */
class BiometricLoginHandler(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val credentialsStorage: SecureCredentialsStorage
) {

    sealed interface Event {
        data class CredentialsReady(val email: String, val password: String) : Event
    }

    data class InitialState(
        val isAvailable: Boolean,
        val isEnabled: Boolean,
        val savedEmail: String?,
        val shouldShowPrompt: Boolean
    )

    private val _events = Channel<Event>(Channel.BUFFERED)

    /**
     * Flow de eventos que el ViewModel debe colectar en su `init {}`.
     * Usa [Channel] en lugar de SharedFlow para garantizar entrega de eventos one-shot.
     */
    val events: Flow<Event> = _events.receiveAsFlow()

    /**
     * Calcula el estado biométrico inicial al abrir la pantalla de Login.
     *
     * Suspend porque [SecureCredentialsStorage] (EncryptedSharedPreferences) puede ser
     * lento en la primera lectura al generar claves criptográficas.
     * El ViewModel debe llamar esto en un coroutine scope con dispatcher IO.
     */
    suspend fun buildInitialState(): InitialState {
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
     * Ejecuta la autenticación biométrica.
     *
     * Delega al [BiometricAuthenticator] pasando el [platformContext] opaco.
     * Si la autenticación es exitosa, emite [Event.CredentialsReady] internamente
     * y luego invoca [onUiResult] para que el ViewModel actualice el estado de UI.
     *
     * @param config Textos del prompt biométrico.
     * @param platformContext En Android: FragmentActivity. Opaco para KMP.
     * @param onUiResult Callback para que el ViewModel actualice UI (cancel, error, lockout).
     */
    fun authenticate(
        config: BiometricPromptConfig,
        platformContext: Any?,
        onUiResult: (BiometricResult) -> Unit
    ) {
        biometricAuthenticator.authenticate(config, platformContext) { result ->
            processResult(result)
            onUiResult(result)
        }
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
     */
    fun saveCredentialsIfEnabled(email: String, password: String, isEnabled: Boolean) {
        if (isEnabled) {
            credentialsStorage.saveCredentials(email, password)
        }
    }

    /**
     * Procesa internamente el resultado de la autenticación.
     * Si es exitosa, recupera credenciales y emite evento por el Channel.
     */
    private fun processResult(result: BiometricResult) {
        if (result is BiometricResult.Success) {
            val credentials = credentialsStorage.getCredentials()
            if (credentials != null) {
                _events.trySend(Event.CredentialsReady(credentials.first, credentials.second))
            }
        }
    }
}
