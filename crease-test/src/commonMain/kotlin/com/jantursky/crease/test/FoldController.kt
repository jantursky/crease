package com.jantursky.crease.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture

/**
 * Changes the [FoldState] of a composition made by [setContentWithFold] while it stays alive, so state the
 * content remembers survives. Every change waits for the composition to settle before it returns.
 */
@OptIn(ExperimentalTestApi::class)
public class FoldController internal constructor(
    initial: FoldState,
    private val test: ComposeUiTest,
) {
    private var current by mutableStateOf(initial)

    /** The state the content currently sees. */
    public val state: FoldState get() = current

    /** Makes the content see [state] and waits until it has recomposed. */
    public fun update(state: FoldState) {
        test.runOnIdle { current = state }
        test.waitForIdle()
    }

    /**
     * Makes the content see [posture] in a window of [windowSize] pixels. [angle] and [foldWidth] apply to
     * Book and Tabletop, and [foldWidth] also to Flat, which folds along [orientation].
     */
    public fun moveTo(
        posture: Posture,
        windowSize: Size,
        angle: Float = 90f,
        foldWidth: Float = 0f,
        orientation: FoldOrientation = FoldOrientation.Vertical,
    ) {
        update(stateFor(posture, windowSize, angle, foldWidth, orientation))
    }

    /** Applies [states] in order, calling [afterEach] once the content has settled on each one. */
    public fun play(
        states: List<FoldState>,
        afterEach: (FoldState) -> Unit = {},
    ) {
        for (next in states) {
            update(next)
            afterEach(next)
        }
    }
}

internal fun stateFor(
    posture: Posture,
    windowSize: Size,
    angle: Float,
    foldWidth: Float,
    orientation: FoldOrientation,
): FoldState =
    when (posture) {
        Posture.Closed -> FoldState.closed()
        Posture.Flat -> FoldState.flat(windowSize, orientation, foldWidth)
        Posture.Book -> FoldState.book(windowSize, angle, foldWidth)
        Posture.Tabletop -> FoldState.tabletop(windowSize, angle, foldWidth)
    }
