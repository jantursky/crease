package com.jantursky.crease.cover.platform

import androidx.compose.runtime.Composable
import com.jantursky.crease.cover.CoverDisplayState

/** The platform's cover display state, as a state that recomposes when it changes. */
@Composable
internal expect fun rememberPlatformCoverDisplayState(): CoverDisplayState

/** Asks the platform to show [content] on the outer display while [enabled]. */
@Composable
internal expect fun PlatformCoverDisplay(
    enabled: Boolean,
    content: @Composable () -> Unit,
)
