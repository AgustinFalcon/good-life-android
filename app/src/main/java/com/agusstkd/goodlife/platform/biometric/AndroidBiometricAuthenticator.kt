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
 * No retiene ninguna referencia a Activity — recibe [FragmentActivity]
 * como [platformContext] en cada llamada a [authenticate], evitando memory leaks.
 *
 * @property context Context de Android para acceder a BiometricManager
 */
class AndroidBiometricAuthenticator(
    private val context: Context
) : BiometricAuthenticator {

    private val biometricManager = BiometricManager.from(context)

    override fun checkAvailability(): BiometricAvailability {
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
        platformContext: Any?,
        onResult: (BiometricResult) -> Unit
    ) {
        val activity = platformContext as? FragmentActivity ?: run {
            onResult(BiometricResult.Error(-1, "platformContext must be a FragmentActivity"))
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

        // DEVICE_CREDENTIAL permite PIN/patrón/password como fallback.
        // Cuando se usa DEVICE_CREDENTIAL, Android prohibe setNegativeButtonText.
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
