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
import androidx.compose.ui.platform.LocalView
import com.jantursky.crease.FoldState

@Composable
internal actual fun rememberPlatformFoldState(): FoldState {
    val view = LocalView.current
    var source by remember(view) { mutableStateOf<AndroidFoldSource?>(null) }
    DisposableEffect(view) {
        val acquired = AndroidFoldSource.acquire(view)
        source = acquired
        onDispose { acquired.release() }
    }
    return source?.state?.value ?: FoldState.None
}

internal actual fun <T> subscribeToPlatformFoldState(
    node: T,
): FoldStateSubscription
    where T : Modifier.Node, T : CompositionLocalConsumerModifierNode {
    val source = AndroidFoldSource.acquire(node.currentValueOf(LocalView))
    return FoldStateSubscription(source.state, source::release)
}
