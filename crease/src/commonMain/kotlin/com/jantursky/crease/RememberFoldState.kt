package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.jantursky.crease.platform.rememberPlatformFoldState

/** The state set by [ProvideFoldState], or `null` to use the platform's. */
internal val LocalFoldStateOverride = compositionLocalOf<FoldState?> { null }

/**
 * The current [FoldState], updated as the device folds and unfolds.
 *
 * Returns the state passed to the nearest [ProvideFoldState] when there is one, otherwise the state the
 * platform reports. JVM and wasm targets report [FoldState.None].
 */
@Composable
public fun rememberFoldState(): FoldState = LocalFoldStateOverride.current ?: rememberPlatformFoldState()

/** The current [Hinge], or `null` when the device has none. Shorthand for `rememberFoldState().hinge`. */
@Composable
public fun rememberHinge(): Hinge? = rememberFoldState().hinge

/**
 * Makes [rememberFoldState] return [state] inside [content]. Use it for previews, tests and simulators.
 */
@Composable
public fun ProvideFoldState(
    state: FoldState,
    content: @Composable () -> Unit,
): Unit = CompositionLocalProvider(LocalFoldStateOverride provides state, content = content)
