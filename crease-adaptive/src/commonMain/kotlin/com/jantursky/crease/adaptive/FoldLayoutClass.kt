package com.jantursky.crease.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass
import com.jantursky.crease.Posture
import com.jantursky.crease.rememberFoldState

/**
 * The window size class and the Crease [Posture] together, so one `when` can pick a layout.
 *
 * Test the size class with `windowSizeClass.isWidthAtLeastBreakpoint(...)` and
 * `windowSizeClass.isHeightAtLeastBreakpoint(...)`.
 */
@Immutable
public data class FoldLayoutClass(
    val windowSizeClass: WindowSizeClass,
    val posture: Posture,
)

/**
 * The current [FoldLayoutClass]. It is recomputed when the window size class or the posture changes, and the
 * two change independently.
 */
@Composable
public fun rememberFoldLayoutClass(): FoldLayoutClass {
    val sizeClass = rememberWindowAdaptiveInfo().windowSizeClass
    val posture = rememberFoldState().posture
    return remember(sizeClass, posture) { FoldLayoutClass(sizeClass, posture) }
}
