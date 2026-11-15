package com.jantursky.crease

import androidx.compose.ui.test.ExperimentalTestApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PaneOrderTest {
    private fun androidx.compose.ui.test.ComposeUiTest.assertFirstBeforeSecond(
        first: String,
        second: String,
    ) = assertTrue(paneOrderOf(first) < paneOrderOf(second), "$first should be read before $second")

    @Test
    fun bookFoldReadsFirstPaneBeforeSecond() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(400f)) { FoldAwareTwoPane({ Pane("first") }, { Pane("second") }) }
            }
            assertFirstBeforeSecond("first", "second")
        }

    @Test
    fun sideBySideFallbackReadsFirstPaneBeforeSecond() =
        runWindowTest {
            setWindowContent {
                FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.SideBySide)
            }
            assertFirstBeforeSecond("first", "second")
        }

    @Test
    fun stackedFallbackReadsFirstPaneBeforeSecond() =
        runWindowTest {
            setWindowContent {
                FoldAwareTwoPane({ Pane("first") }, { Pane("second") }, fallback = TwoPaneFallback.Stacked)
            }
            assertFirstBeforeSecond("first", "second")
        }

    @Test
    fun tabletopReadsTopBeforeBottom() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window)) { TabletopLayout({ Pane("top") }, { Pane("bottom") }) }
            }
            assertFirstBeforeSecond("top", "bottom")
        }

    @Test
    fun flatTabletopLayoutKeepsTheFirstSlotOrder() =
        runWindowTest {
            setWindowContent {
                TabletopLayout({ Pane("top") }, { Pane("bottom") }, flat = { Pane("flat") })
            }
            assertEquals(0f, paneOrderOf("flat"))
        }
}
