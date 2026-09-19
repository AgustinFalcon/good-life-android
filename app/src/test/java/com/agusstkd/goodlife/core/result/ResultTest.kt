package com.agusstkd.goodlife.core.result

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.fail
import org.junit.Test

class ResultTest {
    @Test fun `suspendResultOf rethrows cancellation`() = runTest {
        try {
            suspendResultOf<Unit> { throw CancellationException("cancel") }
            fail("CancellationException must propagate")
        } catch (_: CancellationException) {
            // expected
        }
    }
}
