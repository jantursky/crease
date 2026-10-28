package com.jantursky.crease.test

import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FoldStatePresetsTest {
    private val presets = listOf(FoldStatePresets.Seamless, FoldStatePresets.Divided)

    @Test
    fun everyPresetBuildsEveryPosture() {
        for (preset in presets) {
            assertEquals(Posture.Closed, preset.closed().posture)
            assertEquals(Posture.Flat, preset.flat().posture)
            assertEquals(Posture.Book, preset.book().posture)
            assertEquals(Posture.Tabletop, preset.tabletop().posture)
        }
    }

    @Test
    fun closedEqualsTheCoreFactory() {
        for (preset in presets) assertEquals(FoldState.closed(), preset.closed())
    }

    @Test
    fun tabletopFoldIsHorizontalAndCentered() {
        val preset = FoldStatePresets.Divided
        val fold = checkNotNull(preset.tabletop().fold)
        assertEquals(FoldOrientation.Horizontal, fold.orientation)
        assertTrue(fold.isSeparating)
        assertEquals(preset.windowSize.height / 2f, fold.bounds.center.y)
    }

    @Test
    fun dividedFoldOccludesAndSeamlessDoesNot() {
        assertTrue(checkNotNull(FoldStatePresets.Divided.book().fold).isOccluding)
        assertFalse(checkNotNull(FoldStatePresets.Seamless.book().fold).isOccluding)
    }

    @Test
    fun angleAndOrientationAreForwarded() {
        val book = FoldStatePresets.Seamless.book(angle = 120f)
        assertEquals(120f, book.hinge?.angle)
        val flat = FoldStatePresets.Seamless.flat(FoldOrientation.Horizontal)
        assertEquals(FoldOrientation.Horizontal, checkNotNull(flat.fold).orientation)
    }
}
