package com.jantursky.crease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class TabletopLayoutTest {
    @Test
    fun withoutAFoldTopFillsTheLayout() =
        runWindowTest {
            setWindowContent { TabletopLayout(top = { Pane("top") }, bottom = { Pane("bottom") }) }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("top"))
            onNodeWithTag("bottom").assertDoesNotExist()
        }

    @Test
    fun tabletopPutsTopAboveTheFoldAndBottomBelow() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window, foldWidth = 40f)) {
                    TabletopLayout(top = { Pane("top") }, bottom = { Pane("bottom") }, flat = { Pane("flat") })
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 380f), boundsOf("top"))
            assertEquals(Rect(0f, 420f, 1000f, 800f), boundsOf("bottom"))
            onNodeWithTag("flat").assertDoesNotExist()
        }

    @Test
    fun bookPostureShowsFlat() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.book(window)) {
                    TabletopLayout(top = { Pane("top") }, bottom = { Pane("bottom") }, flat = { Pane("flat") })
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("flat"))
            onNodeWithTag("top").assertDoesNotExist()
            onNodeWithTag("bottom").assertDoesNotExist()
        }

    @Test
    fun tabletopFoldOutsideTheLayoutShowsFlat() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window)) {
                    Placed(x = 0, y = 0, width = 1000, height = 300) {
                        TabletopLayout(top = { Pane("top") }, bottom = { Pane("bottom") }, flat = { Pane("flat") })
                    }
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 300f), boundsOf("flat"))
        }

    @Test
    fun switchesWithThePostureAndKeepsTopsState() =
        runWindowTest {
            var state by mutableStateOf(FoldState.None)
            val seen = mutableListOf<Any>()
            setWindowContent {
                ProvideFoldState(state) {
                    TabletopLayout(
                        top = {
                            seen += remember { Any() }
                            Pane("top")
                        },
                        bottom = { Pane("bottom") },
                    )
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("top"))
            state = FoldState.tabletop(window)
            waitForIdle()
            assertEquals(Rect(0f, 0f, 1000f, 400f), boundsOf("top"))
            assertEquals(Rect(0f, 400f, 1000f, 800f), boundsOf("bottom"))
            state = FoldState.closed()
            waitForIdle()
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("top"))
            onNodeWithTag("bottom").assertDoesNotExist()
            assertSame(seen.first(), seen.last())
        }
}
