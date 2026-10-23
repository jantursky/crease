package com.jantursky.crease.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import com.jantursky.crease.FoldState

// No platform here has a hinge or folds; ProvideFoldState is the only source of a fold state.

@Composable
internal actual fun rememberPlatformFoldState(): FoldState = FoldState.None

internal actual fun <T> subscribeToPlatformFoldState(
    node: T,
): FoldStateSubscription
    where T : Modifier.Node, T : CompositionLocalConsumerModifierNode =
    FoldStateSubscription(mutableStateOf(FoldState.None), release = {})
