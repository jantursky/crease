package com.jantursky.crease.cover.platform

import com.jantursky.crease.cover.CoverDisplayState
import com.jantursky.crease.cover.internal.cinterop.CRSCoverObserverView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSClassFromString
import platform.Foundation.NSProtocolFromString
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * The glue against the iOS this runs on. The simulator has the iOS 27.1 classes, but no scene is ever
 * presented there, so what is checked is that the registration works, not that the content shows up.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosCoverSourceTest {
    private val host = UIViewController(nibName = null, bundle = null)

    private val systemHasAccessories: Boolean
        get() =
            NSClassFromString("UISceneAccessory")?.let { accessory ->
                (accessory as NSObject).respondsToSelector(
                    NSSelectorFromString("cameraCaptureSceneAccessoryWithConfiguration:userInfo:"),
                )
            } == true &&
                host.respondsToSelector(NSSelectorFromString("registerSceneAccessory:"))

    @Test
    fun observerViewMatchesTheSystemsSceneAccessoryApi() {
        var changes = 0
        val view = CRSCoverObserverView(host, contentFactory = { null }, changeHandler = { changes++ })
        assertEquals(systemHasAccessories, view.supported)
        assertFalse(view.presenting)
        assertFalse(view.userInteractionEnabled)
        view.detach()
    }

    @Test
    fun theAccessoryIsOffUntilTheAppAsksForIt() {
        val view = CRSCoverObserverView(host, contentFactory = { null }, changeHandler = {})
        assertFalse(view.enabled)
        view.enabled = true
        // Without a registration there is nothing to enable.
        assertEquals(view.supported, view.enabled)
        view.enabled = false
        assertFalse(view.enabled)
        view.detach()
    }

    @Test
    fun theSceneDelegateIsRegisteredWithTheRuntime() {
        val delegate = NSClassFromString("CRSCoverSceneDelegate")
        assertNotNull(delegate)
        assertTrue((delegate as NSObjectMeta).conformsToProtocol(NSProtocolFromString("UIWindowSceneDelegate")))
    }

    @Test
    fun aDisconnectedSceneIsNotPresenting() {
        var changes = 0
        val view = CRSCoverObserverView(host, contentFactory = { null }, changeHandler = { changes++ })
        view.sceneDisconnected()
        assertFalse(view.presenting)
        assertEquals(1, changes)
        view.detach()
        view.sceneDisconnected()
        assertEquals(1, changes)
    }

    @Test
    fun stateIsUnsupportedWithoutTheApiAndNeverActiveWithoutAScene() {
        val source = IosCoverSource.acquire(host)
        val expected =
            if (systemHasAccessories) {
                listOf(
                    CoverDisplayState.Unavailable,
                    CoverDisplayState.Available,
                )
            } else {
                listOf(CoverDisplayState.Unsupported)
            }
        assertTrue(source.state.value in expected, "was ${source.state.value}")
        source.present { }
        assertEquals(source.view.supported, source.view.enabled)
        source.present(null)
        assertFalse(source.view.enabled)
        source.release()
    }

    @Test
    fun oneSourcePerHostAndTheLastReaderRemovesIt() {
        val a = IosCoverSource.acquire(host)
        val b = IosCoverSource.acquire(host)
        assertSame(a, b)
        assertEquals(a.view, host.view.subviews.first())
        a.release()
        assertEquals(host.view, a.view.superview)
        b.release()
        assertEquals(null, a.view.superview)
        assertNotNull(IosCoverSource.acquire(host).also { it.release() })
    }
}
