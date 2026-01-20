package com.agusstkd.goodlife.platform.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig

/**
 * Implementación Android de [BiometricAuthenticator].
 *
 * Usa AndroidX BiometricPrompt para autenticación con huella/rostro.
 *
 * NOTA IMPORTANTE:
 * Esta clase es específica de Android y NO debe usarse directamente en código compartido.
 * El código de dominio y presentation debe usar la interface [BiometricAuthenticator].
 *
 * @property context Context de Android para acceder a BiometricManager
 */
class AndroidBiometricAuthenticator(
    private val context: Context
) : BiometricAuthenticator {

    private val biometricManager = BiometricManager.from(context)

    // Referencia a la Activity para mostrar el prompt
    // Se configura desde el Owner/Composable
    private var currentActivity: FragmentActivity? = null

    /**
     * Configura la Activity actual.
     *
     * Debe llamarse desde el Composable/Owner antes de authenticate().
     * Esto es necesario porque BiometricPrompt requiere FragmentActivity.
     */
    fun setActivity(activity: FragmentActivity?) {
        currentActivity = activity
    }

    override fun checkAvailability(): BiometricAvailability {
        // Verificar biometría O credenciales del dispositivo
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricAvailability.Available

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricAvailability.NoHardware

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricAvailability.HardwareUnavailable

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricAvailability.NoBiometricEnrolled

            else ->
                BiometricAvailability.Unknown
        }
    }

    override fun authenticate(
        config: BiometricPromptConfig,
        onResult: (BiometricResult) -> Unit
    ) {
        val activity = currentActivity ?: run {
            onResult(BiometricResult.Error(-1, "Activity not set. Call setActivity() first."))
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                val error = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                        BiometricResult.Cancelled

                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                        BiometricResult.Lockout(errString.toString())

                    else ->
                        BiometricResult.Error(errorCode, errString.toString())
                }
                onResult(error)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onResult(BiometricResult.Failed)
            }
        }

        // Permitir biometría O credenciales del dispositivo (PIN/patrón/password)
        // NOTA: Cuando se usa DEVICE_CREDENTIAL, NO se puede usar setNegativeButtonText
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
