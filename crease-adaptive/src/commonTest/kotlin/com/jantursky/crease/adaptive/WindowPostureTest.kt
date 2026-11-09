package com.jantursky.crease.adaptive

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.jantursky.crease.Fold
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.Hinge
import com.jantursky.crease.HingeStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowPostureTest {
    private val window = Size(1000f, 800f)

    @Test
    fun tabletopHasOneHorizontalHingeWithTheFoldBounds() {
        val state = FoldState.tabletop(window, foldWidth = 0f)
        val posture = state.toWindowPosture()
        assertTrue(posture.isTabletop)
        assertEquals(1, posture.hingeList.size)
        val hinge = posture.hingeList.single()
        assertEquals(state.fold!!.bounds, hinge.bounds)
        assertFalse(hinge.isVertical)
        assertTrue(hinge.isSeparating)
        assertFalse(hinge.isFlat)
    }

    @Test
    fun bookIsNotTabletopAndHasAVerticalHinge() {
        val posture = FoldState.book(window).toWindowPosture()
        assertFalse(posture.isTabletop)
        assertTrue(posture.hingeList.single().isVertical)
    }

    @Test
    fun noFoldHasNoHingesAndIsNotTabletop() {
        val posture = FoldState.None.toWindowPosture()
        assertFalse(posture.isTabletop)
        assertTrue(posture.hingeList.isEmpty())
    }

    @Test
    fun closedDeviceHasNoHinges() {
        val posture = FoldState.closed().toWindowPosture()
        assertFalse(posture.isTabletop)
        assertTrue(posture.hingeList.isEmpty())
    }

    @Test
    fun occludingFoldBecomesAnOccludingHinge() {
        val posture = FoldState.tabletop(window, foldWidth = 24f).toWindowPosture()
        assertTrue(posture.hingeList.single().isOccluding)
        assertFalse(
            FoldState
                .tabletop(window, foldWidth = 0f)
                .toWindowPosture()
                .hingeList
                .single()
                .isOccluding,
        )
    }

    @Test
    fun flatFoldIsAFlatHinge() {
        val hinge =
            FoldState
                .flat(window, foldWidth = 12f)
                .toWindowPosture()
                .hingeList
                .single()
        assertTrue(hinge.isFlat)
        assertFalse(hinge.isSeparating)
        assertTrue(hinge.isOccluding)
    }

    @Test
    fun hingeWithoutAStatusIsNotFlat() {
        val fold =
            Fold(Rect(0f, 390f, 1000f, 410f), FoldOrientation.Horizontal, isSeparating = true, isOccluding = true)
        val state = FoldState(hinge = null, folds = listOf(fold), cutouts = emptyList())
        val posture = state.toWindowPosture()
        assertTrue(posture.isTabletop)
        assertFalse(posture.hingeList.single().isFlat)
        val open = FoldState(Hinge(HingeStatus.FullyOpen, 180f), listOf(fold), emptyList())
        assertTrue(
            open
                .toWindowPosture()
                .hingeList
                .single()
                .isFlat,
        )
    }
}
