package com.jantursky.crease.cover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.jantursky.crease.cover.platform.PlatformCoverDisplay
import com.jantursky.crease.cover.platform.rememberPlatformCoverDisplayState

/** The state set by [ProvideCoverDisplayState], or `null` to use the platform's. */
internal val LocalCoverDisplayStateOverride = compositionLocalOf<CoverDisplayState?> { null }

/**
 * The current [CoverDisplayState], updated as the system allows or withdraws the outer display.
 *
 * Returns the state passed to the nearest [ProvideCoverDisplayState] when there is one, otherwise the state
 * the platform reports. JVM and wasm targets report [CoverDisplayState.Unsupported].
 */
@Composable
public fun rememberCoverDisplayState(): CoverDisplayState =
    LocalCoverDisplayStateOverride.current ?: rememberPlatformCoverDisplayState()

/**
 * Makes [rememberCoverDisplayState] return [state] inside [content]. Use it for previews, tests and simulators.
 * A [CoverDisplay] inside it composes nothing, since there is no outer display to show it on.
 */
@Composable
public fun ProvideCoverDisplayState(
    state: CoverDisplayState,
    content: @Composable () -> Unit,
): Unit = CompositionLocalProvider(LocalCoverDisplayStateOverride provides state, content = content)

/**
 * Shows [content] on the outer display while [enabled] is true and the system allows it.
 *
 * The content leaves the composition of this call site: it is composed on the outer display, in a window of
 * its own, and only while [rememberCoverDisplayState] is [CoverDisplayState.Available] or
 * [CoverDisplayState.Active]. Where that is never the case, nothing is composed. Platforms differ in what it
 * takes to get there, see the guide in the docs.
 */
@Composable
public fun CoverDisplay(
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (LocalCoverDisplayStateOverride.current != null) return
    PlatformCoverDisplay(enabled, content)
}
