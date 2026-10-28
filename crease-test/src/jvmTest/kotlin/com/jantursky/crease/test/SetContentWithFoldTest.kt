package com.jantursky.crease.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture
import com.jantursky.crease.rememberFoldState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SetContentWithFoldTest {
    private val window = Size(1000f, 800f)

    @Test
    fun contentSeesTheInitialState() =
        runDesktopComposeUiTest {
            val seen = mutableListOf<Posture>()
            val controller =
                setContentWithFold(FoldState.book(window)) {
                    seen += rememberFoldState().posture
                }
            assertEquals(Posture.Book, seen.last())
            assertEquals(FoldState.book(window), controller.state)
        }

    @Test
    fun defaultIsNoFold() =
        runDesktopComposeUiTest {
            var seen: FoldState? = null
            setContentWithFold { seen = rememberFoldState() }
            assertEquals(FoldState.None, seen)
        }

    @Test
    fun rememberedStateSurvivesAPostureChange() =
        runDesktopComposeUiTest {
            val controller =
                setContentWithFold(FoldState.flat(window)) {
                    var count by remember { mutableStateOf(0) }
                    Box(Modifier.testTag("box").clickable { count++ }) {
                        BasicText("${rememberFoldState().posture}:$count")
                    }
                }
            onNodeWithTag("box").performClick()
            onNodeWithTag("box").performClick()
            controller.moveTo(Posture.Tabletop, window)
            onNodeWithTag("box").assertTextEquals("Tabletop:2")
        }

    @Test
    fun playAppliesStatesInOrderAndSettlesOnEach() =
        runDesktopComposeUiTest {
            val seen = mutableListOf<Posture>()
            val controller =
                setContentWithFold(FoldState.flat(window)) {
                    seen += rememberFoldState().posture
                }
            val after = mutableListOf<Posture>()
            controller.play(
                listOf(FoldState.book(window), FoldState.tabletop(window), FoldState.closed()),
            ) { after += seen.last() }
            assertEquals(listOf(Posture.Book, Posture.Tabletop, Posture.Closed), after)
        }
}
