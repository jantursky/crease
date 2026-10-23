package com.jantursky.crease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class FoldAwareTwoPaneTest {
    @Test
    fun firstOnlyFallbackShowsOnlyTheFirstPane() =
        runWindowTest {
            setWindowContent {
                FoldAwareTwoPane(first = { Pane("first") }, second = { Pane("second") })
            }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("first"))
            onNodeWithTag("second").assertDoesNotExist()
        }

    @Test
    fun sideBySideFallbackSplitsInHalfInARow() =
        runWindowTest {
            setWindowContent {
                FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.SideBySide)
            }
            assertEquals(Rect(0f, 0f, 500f, 800f), boundsOf("first"))
            assertEquals(Rect(500f, 0f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun stackedFallbackSplitsInHalfInAColumn() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.flat(window)) {
                    FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.Stacked)
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 400f), boundsOf("first"))
            assertEquals(Rect(0f, 400f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun verticalFoldSplitsSideBySideAtTheFold() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(400f)) { FoldAwareTwoPane({ Pane("first") }, { Pane("second") }) }
            }
            assertEquals(Rect(0f, 0f, 400f, 800f), boundsOf("first"))
            assertEquals(Rect(400f, 0f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun occludingVerticalFoldLeavesAGap() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.book(window, foldWidth = 40f)) {
                    FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.Stacked)
                }
            }
            assertEquals(Rect(0f, 0f, 480f, 800f), boundsOf("first"))
            assertEquals(Rect(520f, 0f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun horizontalFoldSplitsTopAndBottomWithTheGap() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window, foldWidth = 20f)) {
                    FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.SideBySide)
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 390f), boundsOf("first"))
            assertEquals(Rect(0f, 410f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun foldIsConvertedIntoTheLayoutsCoordinates() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(600f)) {
                    Placed(x = 200, y = 100, width = 800, height = 600) {
                        FoldAwareTwoPane({ Pane("first") }, { Pane("second") })
                    }
                }
            }
            assertEquals(Rect(200f, 100f, 600f, 700f), boundsOf("first"))
            assertEquals(Rect(600f, 100f, 1000f, 700f), boundsOf("second"))
        }

    @Test
    fun foldOutsideTheLayoutUsesTheFallback() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(100f)) {
                    Placed(x = 200, y = 0, width = 800, height = 800) {
                        FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.SideBySide)
                    }
                }
            }
            assertEquals(Rect(200f, 0f, 600f, 800f), boundsOf("first"))
            assertEquals(Rect(600f, 0f, 1000f, 800f), boundsOf("second"))
        }

    @Test
    fun seamlessFlatFoldDoesNotSplit() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.flat(window)) { FoldAwareTwoPane({ Pane("first") }, { Pane("second") }) }
            }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("first"))
            onNodeWithTag("second").assertDoesNotExist()
        }

    @Test
    fun followsTheFoldAsItChanges() =
        runWindowTest {
            var state by mutableStateOf(FoldState.None)
            setWindowContent {
                ProvideFoldState(state) { FoldAwareTwoPane({ Pane("first") }, { Pane("second") }) }
            }
            onNodeWithTag("second").assertDoesNotExist()
            state = FoldState.book(window)
            waitForIdle()
            assertEquals(Rect(0f, 0f, 500f, 800f), boundsOf("first"))
            assertEquals(Rect(500f, 0f, 1000f, 800f), boundsOf("second"))
            state = FoldState.flat(window)
            waitForIdle()
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("first"))
            onNodeWithTag("second").assertDoesNotExist()
        }
}
