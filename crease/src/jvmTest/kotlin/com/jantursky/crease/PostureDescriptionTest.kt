package com.jantursky.crease

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PostureDescriptionTest {
    private fun ComposeUiTest.description() =
        onNodeWithTag("node").fetchSemanticsNode().config[SemanticsProperties.StateDescription]

    @Test
    fun descriptionFollowsThePosture() =
        runWindowTest {
            var state by mutableStateOf(FoldState.flat(window))
            setWindowContent {
                ProvideFoldState(state) {
                    Box(Modifier.testTag("node").postureDescription { "posture ${it.name}" })
                }
            }
            assertEquals("posture Flat", description())
            state = FoldState.book(window)
            waitForIdle()
            assertEquals("posture Book", description())
        }

    @Test
    fun angleOnlyChangeKeepsTheDescription() =
        runWindowTest {
            var state by mutableStateOf(FoldState.book(window, angle = 80f))
            setWindowContent {
                ProvideFoldState(state) {
                    Box(Modifier.testTag("node").postureDescription { "posture ${it.name}" })
                }
            }
            val before = description()
            state = FoldState.book(window, angle = 120f)
            waitForIdle()
            assertEquals(before, description())
        }

    @Test
    fun callerTextIsExposedAsAPoliteLiveRegion() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window)) {
                    Box(Modifier.testTag("node").postureDescription { "Tischmodus" })
                }
            }
            assertEquals("Tischmodus", description())
            assertEquals(
                LiveRegionMode.Polite,
                onNodeWithTag("node").fetchSemanticsNode().config[SemanticsProperties.LiveRegion],
            )
        }
}
