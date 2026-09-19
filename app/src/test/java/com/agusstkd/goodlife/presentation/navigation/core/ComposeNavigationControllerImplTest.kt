package com.agusstkd.goodlife.presentation.navigation.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.agusstkd.goodlife.presentation.navigation.host.collectNavigationActionsWhenStarted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeNavigationControllerImplTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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

    @Test fun `navigation emitted while stopped waits until lifecycle starts`() = runTest {
        val controller = ComposeNavigationControllerImpl()
        val lifecycleOwner = TestLifecycleOwner()
        val delivered = mutableListOf<NavigationAction>()

        lifecycleOwner.registry.currentState = Lifecycle.State.CREATED
        backgroundScope.launch {
            collectNavigationActionsWhenStarted(
                lifecycle = lifecycleOwner.lifecycle,
                navigationActions = controller.navigationAction,
                onAction = delivered::add
            )
        }
        runCurrent()

        lifecycleOwner.registry.currentState = Lifecycle.State.STARTED
        runCurrent()
        lifecycleOwner.registry.currentState = Lifecycle.State.CREATED
        runCurrent()

        controller.navigateUp()
        runCurrent()
        assertTrue(delivered.isEmpty())

        lifecycleOwner.registry.currentState = Lifecycle.State.STARTED
        runCurrent()
        assertEquals(listOf(NavigationAction.NavigateUp), delivered)
    }

    private class TestLifecycleOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle = registry
    }
}
