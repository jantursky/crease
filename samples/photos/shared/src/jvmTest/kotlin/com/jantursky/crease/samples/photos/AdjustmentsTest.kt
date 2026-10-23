package com.jantursky.crease.samples.photos

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdjustmentsTest {
    private val identity =
        floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
        )

    @Test
    fun defaultsAreTheIdentity() = assertContentEquals(identity, Adjustments().colorMatrixValues())

    @Test
    fun oneStopOfExposureDoublesTheChannels() {
        val m = Adjustments(exposure = 1f).colorMatrixValues()
        assertEquals(2f, m[0], 1e-4f)
        assertEquals(2f, m[6], 1e-4f)
    }

    @Test
    fun noSaturationMakesAllChannelsEqual() {
        val m = Adjustments(saturation = -1f).colorMatrixValues()
        assertEquals(m[0], m[5], 1e-4f)
        assertEquals(m[0], m[10], 1e-4f)
    }

    @Test
    fun warmthPushesRedUpAndBlueDown() {
        val m = Adjustments(warmth = 1f).colorMatrixValues()
        assertTrue(m[4] > 0f && m[14] < 0f)
    }

    @Test
    fun tiltFollowsTheAngle() {
        assertEquals(0f, tiltDegrees(null))
        assertEquals(0f, tiltDegrees(90f))
        assertTrue(tiltDegrees(170f) > 0f && tiltDegrees(30f) < 0f)
    }
}
