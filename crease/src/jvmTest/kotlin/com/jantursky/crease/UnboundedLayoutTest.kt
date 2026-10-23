package com.jantursky.crease

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/** Under unbounded constraints the layouts take the length their content needs. */
@OptIn(ExperimentalTestApi::class)
class UnboundedLayoutTest {
    @Composable
    private fun Tall(
        tag: String,
        height: Int,
    ) {
        Box(Modifier.height(height.dp).testTag(tag))
    }

    @Composable
    private fun Scrolling(content: @Composable () -> Unit) {
        Column(Modifier.verticalScroll(rememberScrollState()).testTag("layout")) { content() }
    }

    @Test
    fun firstOnlyWrapsTheFirstPane() =
        runWindowTest {
            setWindowContent { Scrolling { FoldAwareTwoPane({ Tall("first", 300) }, { Tall("second", 50) }) } }
            assertEquals(Rect(0f, 0f, 1000f, 300f), boundsOf("first"))
            assertEquals(Rect(0f, 0f, 1000f, 300f), boundsOf("layout"))
        }

    @Test
    fun sideBySideTakesTheTallerPane() =
        runWindowTest {
            setWindowContent {
                Scrolling {
                    FoldAwareTwoPane(
                        { Tall("first", 300) },
                        { Tall("second", 120) },
                        fallback = TwoPaneFallback.SideBySide,
                    )
                }
            }
            assertEquals(Rect(0f, 0f, 500f, 300f), boundsOf("first"))
            assertEquals(Rect(500f, 0f, 1000f, 120f), boundsOf("second"))
            assertEquals(Rect(0f, 0f, 1000f, 300f), boundsOf("layout"))
        }

    @Test
    fun stackedPutsThePanesOneAfterTheOther() =
        runWindowTest {
            setWindowContent {
                Scrolling {
                    FoldAwareTwoPane(
                        { Tall("first", 300) },
                        { Tall("second", 120) },
                        fallback = TwoPaneFallback.Stacked,
                    )
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 300f), boundsOf("first"))
            assertEquals(Rect(0f, 300f, 1000f, 420f), boundsOf("second"))
        }

    @Test
    fun sideBySideInAHorizontalScrollPutsThePanesInARow() =
        runWindowTest {
            setWindowContent {
                Box(Modifier.horizontalScroll(rememberScrollState())) {
                    FoldAwareTwoPane(
                        { Box(Modifier.width(300.dp).testTag("first")) },
                        { Box(Modifier.width(200.dp).testTag("second")) },
                        fallback = TwoPaneFallback.SideBySide,
                    )
                }
            }
            assertEquals(Rect(0f, 0f, 300f, 800f), boundsOf("first"))
            assertEquals(Rect(300f, 0f, 500f, 800f), boundsOf("second"))
        }

    @Test
    fun verticalFoldStillSplitsInAVerticalScroll() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(400f)) {
                    Scrolling { FoldAwareTwoPane({ Tall("first", 200) }, { Tall("second", 260) }) }
                }
            }
            assertEquals(Rect(0f, 0f, 400f, 200f), boundsOf("first"))
            assertEquals(Rect(400f, 0f, 1000f, 260f), boundsOf("second"))
        }

    @Test
    fun tabletopInAVerticalScrollSplitsAtTheFoldAndWrapsTheBottom() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window)) {
                    Scrolling {
                        TabletopLayout(top = { Tall("top", 50) }, bottom = { Tall("bottom", 100) })
                        Box(Modifier.size(10.dp).testTag("after"))
                    }
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 400f), boundsOf("top"))
            assertEquals(Rect(0f, 400f, 1000f, 500f), boundsOf("bottom"))
            assertEquals(500f, boundsOf("after").top)
        }

    @Test
    fun flatTabletopLayoutWrapsItsContent() =
        runWindowTest {
            setWindowContent {
                Scrolling {
                    TabletopLayout(
                        top = { Tall("top", 150) },
                        bottom = { Tall("bottom", 100) },
                    )
                }
            }
            assertEquals(Rect(0f, 0f, 1000f, 150f), boundsOf("top"))
        }
}
