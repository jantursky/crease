package com.jantursky.crease.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.jantursky.crease.FoldState
import com.jantursky.crease.ProvideFoldState

/**
 * Sets [content] with `rememberFoldState()` returning [initial], and returns the controller that changes the
 * state later without recreating the composition.
 */
@OptIn(ExperimentalTestApi::class)
public fun ComposeUiTest.setContentWithFold(
    initial: FoldState = FoldState.None,
    content: @Composable () -> Unit,
): FoldController {
    val controller = FoldController(initial, this)
    setContent { ProvideFoldState(controller.state, content) }
    return controller
}
