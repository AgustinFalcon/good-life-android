package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig

class FakeBiometricAuthenticator : BiometricAuthenticator {

    var availability: BiometricAvailability = BiometricAvailability.Available
    var authResult: BiometricResult = BiometricResult.Success

    var authenticateCallCount = 0
        private set
    var lastPlatformContext: Any? = null
        private set

    override fun checkAvailability(): BiometricAvailability = availability

    override fun authenticate(
        config: BiometricPromptConfig,
        platformContext: Any?,
        onResult: (BiometricResult) -> Unit
    ) {
        authenticateCallCount++
        lastPlatformContext = platformContext
        onResult(authResult)
    }
}
