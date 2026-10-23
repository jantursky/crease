package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ValueSemanticsTest {
    private val fold = Fold(Rect(10f, 0f, 20f, 100f), FoldOrientation.Vertical, isSeparating = true, isOccluding = true)

    @Test
    fun hingesWithTheSameValuesAreEqual() {
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 90f), Hinge(HingeStatus.PartiallyOpen, 90f))
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 90f).hashCode(), Hinge(HingeStatus.PartiallyOpen, 90f).hashCode())
        assertEquals(Hinge(HingeStatus.FullyOpen, null), Hinge(HingeStatus.FullyOpen, null))
    }

    @Test
    fun hingesDifferByStatusAndAngle() {
        assertNotEquals(Hinge(HingeStatus.PartiallyOpen, 90f), Hinge(HingeStatus.FullyOpen, 90f))
        assertNotEquals(Hinge(HingeStatus.PartiallyOpen, 90f), Hinge(HingeStatus.PartiallyOpen, 91f))
        assertNotEquals(Hinge(HingeStatus.PartiallyOpen, 90f), Hinge(HingeStatus.PartiallyOpen, null))
        assertNotEquals<Any>(Hinge(HingeStatus.Closed, 0f), "Hinge")
    }

    @Test
    fun hingeToStringNamesItsValues() {
        assertEquals("Hinge(status=PartiallyOpen, angle=null)", Hinge(HingeStatus.PartiallyOpen, null).toString())
    }

    @Test
    fun foldsWithTheSameValuesAreEqual() {
        val copy = Fold(Rect(10f, 0f, 20f, 100f), FoldOrientation.Vertical, isSeparating = true, isOccluding = true)
        assertEquals(fold, copy)
        assertEquals(fold.hashCode(), copy.hashCode())
    }

    @Test
    fun foldsDifferByEveryProperty() {
        assertNotEquals(fold, Fold(Rect(11f, 0f, 20f, 100f), fold.orientation, fold.isSeparating, fold.isOccluding))
        assertNotEquals(fold, Fold(fold.bounds, FoldOrientation.Horizontal, fold.isSeparating, fold.isOccluding))
        assertNotEquals(fold, Fold(fold.bounds, fold.orientation, isSeparating = false, isOccluding = true))
        assertNotEquals(fold, Fold(fold.bounds, fold.orientation, isSeparating = true, isOccluding = false))
        assertNotEquals<Any>(fold, fold.bounds)
    }

    @Test
    fun foldToStringNamesItsValues() {
        assertEquals(
            "Fold(bounds=${fold.bounds}, orientation=Vertical, isSeparating=true, isOccluding=true)",
            fold.toString(),
        )
    }

    @Test
    fun foldStatesWithTheSameValuesAreEqual() {
        val a = FoldState(Hinge(HingeStatus.PartiallyOpen, 90f), listOf(fold), listOf(Rect(0f, 0f, 5f, 5f)))
        val b = FoldState(Hinge(HingeStatus.PartiallyOpen, 90f), listOf(fold), listOf(Rect(0f, 0f, 5f, 5f)))
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(FoldState.None, FoldState(null, emptyList(), emptyList()))
    }

    @Test
    fun foldStatesDifferByEveryProperty() {
        val a = FoldState(Hinge(HingeStatus.PartiallyOpen, 90f), listOf(fold), emptyList())
        assertNotEquals(a, FoldState(null, listOf(fold), emptyList()))
        assertNotEquals(a, FoldState(a.hinge, emptyList(), emptyList()))
        assertNotEquals(a, FoldState(a.hinge, listOf(fold), listOf(Rect.Zero)))
        assertNotEquals<Any>(a, fold)
    }

    @Test
    fun foldStateToStringNamesItsValues() {
        assertEquals("FoldState(hinge=null, folds=[], cutouts=[])", FoldState.None.toString())
    }

    @Test
    fun firstFoldIsTheFold() {
        val second = Fold(Rect.Zero, FoldOrientation.Horizontal, isSeparating = false, isOccluding = false)
        assertEquals(fold, FoldState(null, listOf(fold, second), emptyList()).fold)
        assertEquals(null, FoldState.None.fold)
    }
}
