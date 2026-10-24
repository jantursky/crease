package com.jantursky.crease.cover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class RememberCoverDisplayStateTest {
    @Test
    fun desktopIsUnsupportedAndShowsNothing() =
        runComposeUiTest {
            var state: CoverDisplayState? = null
            var composed = false
            setContent {
                state = rememberCoverDisplayState()
                CoverDisplay { composed = true }
            }
            assertEquals(CoverDisplayState.Unsupported, state)
            assertFalse(composed)
        }

    @Test
    fun providedStateWinsAndTheInnermostOneApplies() =
        runComposeUiTest {
            var outer: CoverDisplayState? = null
            var inner: CoverDisplayState? = null
            setContent {
                ProvideCoverDisplayState(CoverDisplayState.Available) {
                    outer = rememberCoverDisplayState()
                    ProvideCoverDisplayState(CoverDisplayState.Active) { inner = rememberCoverDisplayState() }
                }
            }
            assertEquals(CoverDisplayState.Available, outer)
            assertEquals(CoverDisplayState.Active, inner)
        }

    @Test
    fun theProvidedStateFollowsItsSource() =
        runComposeUiTest {
            var provided by mutableStateOf<CoverDisplayState>(CoverDisplayState.Unavailable)
            var state: CoverDisplayState? = null
            setContent { ProvideCoverDisplayState(provided) { state = rememberCoverDisplayState() } }
            assertEquals(CoverDisplayState.Unavailable, state)
            provided = CoverDisplayState.Active
            waitForIdle()
            assertEquals(CoverDisplayState.Active, state)
        }

    @Test
    fun aProvidedStateComposesNoCoverContent() =
        runComposeUiTest {
            var composed = false
            setContent { ProvideCoverDisplayState(CoverDisplayState.Active) { CoverDisplay { composed = true } } }
            assertFalse(composed)
        }

    @Test
    fun theEnabledFlagCanChangeWhileComposed() =
        runComposeUiTest {
            var enabled by mutableStateOf(true)
            var state: CoverDisplayState? = null
            setContent {
                state = rememberCoverDisplayState()
                CoverDisplay(enabled = enabled) {}
            }
            enabled = false
            waitForIdle()
            assertEquals(CoverDisplayState.Unsupported, state)
        }

    @Test
    fun recomposingTheCallerKeepsTheCoverDisplayStable() =
        runComposeUiTest {
            var tick by mutableStateOf(0)
            var reads = 0
            setContent {
                tick.let { reads++ }
                ProvideCoverDisplayState(CoverDisplayState.Available) {
                    rememberCoverDisplayState()
                }
                CoverDisplay {}
                CoverDisplay(enabled = false) {}
            }
            tick = 1
            waitForIdle()
            tick = 2
            waitForIdle()
            assertEquals(3, reads)
        }
}

@OptIn(ExperimentalTestApi::class)
class RememberCameraDirectionsTest {
    private val directions =
        CameraDirections(
            forwardFacing = listOf(CameraDescriptor("a", "Back Camera", "wide", CameraPosition.Back)),
            backwardFacing = emptyList(),
        )

    @Test
    fun desktopCannotTellWhichWayTheCamerasFace() =
        runComposeUiTest {
            var read: CameraDirections? = directions
            setContent { read = rememberCameraDirections() }
            assertEquals(null, read)
        }

    @Test
    fun providedDirectionsWin() =
        runComposeUiTest {
            var read: CameraDirections? = null
            setContent { ProvideCameraDirections(directions) { read = rememberCameraDirections() } }
            assertEquals(directions, read)
        }

    @Test
    fun providingNullKeepsThePlatformAnswer() =
        runComposeUiTest {
            var read: CameraDirections? = directions
            setContent { ProvideCameraDirections(null) { read = rememberCameraDirections() } }
            assertEquals(null, read)
        }
}
