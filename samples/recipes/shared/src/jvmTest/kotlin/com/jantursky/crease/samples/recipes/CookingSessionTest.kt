package com.jantursky.crease.samples.recipes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CookingSessionTest {
    private fun session() = CookingSession(sampleRecipes)

    @Test
    fun timerCountsDownAndStopsAtZero() {
        val s = session()
        s.toggleTimer()
        assertTrue(s.isRunning)
        repeat(8 * 60) { s.tick() }
        assertEquals(0, s.remainingSeconds)
        assertFalse(s.isRunning)
    }

    @Test
    fun movingToAnotherStepResetsTheTimer() {
        val s = session()
        s.toggleTimer()
        s.tick()
        s.next()
        assertFalse(s.isRunning)
        assertEquals(2 * 60, s.remainingSeconds)
    }

    @Test
    fun stepWithoutTimerCannotStart() {
        val s = session()
        s.selectStep(5)
        s.toggleTimer()
        assertFalse(s.isRunning)
    }

    @Test
    fun checkedIngredientsArePerRecipe() {
        val s = session()
        s.toggleChecked(0)
        assertTrue(s.isChecked(0))
        s.selectRecipe(1)
        assertFalse(s.isChecked(0))
    }

    @Test
    fun clockIsPadded() = assertEquals("04:05", formatClock(245))

    @Test
    fun shadeGrowsAsTheHingeCloses() {
        assertEquals(0f, shadeAlpha(180f))
        assertEquals(0f, shadeAlpha(null))
        assertTrue(shadeAlpha(40f) > shadeAlpha(120f))
    }
}
