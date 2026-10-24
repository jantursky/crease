package com.jantursky.crease.cover

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformReadingsTest {
    @Test
    fun unsupportedWinsOverEverything() {
        assertEquals(
            CoverDisplayState.Unsupported,
            coverDisplayState(supported = false, available = true, presenting = true),
        )
        assertEquals(
            CoverDisplayState.Unsupported,
            coverDisplayState(supported = false, available = false, presenting = false),
        )
    }

    @Test
    fun presentingMeansActive() {
        assertEquals(CoverDisplayState.Active, coverDisplayState(supported = true, available = true, presenting = true))
        assertEquals(
            CoverDisplayState.Active,
            coverDisplayState(supported = true, available = false, presenting = true),
        )
    }

    @Test
    fun availableButNotPresentingMeansAvailable() {
        assertEquals(
            CoverDisplayState.Available,
            coverDisplayState(supported = true, available = true, presenting = false),
        )
    }

    @Test
    fun supportedButNothingElseMeansUnavailable() {
        assertEquals(
            CoverDisplayState.Unavailable,
            coverDisplayState(supported = true, available = false, presenting = false),
        )
    }

    @Test
    fun onlyUnsupportedIsNotSupported() {
        assertEquals(false, CoverDisplayState.Unsupported.isSupported)
        listOf(CoverDisplayState.Unavailable, CoverDisplayState.Available, CoverDisplayState.Active).forEach {
            assertEquals(true, it.isSupported)
        }
    }
}
