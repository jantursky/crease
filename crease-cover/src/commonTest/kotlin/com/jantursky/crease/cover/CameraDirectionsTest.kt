package com.jantursky.crease.cover

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CameraDirectionsTest {
    private val wide = CameraDescriptor("a", "Back Camera", "wide", CameraPosition.Back)
    private val selfie = CameraDescriptor("b", "Front Camera", "trueDepth", CameraPosition.Front)

    @Test
    fun rawPositionsMapToTheirPlace() {
        assertEquals(CameraPosition.Back, cameraPosition(1))
        assertEquals(CameraPosition.Front, cameraPosition(2))
        assertEquals(CameraPosition.Unspecified, cameraPosition(0))
        assertEquals(CameraPosition.Unspecified, cameraPosition(7))
    }

    @Test
    fun descriptorsCompareByValue() {
        assertEquals(wide, CameraDescriptor("a", "Back Camera", "wide", CameraPosition.Back))
        assertEquals(wide.hashCode(), CameraDescriptor("a", "Back Camera", "wide", CameraPosition.Back).hashCode())
        assertNotEquals(wide, selfie)
        assertNotEquals<Any>(wide, "wide")
        assertEquals("CameraDescriptor(id=a, name=Back Camera, type=wide, position=Back)", wide.toString())
    }

    @Test
    fun directionsCompareByValue() {
        val directions = CameraDirections(listOf(selfie), listOf(wide))
        assertEquals(directions, CameraDirections(listOf(selfie), listOf(wide)))
        assertEquals(directions.hashCode(), CameraDirections(listOf(selfie), listOf(wide)).hashCode())
        assertNotEquals(directions, CameraDirections(listOf(wide), listOf(selfie)))
        assertNotEquals<Any>(directions, "directions")
        assertEquals("CameraDirections(forwardFacing=[$selfie], backwardFacing=[$wide])", directions.toString())
    }
}
