package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * Where a layout's top-left corner sits in the window, or `null` before its first placement. Fold
 * bounds are window coordinates, so layouts need this to find the fold in their own space.
 */
@Composable
internal fun rememberWindowOrigin(): MutableState<Offset?> = remember { mutableStateOf(null) }

internal fun Modifier.trackWindowOrigin(origin: MutableState<Offset?>): Modifier =
    onGloballyPositioned { origin.value = it.positionInWindow() }
