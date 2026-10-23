package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FoldStateFactoriesTest {
    private val window = Size(1000f, 800f)

    @Test
    fun closedHasAShutHingeAndNoFolds() {
        val state = FoldState.closed()
        assertEquals(Hinge(HingeStatus.Closed, 0f), state.hinge)
        assertTrue(state.folds.isEmpty())
        assertTrue(state.cutouts.isEmpty())
        assertEquals(Posture.Closed, state.posture)
    }

    @Test
    fun flatHasAFullyOpenHingeAndASeamlessCenteredFold() {
        val state = FoldState.flat(window)
        assertEquals(Hinge(HingeStatus.FullyOpen, 180f), state.hinge)
        assertEquals(
            Fold(Rect(500f, 0f, 500f, 800f), FoldOrientation.Vertical, isSeparating = false, isOccluding = false),
            state.fold,
        )
        assertEquals(Posture.Flat, state.posture)
    }

    @Test
    fun flatAcceptsAHorizontalFoldWithWidth() {
        val fold = FoldState.flat(window, FoldOrientation.Horizontal, foldWidth = 40f).fold
        assertEquals(
            Fold(Rect(0f, 380f, 1000f, 420f), FoldOrientation.Horizontal, isSeparating = false, isOccluding = true),
            fold,
        )
    }

    @Test
    fun bookIsHalfOpenWithAVerticalSeparatingFold() {
        val state = FoldState.book(window)
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 90f), state.hinge)
        assertEquals(
            Fold(Rect(500f, 0f, 500f, 800f), FoldOrientation.Vertical, isSeparating = true, isOccluding = false),
            state.fold,
        )
        assertEquals(Posture.Book, state.posture)
    }

    @Test
    fun bookWithAGapOccludes() {
        val state = FoldState.book(window, angle = 120f, foldWidth = 40f)
        assertEquals(120f, state.hinge?.angle)
        assertEquals(
            Fold(Rect(480f, 0f, 520f, 800f), FoldOrientation.Vertical, isSeparating = true, isOccluding = true),
            state.fold,
        )
    }

    @Test
    fun tabletopIsHalfOpenWithAHorizontalSeparatingFold() {
        val state = FoldState.tabletop(window, angle = 100f, foldWidth = 20f)
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 100f), state.hinge)
        assertEquals(
            Fold(Rect(0f, 390f, 1000f, 410f), FoldOrientation.Horizontal, isSeparating = true, isOccluding = true),
            state.fold,
        )
        assertEquals(Posture.Tabletop, state.posture)
    }

    @Test
    fun noneHasNothing() {
        assertEquals(null, FoldState.None.hinge)
        assertTrue(FoldState.None.folds.isEmpty())
        assertTrue(FoldState.None.cutouts.isEmpty())
    }
}
