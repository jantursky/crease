package com.jantursky.crease.cover.platform

import androidx.compose.runtime.Composable
import com.jantursky.crease.cover.CameraDirections
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

/** The platform's camera directions, or `null` where it has none. */
@Composable
internal expect fun rememberPlatformCameraDirections(): CameraDirections?
