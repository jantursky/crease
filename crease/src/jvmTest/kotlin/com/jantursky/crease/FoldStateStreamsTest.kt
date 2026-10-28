package com.jantursky.crease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FoldStateStreamsTest {
    @Test
    fun flowHasTheCurrentStateFromTheFirstRead() =
        runComposeUiTest {
            var flow: StateFlow<FoldState>? = null
            var direct: FoldState? = null
            setContent {
                ProvideFoldState(verticalFold(500f)) {
                    flow = rememberFoldStateFlow()
                    direct = rememberFoldState()
                }
            }
            assertEquals(direct, flow?.value)
        }

    @Test
    fun flowFollowsTheProvidedState() =
        runComposeUiTest {
            var state by mutableStateOf(FoldState.flat(window))
            var flow: StateFlow<FoldState>? = null
            setContent {
                ProvideFoldState(state) { flow = rememberFoldStateFlow() }
            }
            assertEquals(Posture.Flat, flow?.value?.posture)
            state = FoldState.book(window)
            waitForIdle()
            assertEquals(Posture.Book, flow?.value?.posture)
        }

    @Test
    fun flowKeepsTheLastValueAfterTheCompositionLeaves() =
        runComposeUiTest {
            var state by mutableStateOf(FoldState.book(window))
            var present by mutableStateOf(true)
            var flow: StateFlow<FoldState>? = null
            setContent {
                ProvideFoldState(state) {
                    if (present) flow = rememberFoldStateFlow()
                }
            }
            present = false
            waitForIdle()
            state = FoldState.tabletop(window)
            waitForIdle()
            assertEquals(Posture.Book, flow?.value?.posture)
        }

    @Test
    fun posturesSkipAngleChangesAndEmitPostureChanges() =
        runComposeUiTest {
            var state by mutableStateOf(FoldState.book(window, angle = 80f))
            var flow: StateFlow<FoldState>? = null
            setContent {
                ProvideFoldState(state) { flow = rememberFoldStateFlow() }
            }
            val seen = mutableListOf<Posture>()
            val postures = checkNotNull(flow).postures()
            val collected = runBlocking { postures.take(1).toList() }
            seen += collected
            state = FoldState.book(window, angle = 85f)
            waitForIdle()
            state = FoldState.tabletop(window)
            waitForIdle()
            seen += runBlocking { postures.first() }
            assertEquals(listOf(Posture.Book, Posture.Tabletop), seen)
        }

    @Test
    fun transitionIsUnchangedOnFirstComposition() =
        runComposeUiTest {
            var transition: PostureTransition? = null
            setContent {
                ProvideFoldState(FoldState.flat(window)) { transition = rememberPostureTransition() }
            }
            assertEquals(Posture.Flat, transition?.previous)
            assertEquals(Posture.Flat, transition?.current)
            assertFalse(checkNotNull(transition).changed)
        }

    @Test
    fun transitionReportsAChangeAndIgnoresAngleChanges() =
        runComposeUiTest {
            var state by mutableStateOf(FoldState.flat(window))
            var transition: PostureTransition? = null
            setContent {
                ProvideFoldState(state) {
                    transition = rememberPostureTransition()
                }
            }
            state = FoldState.tabletop(window, angle = 90f)
            waitForIdle()
            assertEquals(PostureTransition(Posture.Flat, Posture.Tabletop), transition)
            assertTrue(checkNotNull(transition).changed)
            state = FoldState.tabletop(window, angle = 100f)
            waitForIdle()
            assertEquals(PostureTransition(Posture.Flat, Posture.Tabletop), transition)
        }
}
