package com.jantursky.crease.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.uikit.LocalUIViewController
import com.jantursky.crease.FoldState

@Composable
internal actual fun rememberPlatformFoldState(): FoldState {
    val host = LocalUIViewController.current.view
    var source by remember(host) { mutableStateOf<IosFoldSource?>(null) }
    DisposableEffect(host) {
        val acquired = IosFoldSource.acquire(host)
        source = acquired
        onDispose { acquired.release() }
    }
    return source?.state?.value ?: FoldState.None
}

internal actual fun <T> subscribeToPlatformFoldState(
    node: T,
): FoldStateSubscription
    where T : Modifier.Node, T : CompositionLocalConsumerModifierNode {
    val source = IosFoldSource.acquire(node.currentValueOf(LocalUIViewController).view)
    return FoldStateSubscription(source.state, source::release)
}
