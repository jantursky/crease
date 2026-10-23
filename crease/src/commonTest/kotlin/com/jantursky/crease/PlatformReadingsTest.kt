package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlatformReadingsTest {
    @Test
    fun angleThresholds() {
        assertEquals(HingeStatus.Closed, hingeStatusFromAngle(0f))
        assertEquals(HingeStatus.Closed, hingeStatusFromAngle(10f))
        assertEquals(HingeStatus.PartiallyOpen, hingeStatusFromAngle(10.5f))
        assertEquals(HingeStatus.PartiallyOpen, hingeStatusFromAngle(90f))
        assertEquals(HingeStatus.PartiallyOpen, hingeStatusFromAngle(169.9f))
        assertEquals(HingeStatus.FullyOpen, hingeStatusFromAngle(170f))
        assertEquals(HingeStatus.FullyOpen, hingeStatusFromAngle(180f))
    }

    @Test
    fun androidFeatureStatusWinsOverTheAngle() {
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 175f), androidHinge(HingeStatus.PartiallyOpen, 175f))
        assertEquals(Hinge(HingeStatus.FullyOpen, null), androidHinge(HingeStatus.FullyOpen, null))
    }

    @Test
    fun androidAngleAloneDecidesTheStatus() {
        assertEquals(Hinge(HingeStatus.Closed, 2f), androidHinge(null, 2f))
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 95f), androidHinge(null, 95f))
        assertEquals(Hinge(HingeStatus.FullyOpen, 179f), androidHinge(null, 179f))
    }

    @Test
    fun androidWithoutFeatureOrSensorHasNoHinge() {
        assertNull(androidHinge(null, null))
    }

    @Test
    fun radiansBecomeDegreesClampedToTheHingeRange() {
        assertEquals(0f, radiansToDegrees(0.0))
        assertEquals(90f, radiansToDegrees(PI / 2), 0.001f)
        assertEquals(180f, radiansToDegrees(PI), 0.001f)
        assertEquals(0f, radiansToDegrees(-0.2))
        assertEquals(180f, radiansToDegrees(PI + 0.3))
    }

    @Test
    fun iosRawStatuses() {
        assertNull(iosHingeStatus(0))
        assertEquals(HingeStatus.Closed, iosHingeStatus(1))
        assertEquals(HingeStatus.PartiallyOpen, iosHingeStatus(2))
        assertEquals(HingeStatus.FullyOpen, iosHingeStatus(3))
        assertNull(iosHingeStatus(4))
        assertNull(iosHingeStatus(-1))
    }

    @Test
    fun regionConvertsPointsToPixels() {
        assertEquals(Rect(30f, 0f, 150f, 300f), RawRegion(10.0, 0.0, 40.0, 100.0, isActive = true).toPixels(3f))
    }

    private val division = RawRegion(455.5, 0.0, 40.0, 669.0, isActive = true)

    private fun reading(
        hasHinge: Boolean = true,
        status: Long = 2,
        radians: Double = PI / 2,
        divisions: List<RawRegion> = listOf(division),
        occlusions: List<RawRegion> = emptyList(),
    ) = IosReading(hasHinge, status, radians, divisions, occlusions)

    @Test
    fun iosWithoutHingeReportsNothingButOcclusions() {
        val camera = RawRegion(0.0, 0.0, 10.0, 10.0, isActive = true)
        val state = iosFoldState(reading(hasHinge = false, occlusions = listOf(camera)), scale = 2f)
        assertNull(state.hinge)
        assertTrue(state.folds.isEmpty())
        assertEquals(listOf(Rect(0f, 0f, 20f, 20f)), state.cutouts)
    }

    @Test
    fun iosPartiallyOpenPublishesTheDivisionAsAnOccludingFold() {
        val state = iosFoldState(reading(), scale = 2f)
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 90f), state.hinge)
        assertEquals(
            Fold(Rect(911f, 0f, 991f, 1338f), FoldOrientation.Vertical, isSeparating = true, isOccluding = true),
            state.fold,
        )
        assertEquals(Posture.Book, state.posture)
    }

    @Test
    fun iosDivisionThatHasNotCaughtUpSeparatesButDoesNotOcclude() {
        val lagging = RawRegion(455.5, 0.0, 40.0, 669.0, isActive = false)
        val fold = iosFoldState(reading(divisions = listOf(lagging)), scale = 1f).fold!!
        assertTrue(fold.isSeparating)
        assertEquals(false, fold.isOccluding)
    }

    @Test
    fun iosFlatOrClosedHidesTheDivision() {
        val flat = iosFoldState(reading(status = 3, radians = PI), scale = 1f)
        assertEquals(Hinge(HingeStatus.FullyOpen, 180f), flat.hinge)
        assertTrue(flat.folds.isEmpty())
        val closed = iosFoldState(reading(status = 1, radians = 0.0), scale = 1f)
        assertEquals(Posture.Closed, closed.posture)
        assertTrue(closed.folds.isEmpty())
    }

    @Test
    fun iosUnknownStatusFallsBackToTheAngle() {
        assertEquals(HingeStatus.PartiallyOpen, iosFoldState(reading(status = 0), scale = 1f).hinge?.status)
        assertEquals(HingeStatus.FullyOpen, iosFoldState(reading(status = 0, radians = PI), scale = 1f).hinge?.status)
    }

    @Test
    fun iosHorizontalDivisionInPortrait() {
        val rotated = RawRegion(0.0, 455.5, 669.0, 40.0, isActive = true)
        val state = iosFoldState(reading(divisions = listOf(rotated)), scale = 1f)
        assertEquals(FoldOrientation.Horizontal, state.fold?.orientation)
        assertEquals(Posture.Tabletop, state.posture)
    }

    @Test
    fun iosEmptyDivisionIsDroppedAndZeroWidthNeverOccludes() {
        val empty = RawRegion(0.0, 0.0, 0.0, 0.0, isActive = true)
        assertTrue(iosFoldState(reading(divisions = listOf(empty)), scale = 1f).folds.isEmpty())
        val line = RawRegion(475.5, 0.0, 0.0, 669.0, isActive = true)
        val fold = iosFoldState(reading(divisions = listOf(line)), scale = 1f).fold!!
        assertEquals(FoldOrientation.Vertical, fold.orientation)
        assertEquals(false, fold.isOccluding)
    }
}
