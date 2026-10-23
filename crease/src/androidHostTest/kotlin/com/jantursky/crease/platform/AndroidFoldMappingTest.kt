package com.jantursky.crease.platform

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.jantursky.crease.Fold
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.Hinge
import com.jantursky.crease.HingeStatus
import com.jantursky.crease.Posture
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import android.graphics.Rect as AndroidRect

/** A folding feature as WindowManager reports one; the library only reads its properties. */
private class TestFoldingFeature(
    override val bounds: AndroidRect,
    override val state: FoldingFeature.State,
    override val orientation: FoldingFeature.Orientation,
    override val occlusionType: FoldingFeature.OcclusionType = FoldingFeature.OcclusionType.NONE,
    override val isSeparating: Boolean = state == FoldingFeature.State.HALF_OPENED,
) : FoldingFeature

private class OtherFeature(
    override val bounds: AndroidRect,
) : DisplayFeature

@RunWith(RobolectricTestRunner::class)
class AndroidFoldMappingTest {
    private val halfOpenVertical =
        TestFoldingFeature(
            AndroidRect(1080, 0, 1080, 2000),
            FoldingFeature.State.HALF_OPENED,
            FoldingFeature.Orientation.VERTICAL,
        )

    @Test
    fun halfOpenedVerticalFeatureIsABook() {
        val state = androidFoldState(listOf(halfOpenVertical), angle = 95f, cutouts = emptyList())
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 95f), state.hinge)
        assertEquals(
            Fold(Rect(1080f, 0f, 1080f, 2000f), FoldOrientation.Vertical, isSeparating = true, isOccluding = false),
            state.fold,
        )
        assertEquals(Posture.Book, state.posture)
    }

    @Test
    fun flatHorizontalOccludingFeature() {
        val feature =
            TestFoldingFeature(
                AndroidRect(0, 1200, 1800, 1260),
                FoldingFeature.State.FLAT,
                FoldingFeature.Orientation.HORIZONTAL,
                FoldingFeature.OcclusionType.FULL,
                isSeparating = true,
            )
        val state = androidFoldState(listOf(feature), angle = null, cutouts = emptyList())
        assertEquals(Hinge(HingeStatus.FullyOpen, null), state.hinge)
        assertEquals(
            Fold(Rect(0f, 1200f, 1800f, 1260f), FoldOrientation.Horizontal, isSeparating = true, isOccluding = true),
            state.fold,
        )
        assertEquals(Posture.Flat, state.posture)
    }

    @Test
    fun withoutAFeatureTheSensorDecides() {
        val state = androidFoldState(emptyList(), angle = 3f, cutouts = emptyList())
        assertEquals(Hinge(HingeStatus.Closed, 3f), state.hinge)
        assertTrue(state.folds.isEmpty())
        assertEquals(Posture.Closed, state.posture)
    }

    @Test
    fun withoutFeatureOrSensorThereIsNoHinge() {
        val cutout = Rect(500f, 0f, 580f, 80f)
        val state =
            androidFoldState(listOf(OtherFeature(AndroidRect(0, 0, 10, 10))), angle = null, cutouts = listOf(cutout))
        assertNull(state.hinge)
        assertTrue(state.folds.isEmpty())
        assertEquals(listOf(cutout), state.cutouts)
    }

    @Test
    fun featuresMoveIntoTheCompositionsWindow() {
        // A dialog whose window sits 100 px right and 300 px down from the activity's.
        val state =
            androidFoldState(listOf(halfOpenVertical), angle = null, cutouts = emptyList(), Offset(-100f, -300f))
        assertEquals(Rect(980f, -300f, 980f, 1700f), state.fold?.bounds)
    }

    @Test
    fun featureStatusMapping() {
        assertEquals(HingeStatus.PartiallyOpen, halfOpenVertical.hingeStatus)
        val flat = TestFoldingFeature(AndroidRect(), FoldingFeature.State.FLAT, FoldingFeature.Orientation.VERTICAL)
        assertEquals(HingeStatus.FullyOpen, flat.hingeStatus)
    }

    @Test
    fun findsTheActivityBehindContextWrappers() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        assertSame(activity, ContextWrapper(ContextWrapper(activity)).findActivity())
        assertNull(RuntimeEnvironment.getApplication().findActivity())
    }
}
