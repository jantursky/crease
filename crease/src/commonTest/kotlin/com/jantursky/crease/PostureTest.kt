package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class PostureTest {
    private val vertical = fold(FoldOrientation.Vertical, isSeparating = true)
    private val horizontal = fold(FoldOrientation.Horizontal, isSeparating = true)
    private val seamless = fold(FoldOrientation.Vertical, isSeparating = false)

    private fun fold(
        orientation: FoldOrientation,
        isSeparating: Boolean,
    ) = Fold(Rect(50f, 0f, 50f, 100f), orientation, isSeparating, isOccluding = false)

    private fun hinge(status: HingeStatus) = Hinge(status, null)

    @Test
    fun closedHingeIsClosedWhateverTheFold() {
        assertEquals(Posture.Closed, postureOf(hinge(HingeStatus.Closed), null))
        assertEquals(Posture.Closed, postureOf(hinge(HingeStatus.Closed), vertical))
        assertEquals(Posture.Closed, postureOf(hinge(HingeStatus.Closed), horizontal))
    }

    @Test
    fun halfOpenWithSeparatingFoldIsBookOrTabletop() {
        assertEquals(Posture.Book, postureOf(hinge(HingeStatus.PartiallyOpen), vertical))
        assertEquals(Posture.Tabletop, postureOf(hinge(HingeStatus.PartiallyOpen), horizontal))
    }

    @Test
    fun unknownHingeWithSeparatingFoldIsBookOrTabletop() {
        assertEquals(Posture.Book, postureOf(null, vertical))
        assertEquals(Posture.Tabletop, postureOf(null, horizontal))
    }

    @Test
    fun everythingElseIsFlat() {
        assertEquals(Posture.Flat, postureOf(null, null))
        assertEquals(Posture.Flat, postureOf(null, seamless))
        assertEquals(Posture.Flat, postureOf(hinge(HingeStatus.PartiallyOpen), null))
        assertEquals(Posture.Flat, postureOf(hinge(HingeStatus.PartiallyOpen), seamless))
        assertEquals(Posture.Flat, postureOf(hinge(HingeStatus.FullyOpen), vertical))
        assertEquals(Posture.Flat, postureOf(hinge(HingeStatus.FullyOpen), null))
    }

    @Test
    fun foldStateDerivesPostureFromHingeAndFirstFold() {
        assertEquals(Posture.Flat, FoldState.None.posture)
        assertEquals(
            Posture.Book,
            FoldState(hinge(HingeStatus.PartiallyOpen), listOf(vertical, horizontal), emptyList()).posture,
        )
    }
}
