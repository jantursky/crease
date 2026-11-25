package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals

class FoldAvoidingPopupTest {
    private val windowSize = IntSize(1000, 800)
    private val popup = IntSize(200, 100)

    private fun seamless(
        at: Float,
        orientation: FoldOrientation = FoldOrientation.Vertical,
    ): Fold =
        if (orientation == FoldOrientation.Vertical) {
            Fold(Rect(at, 0f, at, 800f), orientation, isSeparating = true, isOccluding = false)
        } else {
            Fold(Rect(0f, at, 1000f, at), orientation, isSeparating = true, isOccluding = false)
        }

    private fun anchorAt(
        x: Int,
        y: Int = 300,
    ) = IntRect(x, y, x + 40, y + 40)

    @Test
    fun noFoldKeepsThePosition() {
        val position = IntOffset(450, 300)
        assertEquals(position, foldAvoidingPosition(position, anchorAt(440), popup, windowSize, fold = null))
    }

    @Test
    fun nonSeparatingFoldKeepsThePosition() {
        val fold = Fold(Rect(500f, 0f, 500f, 800f), FoldOrientation.Vertical, isSeparating = false, isOccluding = false)
        val position = IntOffset(450, 300)
        assertEquals(position, foldAvoidingPosition(position, anchorAt(440), popup, windowSize, fold))
    }

    @Test
    fun popupThatWouldCrossMovesBesideTheFoldOnTheAnchorSide() {
        // Anchor is left of the fold at 500; the popup at 350..550 crosses it and fits in the 500 px left side.
        val result = foldAvoidingPosition(IntOffset(350, 300), anchorAt(440), popup, windowSize, seamless(500f))
        assertEquals(IntOffset(300, 300), result)
    }

    @Test
    fun popupThatDoesNotCrossIsUnchanged() {
        val position = IntOffset(100, 300)
        assertEquals(position, foldAvoidingPosition(position, anchorAt(90), popup, windowSize, seamless(500f)))
        val right = IntOffset(600, 300)
        assertEquals(right, foldAvoidingPosition(right, anchorAt(700), popup, windowSize, seamless(500f)))
    }

    @Test
    fun anchorOnTheSmallSideUsesTheOtherSide() {
        // Fold at 300: the left side is 300 px, too narrow for a 350 px popup, so it goes right of the fold.
        val wide = IntSize(350, 100)
        val result = foldAvoidingPosition(IntOffset(100, 300), anchorAt(50), wide, windowSize, seamless(300f))
        assertEquals(IntOffset(300, 300), result)
    }

    @Test
    fun popupThatFitsOnNeitherSideStartsAtTheLargerSide() {
        // Fold at 300: the right side (700 px) is larger, but the 800 px popup fits on neither side.
        // It starts at the fold on the right and is clipped by the window, instead of crossing the fold.
        val huge = IntSize(800, 100)
        val result = foldAvoidingPosition(IntOffset(100, 300), anchorAt(50), huge, windowSize, seamless(300f))
        assertEquals(IntOffset(300, 300), result)
    }

    @Test
    fun popupThatFitsOnNeitherSideWithEqualSidesStartsAtTheLeft() {
        val huge = IntSize(800, 100)
        val result = foldAvoidingPosition(IntOffset(300, 300), anchorAt(800), huge, windowSize, seamless(500f))
        assertEquals(IntOffset(0, 300), result)
    }

    @Test
    fun horizontalFoldMovesAboveOrBelowOnTheAnchorSide() {
        // Fold at y=400. Anchor is below it, so the popup goes below: its 200 px height fits in the 400 px lower part.
        val tall = IntSize(200, 200)
        val result =
            foldAvoidingPosition(
                IntOffset(100, 350),
                anchorAt(100, y = 600),
                tall,
                windowSize,
                seamless(400f, FoldOrientation.Horizontal),
            )
        assertEquals(IntOffset(100, 400), result)
    }

    @Test
    fun occludingFoldKeepsThePopupOffTheHingeBand() {
        // The occluding band is 480..520. A popup at 350..550 crosses it and fits in the 480 px left side.
        val fold = FoldState.book(Size(1000f, 800f), foldWidth = 40f).fold!!
        val result = foldAvoidingPosition(IntOffset(350, 300), anchorAt(440), popup, windowSize, fold)
        assertEquals(IntOffset(280, 300), result)
    }
}
