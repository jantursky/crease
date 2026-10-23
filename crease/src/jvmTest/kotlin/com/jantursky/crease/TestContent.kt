package com.jantursky.crease

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/** The test window, in pixels. Density is 1, so dp and px are the same. */
val window = Size(1000f, 800f)

/** Runs [block] in a test window of exactly [window] size. */
@OptIn(ExperimentalTestApi::class)
fun runWindowTest(block: ComposeUiTest.() -> Unit) =
    runDesktopComposeUiTest(window.width.toInt(), window.height.toInt()) { block() }

/** Runs [content] at density 1 in a [window]-sized box at the top-left of the test window. */
@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.setWindowContent(content: @Composable () -> Unit) {
    setContent {
        CompositionLocalProvider(LocalDensity provides Density(1f)) {
            Box(Modifier.size(window.width.dp, window.height.dp)) { content() }
        }
    }
}

/** A box that fills its pane and can be found by [tag]. */
@Composable
fun Pane(tag: String) {
    Box(Modifier.fillMaxSize().testTag(tag))
}

/** A [width] x [height] box at ([x], [y]) holding [content]. */
@Composable
fun Placed(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    content: @Composable () -> Unit,
) {
    Box(Modifier.offset(x.dp, y.dp).size(width.dp, height.dp)) { content() }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.boundsOf(tag: String): Rect = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

fun verticalFold(
    left: Float,
    right: Float = left,
) = FoldState(
    hinge = Hinge(HingeStatus.PartiallyOpen, 90f),
    folds = listOf(Fold(Rect(left, 0f, right, 800f), FoldOrientation.Vertical, true, right > left)),
    cutouts = emptyList(),
)
