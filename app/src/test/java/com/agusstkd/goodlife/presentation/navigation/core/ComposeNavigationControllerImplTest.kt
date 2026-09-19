package com.agusstkd.goodlife.presentation.navigation.core

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ComposeNavigationControllerImplTest {
    @Test fun `navigation emitted before collection is delivered in order`() = runTest {
        val controller = ComposeNavigationControllerImpl()

        controller.navigateUp()
        controller.popBackTo(route = "root", inclusive = true)

        assertEquals(NavigationAction.NavigateUp, controller.navigationAction.first())
        assertEquals(
            NavigationAction.PopBackTo(route = "root", inclusive = true),
            controller.navigationAction.first()
        )
    }

    @Test fun `consumed navigation is not replayed to a later collector`() = runTest {
        val controller = ComposeNavigationControllerImpl()
        controller.navigateUp()

        assertEquals(NavigationAction.NavigateUp, controller.navigationAction.first())
        assertNull(withTimeoutOrNull(1) { controller.navigationAction.first() })
    }
}
