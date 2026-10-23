package com.jantursky.crease

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Constraints
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FoldGeometryTest {
    private val container = Rect(0f, 0f, 1000f, 800f)

    private fun vertical(
        left: Float,
        right: Float,
        occluding: Boolean = right > left,
    ) = Fold(Rect(left, 0f, right, 800f), FoldOrientation.Vertical, isSeparating = true, isOccluding = occluding)

    private fun horizontal(
        top: Float,
        bottom: Float,
        occluding: Boolean = bottom > top,
    ) = Fold(Rect(0f, top, 1000f, bottom), FoldOrientation.Horizontal, isSeparating = true, isOccluding = occluding)

    @Test
    fun seamlessVerticalFoldSplitsAtItsLine() {
        val split = splitAtFold(container, vertical(400f, 400f))!!
        assertEquals(Rect(0f, 0f, 400f, 800f), split.first)
        assertEquals(Rect(400f, 0f, 1000f, 800f), split.second)
    }

    @Test
    fun occludingVerticalFoldIsLeftOut() {
        val split = splitAtFold(container, vertical(480f, 520f))!!
        assertEquals(Rect(0f, 0f, 480f, 800f), split.first)
        assertEquals(Rect(520f, 0f, 1000f, 800f), split.second)
    }

    @Test
    fun nonOccludingFoldWithWidthSplitsAtItsCenter() {
        val split = splitAtFold(container, vertical(480f, 520f, occluding = false))!!
        assertEquals(500f, split.first.right)
        assertEquals(500f, split.second.left)
    }

    @Test
    fun horizontalFoldSplitsTopAndBottom() {
        val split = splitAtFold(container, horizontal(380f, 420f))!!
        assertEquals(Rect(0f, 0f, 1000f, 380f), split.first)
        assertEquals(Rect(0f, 420f, 1000f, 800f), split.second)
        val seamless = splitAtFold(container, horizontal(300f, 300f))!!
        assertEquals(300f, seamless.first.bottom)
        assertEquals(300f, seamless.second.top)
    }

    @Test
    fun foldOutsideTheContainerDoesNotSplit() {
        assertNull(splitAtFold(container, vertical(1200f, 1200f)))
        assertNull(splitAtFold(container, vertical(-10f, -10f)))
        assertNull(splitAtFold(container, vertical(0f, 0f)))
        assertNull(splitAtFold(container, vertical(1000f, 1000f)))
        assertNull(splitAtFold(container, horizontal(900f, 940f)))
        assertNull(splitAtFold(container, horizontal(800f, 800f)))
    }

    @Test
    fun foldCoveringAnEdgeDoesNotSplit() {
        assertNull(splitAtFold(container, vertical(-20f, 20f)))
        assertNull(splitAtFold(container, horizontal(780f, 820f)))
    }

    @Test
    fun foldThatMissesTheContainerAlongItsLineDoesNotSplit() {
        val below =
            Fold(Rect(500f, 900f, 500f, 1200f), FoldOrientation.Vertical, isSeparating = true, isOccluding = false)
        val above =
            Fold(Rect(500f, -300f, 500f, 0f), FoldOrientation.Vertical, isSeparating = true, isOccluding = false)
        val right =
            Fold(Rect(1000f, 400f, 1300f, 400f), FoldOrientation.Horizontal, isSeparating = true, isOccluding = false)
        val left =
            Fold(Rect(-300f, 400f, 0f, 400f), FoldOrientation.Horizontal, isSeparating = true, isOccluding = false)
        listOf(below, above, right, left).forEach { assertNull(splitAtFold(container, it)) }
    }

    @Test
    fun largerSideIsChosenAndTiesGoFirst() {
        assertEquals(Rect(300f, 0f, 1000f, 800f), splitAtFold(container, vertical(300f, 300f))!!.larger)
        assertEquals(Rect(0f, 0f, 700f, 800f), splitAtFold(container, vertical(700f, 700f))!!.larger)
        assertEquals(Rect(0f, 0f, 480f, 800f), splitAtFold(container, vertical(480f, 520f))!!.larger)
    }

    @Test
    fun foldMovesIntoLocalCoordinates() {
        val local = vertical(480f, 520f).toLocal(Offset(100f, 50f))
        assertEquals(Rect(380f, -50f, 420f, 750f), local.bounds)
        assertEquals(FoldOrientation.Vertical, local.orientation)
        assertEquals(true, local.isSeparating)
        assertEquals(true, local.isOccluding)
    }

    @Test
    fun windowFoldSplitsOnlyOnceTheOriginIsKnown() {
        val fold = vertical(600f, 600f)
        assertNull(splitAtWindowFold(container, fold, null))
        assertNull(splitAtWindowFold(container, null, Offset.Zero))
        assertEquals(500f, splitAtWindowFold(container, fold, Offset(100f, 0f))!!.first.right)
    }

    @Test
    fun separatingFoldIgnoresSeamlessOnes() {
        val seamless = Fold(Rect.Zero, FoldOrientation.Vertical, isSeparating = false, isOccluding = false)
        assertNull(FoldState(null, listOf(seamless), emptyList()).separatingFold)
        assertEquals(vertical(1f, 1f), FoldState(null, listOf(vertical(1f, 1f)), emptyList()).separatingFold)
        assertNull(FoldState.None.separatingFold)
    }

    @Test
    fun fillAreaIsOpenEndedWhereUnbounded() {
        assertEquals(Rect(0f, 0f, 300f, 200f), Constraints(maxWidth = 300, maxHeight = 200).fillArea())
        val open = Constraints(minWidth = 10, maxWidth = 300).fillArea()
        assertEquals(300f, open.right)
        assertEquals(Float.POSITIVE_INFINITY, open.bottom)
        assertEquals(Float.POSITIVE_INFINITY, Constraints().fillArea().right)
    }

    @Test
    fun openEndedContainerSplitsAtTheFold() {
        val split = splitAtFold(Rect(0f, 0f, 1000f, Float.POSITIVE_INFINITY), horizontal(300f, 340f))!!
        assertEquals(Rect(0f, 0f, 1000f, 300f), split.first)
        assertEquals(Rect(0f, 340f, 1000f, Float.POSITIVE_INFINITY), split.second)
    }
}
