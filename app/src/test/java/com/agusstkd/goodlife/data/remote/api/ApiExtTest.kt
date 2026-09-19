package com.agusstkd.goodlife.data.remote.api

import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ApiExtTest {
    @Test fun `optional success without data remains a successful absence`() {
        val result = processOptionalResponse(BaseResponse<Any?>(code = 200, data = null))
        assertTrue(result is Result.Success)
        assertNull((result as Result.Success).data)
    }

    @Test fun `optional success with data preserves its payload`() {
        val result = processOptionalResponse(BaseResponse(code = 200, data = "routine"))
        assertEquals(Result.Success("routine"), result)
    }

    @Test fun `optional non success preserves server classification`() {
        val result = processOptionalResponse(BaseResponse<Any?>(code = 500, message = "redacted"))
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is ApiException.ServerException)
    }

    @Test fun `optional call rethrows cancellation`() = runTest {
        try {
            executeOptionalApiCall<Any> { throw CancellationException("cancel") }
            fail("CancellationException must propagate")
        } catch (_: CancellationException) {
            // expected
        }
    }
    @Test fun `standard call rethrows cancellation`() = runTest {
        try {
            executeApiCall<Any> { throw CancellationException("cancel") }
            fail("CancellationException must propagate")
        } catch (_: CancellationException) {
            // expected
        }
    }
}
