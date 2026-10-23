package com.jantursky.crease.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import com.jantursky.crease.FoldState

/** The fold state this platform reports for the window hosting the composition. */
@Composable
internal expect fun rememberPlatformFoldState(): FoldState

/**
 * A reader's hold on the platform's fold state. [state] is snapshot state, so reading it during layout
 * schedules a new layout when it changes. Call [release] when done.
 */
internal class FoldStateSubscription(
    val state: State<FoldState>,
    val release: () -> Unit,
)

/**
 * Subscribes [node] to the fold state of the window hosting it, for modifier nodes, which cannot call
 * composables. Call it once the node is attached.
 */
internal expect fun <T> subscribeToPlatformFoldState(
    node: T,
): FoldStateSubscription
    where T : Modifier.Node, T : CompositionLocalConsumerModifierNode
