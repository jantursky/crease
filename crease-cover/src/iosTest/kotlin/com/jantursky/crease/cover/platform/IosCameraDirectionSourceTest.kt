package com.jantursky.crease.cover.platform

import com.jantursky.crease.cover.internal.cinterop.CRSCameraDirectionObserver
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSClassFromString
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** The camera direction glue against the iOS this runs on. The simulator reports no cameras, so lists stay empty. */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosCameraDirectionSourceTest {
    private val hasCoordinator = NSClassFromString("AVCaptureDeviceDirectionCoordinator") != null

    @Test
    fun observerMatchesTheSystemsCoordinator() {
        val observer = CRSCameraDirectionObserver(UIView(), changeHandler = {})
        assertEquals(hasCoordinator, observer.supported)
        assertTrue(observer.forwardFacing.isEmpty())
        assertTrue(observer.backwardFacing.isEmpty())
        observer.detach()
    }

    @Test
    fun aMapWithoutDescriptorsEmptiesTheListsAndNotifies() {
        var changes = 0
        val observer = CRSCameraDirectionObserver(UIView(), changeHandler = { changes++ })
        observer.handleMap(NSObject())
        assertEquals(1, changes)
        assertTrue(observer.directions().forwardFacing.isEmpty())
        assertTrue(observer.directions().backwardFacing.isEmpty())
        observer.detach()
        observer.handleMap(NSObject())
        assertEquals(1, changes)
    }

    @Test
    fun readersOfOneViewShareASourceUntilTheLastLeaves() {
        val host = UIView()
        val first = IosCameraDirectionSource.acquire(host)
        val second = IosCameraDirectionSource.acquire(host)
        assertSame(first, second)
        assertEquals(hasCoordinator, first.state.value != null)
        first.release()
        assertSame(first, IosCameraDirectionSource.acquire(host))
        repeat(2) { first.release() }
        val fresh = IosCameraDirectionSource.acquire(host)
        assertNotSame(first, fresh)
        fresh.release()
    }
}
