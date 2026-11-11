package com.agusstkd.goodlife.core.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionExpiryPolicyTest {

    @Test
    fun `password grant never wipes even with leftover refresh`() {
        assertFalse(
            SessionExpiryPolicy.shouldWipeSessionOnTokenEndpointFailure(
                grantType = SessionExpiryPolicy.GRANT_PASSWORD,
                hasStoredRefreshToken = true,
            ),
        )
    }

    @Test
    fun `refresh grant wipes session`() {
        assertTrue(
            SessionExpiryPolicy.shouldWipeSessionOnTokenEndpointFailure(
                grantType = SessionExpiryPolicy.GRANT_REFRESH,
                hasStoredRefreshToken = true,
            ),
        )
    }

    @Test
    fun `unknown grant with stored refresh wipes as fallback`() {
        assertTrue(
            SessionExpiryPolicy.shouldWipeSessionOnTokenEndpointFailure(
                grantType = null,
                hasStoredRefreshToken = true,
            ),
        )
    }

    @Test
    fun `unknown grant without refresh is a login failure not expiry`() {
        assertFalse(
            SessionExpiryPolicy.shouldWipeSessionOnTokenEndpointFailure(
                grantType = null,
                hasStoredRefreshToken = false,
            ),
        )
    }

    @Test
    fun `parses grantType from form body`() {
        assertEquals(
            SessionExpiryPolicy.GRANT_PASSWORD,
            SessionExpiryPolicy.grantTypeFromFormBody("grantType=password&username=ada&password=secret"),
        )
        assertEquals(
            SessionExpiryPolicy.GRANT_REFRESH,
            SessionExpiryPolicy.grantTypeFromFormBody("grantType=refreshToken&refreshToken=abc"),
        )
        assertNull(SessionExpiryPolicy.grantTypeFromFormBody("username=ada&password=secret"))
    }
}
