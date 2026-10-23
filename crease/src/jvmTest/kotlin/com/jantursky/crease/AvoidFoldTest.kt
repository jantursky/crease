package com.jantursky.crease

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AvoidFoldTest {
    @Test
    fun withoutAFoldNothingChanges() =
        runWindowTest {
            setWindowContent { AvoidingBox() }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("footprint"))
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("content"))
        }

    @Test
    fun contentMovesToTheLargerSideAndTheFootprintStays() =
        runWindowTest {
            setWindowContent { ProvideFoldState(verticalFold(300f)) { AvoidingBox() } }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("footprint"))
            assertEquals(Rect(300f, 0f, 1000f, 800f), boundsOf("content"))
        }

    @Test
    fun occludingFoldIsLeftOut() =
        runWindowTest {
            setWindowContent { ProvideFoldState(verticalFold(600f, 640f)) { AvoidingBox() } }
            assertEquals(Rect(0f, 0f, 600f, 800f), boundsOf("content"))
        }

    @Test
    fun horizontalFoldKeepsTheTallerSide() =
        runWindowTest {
            setWindowContent { ProvideFoldState(FoldState.tabletop(window, foldWidth = 40f)) { AvoidingBox() } }
            assertEquals(Rect(0f, 0f, 1000f, 380f), boundsOf("content"))
        }

    @Test
    fun foldIsFoundRelativeToTheElement() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(700f)) {
                    Placed(x = 400, y = 0, width = 600, height = 800) { AvoidingBox() }
                }
            }
            assertEquals(Rect(400f, 0f, 700f, 800f), boundsOf("content"))
        }

    @Test
    fun foldThatMissesTheElementChangesNothing() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(200f)) {
                    Placed(x = 400, y = 0, width = 600, height = 800) { AvoidingBox() }
                }
            }
            assertEquals(Rect(400f, 0f, 1000f, 800f), boundsOf("content"))
        }

    @Test
    fun seamlessFlatFoldChangesNothing() =
        runWindowTest {
            setWindowContent { ProvideFoldState(FoldState.flat(window)) { AvoidingBox() } }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("content"))
        }

    @Test
    fun followsTheProvidedStateAsItChanges() =
        runWindowTest {
            var state by mutableStateOf(FoldState.None)
            setWindowContent { ProvideFoldState(state) { AvoidingBox() } }
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("content"))
            state = verticalFold(700f)
            waitForIdle()
            assertEquals(Rect(0f, 0f, 700f, 800f), boundsOf("content"))
            state = FoldState.None
            waitForIdle()
            assertEquals(Rect(0f, 0f, 1000f, 800f), boundsOf("content"))
        }

    @Test
    fun followsTheElementWhenItMoves() =
        runWindowTest {
            var x by mutableStateOf(0)
            setWindowContent {
                ProvideFoldState(verticalFold(700f)) {
                    Placed(x = x, y = 0, width = 600, height = 800) { AvoidingBox() }
                }
            }
            assertEquals(Rect(0f, 0f, 600f, 800f), boundsOf("content"))
            x = 400
            waitForIdle()
            assertEquals(Rect(400f, 0f, 700f, 800f), boundsOf("content"))
        }
}

@Composable
private fun AvoidingBox() {
    Box(Modifier.fillMaxSize().testTag("footprint").avoidFold()) {
        Box(Modifier.fillMaxSize().testTag("content"))
    }
}

@OptIn(ExperimentalTestApi::class)
class AvoidFoldWrapContentTest {
    @Test
    fun verticalFoldPinsOnlyTheWidth() =
        runWindowTest {
            var height by mutableStateOf(100)
            setWindowContent {
                ProvideFoldState(verticalFold(300f)) {
                    Box(Modifier.fillMaxWidth().testTag("footprint").avoidFold()) {
                        Box(Modifier.fillMaxWidth().height(height.dp).testTag("content"))
                    }
                }
            }
            assertEquals(Rect(300f, 0f, 1000f, 100f), boundsOf("content"))
            // The content grows along the fold, as a paragraph or a list would.
            height = 250
            waitForIdle()
            assertEquals(Rect(300f, 0f, 1000f, 250f), boundsOf("content"))
            assertEquals(Rect(0f, 0f, 1000f, 250f), boundsOf("footprint"))
        }

    @Test
    fun horizontalFoldPinsOnlyTheHeight() =
        runWindowTest {
            var width by mutableStateOf(200)
            setWindowContent {
                ProvideFoldState(FoldState.tabletop(window, foldWidth = 40f)) {
                    Box(Modifier.fillMaxHeight().testTag("footprint").avoidFold()) {
                        Box(Modifier.fillMaxHeight().width(width.dp).testTag("content"))
                    }
                }
            }
            assertEquals(Rect(0f, 0f, 200f, 380f), boundsOf("content"))
            width = 600
            waitForIdle()
            assertEquals(Rect(0f, 0f, 600f, 380f), boundsOf("content"))
            assertEquals(Rect(0f, 0f, 600f, 800f), boundsOf("footprint"))
        }

    @Test
    fun wrapContentElementBesideTheFoldIsUntouched() =
        runWindowTest {
            setWindowContent {
                ProvideFoldState(verticalFold(700f)) {
                    Box(Modifier.testTag("footprint").avoidFold()) {
                        Box(Modifier.size(200.dp, 50.dp).testTag("content"))
                    }
                }
            }
            assertEquals(Rect(0f, 0f, 200f, 50f), boundsOf("content"))
        }
}
