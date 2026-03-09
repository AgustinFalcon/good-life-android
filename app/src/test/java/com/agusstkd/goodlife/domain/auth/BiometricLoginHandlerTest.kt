package com.agusstkd.goodlife.domain.auth

import app.cash.turbine.test
import com.agusstkd.goodlife.core.biometric.BiometricAvailability
import com.agusstkd.goodlife.core.biometric.BiometricResult
import com.agusstkd.goodlife.domain.biometric.BiometricPromptConfig
import com.agusstkd.goodlife.fake.FakeBiometricAuthenticator
import com.agusstkd.goodlife.fake.FakeSecureCredentialsStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BiometricLoginHandlerTest {

    private lateinit var authenticator: FakeBiometricAuthenticator
    private lateinit var storage: FakeSecureCredentialsStorage
    private lateinit var handler: BiometricLoginHandler

    @Before
    fun setUp() {
        authenticator = FakeBiometricAuthenticator()
        storage = FakeSecureCredentialsStorage()
        handler = BiometricLoginHandler(authenticator, storage)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // buildInitialState
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `buildInitialState - available with credentials - returns shouldShowPrompt true`() = runTest {
        storage.setBiometricEnabled(true)
        storage.saveCredentials("user@test.com", "password123")

        val state = handler.buildInitialState()

        assertTrue(state.isAvailable)
        assertTrue(state.isEnabled)
        assertTrue(state.shouldShowPrompt)
        assertEquals("user@test.com", state.savedEmail)
    }

    @Test
    fun `buildInitialState - no credentials - returns disabled`() = runTest {
        storage.setBiometricEnabled(true)

        val state = handler.buildInitialState()

        assertTrue(state.isAvailable)
        assertFalse(state.isEnabled)
        assertFalse(state.shouldShowPrompt)
        assertNull(state.savedEmail)
    }

    @Test
    fun `buildInitialState - no hardware - returns unavailable`() = runTest {
        authenticator.availability = BiometricAvailability.NoHardware

        val state = handler.buildInitialState()

        assertFalse(state.isAvailable)
        assertFalse(state.shouldShowPrompt)
    }

    @Test
    fun `buildInitialState - biometric not enabled - returns disabled`() = runTest {
        storage.saveCredentials("user@test.com", "pass")
        storage.setBiometricEnabled(false)

        val state = handler.buildInitialState()

        assertFalse(state.isEnabled)
        assertFalse(state.shouldShowPrompt)
        assertNull(state.savedEmail)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // authenticate → CredentialsReady event
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `authenticate success - emits CredentialsReady`() = runTest {
        storage.saveCredentials("user@test.com", "secret")
        authenticator.authResult = BiometricResult.Success

        handler.events.test {
            handler.authenticate(
                config = BiometricPromptConfig("Title", "Subtitle"),
                platformContext = null,
                onUiResult = {}
            )

            val event = awaitItem()
            assertTrue(event is BiometricLoginHandler.Event.CredentialsReady)
            val ready = event as BiometricLoginHandler.Event.CredentialsReady
            assertEquals("user@test.com", ready.email)
            assertEquals("secret", ready.password)
        }
    }

    @Test
    fun `authenticate cancelled - does not emit event`() = runTest {
        authenticator.authResult = BiometricResult.Cancelled

        handler.events.test {
            var uiResult: BiometricResult? = null
            handler.authenticate(
                config = BiometricPromptConfig("Title", "Subtitle"),
                platformContext = null,
                onUiResult = { uiResult = it }
            )

            assertEquals(BiometricResult.Cancelled, uiResult)
            expectNoEvents()
        }
    }

    @Test
    fun `authenticate success without credentials - does not emit event`() = runTest {
        authenticator.authResult = BiometricResult.Success

        handler.events.test {
            handler.authenticate(
                config = BiometricPromptConfig("Title", "Subtitle"),
                platformContext = null,
                onUiResult = {}
            )
            expectNoEvents()
        }
    }

    @Test
    fun `authenticate - passes platformContext to authenticator`() {
        val fakeContext = "fake-activity"
        handler.authenticate(
            config = BiometricPromptConfig("T", "S"),
            platformContext = fakeContext,
            onUiResult = {}
        )
        assertEquals("fake-activity", authenticator.lastPlatformContext)
        assertEquals(1, authenticator.authenticateCallCount)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // handleToggle
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `handleToggle - disables when currently enabled`() {
        storage.setBiometricEnabled(true)
        val newState = handler.handleToggle(currentEnabled = true)
        assertFalse(newState)
        assertFalse(storage.isBiometricEnabled())
    }

    @Test
    fun `handleToggle - enables when currently disabled`() {
        val newState = handler.handleToggle(currentEnabled = false)
        assertTrue(newState)
        assertTrue(storage.isBiometricEnabled())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // saveCredentialsIfEnabled
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `saveCredentialsIfEnabled - saves when enabled`() {
        handler.saveCredentialsIfEnabled("a@b.com", "pass", isEnabled = true)
        assertEquals(Pair("a@b.com", "pass"), storage.getCredentials())
    }

    @Test
    fun `saveCredentialsIfEnabled - does not save when disabled`() {
        handler.saveCredentialsIfEnabled("a@b.com", "pass", isEnabled = false)
        assertNull(storage.getCredentials())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // canShowPrompt
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    @Test
    fun `canShowPrompt - true when credentials exist`() {
        storage.saveCredentials("a@b.com", "pass")
        assertTrue(handler.canShowPrompt())
    }

    @Test
    fun `canShowPrompt - false when no credentials`() {
        assertFalse(handler.canShowPrompt())
    }
}
