@file:OptIn(ExperimentalForeignApi::class)

package com.jantursky.crease.platform

import com.jantursky.crease.FoldState
import com.jantursky.crease.Hinge
import com.jantursky.crease.HingeStatus
import com.jantursky.crease.internal.cinterop.CRSHingeObserverView
import com.jantursky.crease.internal.cinterop.CRSHingeProtocol
import com.jantursky.crease.internal.cinterop.CRSHingeUpdateProtocol
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSClassFromString
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** Stands in for `UIHingeInteractionUpdate`; the observer only sends it `hinge`. */
private class FakeUpdate(
    private val hinge: Any?,
) : NSObject(),
    CRSHingeUpdateProtocol {
    override fun hinge(): Any? = hinge
}

/** Stands in for `UIHinge`. */
private class FakeHinge(
    private val status: Long,
    private val angle: Double,
) : NSObject(),
    CRSHingeProtocol {
    override fun status(): Long = status

    override fun angle(): Double = angle
}

class IosFoldSourceTest {
    private val host = UIView(frame = CGRectMake(0.0, 0.0, 400.0, 800.0))

    @Test
    fun observerViewMatchesTheSystemsHingeApi() {
        var changes = 0
        val view = CRSHingeObserverView(changeHandler = { changes++ })
        // iOS 27.1 has the interaction on every device; older systems do not.
        assertEquals(NSClassFromString("UIHingeInteraction") != null, view.hingeSupported)
        assertFalse(view.hasHinge)
        assertFalse(view.userInteractionEnabled)
        view.refreshRegions()
        view.layoutSubviews()
        assertEquals(2, changes)
        view.detach()
        view.refreshRegions()
        assertEquals(2, changes)
    }

    @Test
    fun updatesWithAHingeAreRead() {
        val source = IosFoldSource.acquire(host)
        source.view.handleUpdate(FakeUpdate(FakeHinge(status = 2, angle = PI / 2)))
        assertTrue(source.view.hasHinge)
        assertEquals(2L, source.view.hingeStatus)
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 90f), source.state.value.hinge)
        source.view.handleUpdate(FakeUpdate(FakeHinge(status = 3, angle = PI)))
        assertEquals(Hinge(HingeStatus.FullyOpen, 180f), source.state.value.hinge)
        source.release()
    }

    @Test
    fun updateWithoutAHingeMeansNoHinge() {
        val source = IosFoldSource.acquire(host)
        source.view.handleUpdate(FakeUpdate(FakeHinge(status = 1, angle = 0.0)))
        assertEquals(
            HingeStatus.Closed,
            source.state.value.hinge
                ?.status,
        )
        source.view.handleUpdate(FakeUpdate(hinge = null))
        assertFalse(source.view.hasHinge)
        assertEquals(FoldState.None, source.state.value)
        // An object without the expected selectors reads as no hinge too.
        source.view.handleUpdate(NSObject())
        assertNull(source.state.value.hinge)
        source.release()
    }

    @Test
    fun layoutReportsNoFoldWithoutTheRegionApi() {
        val source = IosFoldSource.acquire(host)
        source.view.layoutSubviews()
        assertTrue(source.view.divisions.isEmpty())
        assertTrue(source.view.occlusions.isEmpty())
        assertEquals(FoldState.None, source.state.value)
        source.release()
    }

    @Test
    fun oneObserverViewPerHostAndTheLastReaderRemovesIt() {
        val a = IosFoldSource.acquire(host)
        val b = IosFoldSource.acquire(host)
        assertSame(a, b)
        assertEquals(1, host.subviews.size)
        assertEquals(host, a.view.superview)
        a.release()
        assertEquals(1, host.subviews.size)
        b.release()
        assertTrue(host.subviews.isEmpty())
    }
}
