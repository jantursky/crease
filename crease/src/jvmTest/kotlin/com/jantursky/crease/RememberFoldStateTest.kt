package com.jantursky.crease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class RememberFoldStateTest {
    @Test
    fun desktopReportsNoHinge() =
        runComposeUiTest {
            var state: FoldState? = null
            var hinge: Hinge? = Hinge(HingeStatus.Closed, 0f)
            setContent {
                state = rememberFoldState()
                hinge = rememberHinge()
            }
            assertEquals(FoldState.None, state)
            assertNull(hinge)
        }

    @Test
    fun providedStateWinsAndTheInnermostOneApplies() =
        runComposeUiTest {
            var outer: FoldState? = null
            var inner: FoldState? = null
            setContent {
                ProvideFoldState(FoldState.closed()) {
                    outer = rememberFoldState()
                    ProvideFoldState(FoldState.book(window)) { inner = rememberFoldState() }
                }
            }
            assertEquals(FoldState.closed(), outer)
            assertEquals(FoldState.book(window), inner)
        }

    @Test
    fun hingeFollowsTheProvidedState() =
        runComposeUiTest {
            var provided by mutableStateOf(FoldState.book(window, angle = 120f))
            var hinge: Hinge? = null
            setContent { ProvideFoldState(provided) { hinge = rememberHinge() } }
            assertEquals(Hinge(HingeStatus.PartiallyOpen, 120f), hinge)
            provided = FoldState.closed()
            waitForIdle()
            assertEquals(Hinge(HingeStatus.Closed, 0f), hinge)
            provided = FoldState.None
            waitForIdle()
            assertNull(hinge)
        }
}
