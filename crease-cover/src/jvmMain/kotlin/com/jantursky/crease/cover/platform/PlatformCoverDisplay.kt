package com.jantursky.crease.cover.platform

import androidx.compose.runtime.Composable
import com.jantursky.crease.cover.CameraDirections
import com.jantursky.crease.cover.CoverDisplayState

// No cover display here yet; the platform sources are wired up one platform at a time.

@Composable
internal actual fun rememberPlatformCoverDisplayState(): CoverDisplayState = CoverDisplayState.Unsupported

@Composable
internal actual fun PlatformCoverDisplay(
    enabled: Boolean,
    content: @Composable () -> Unit,
) = Unit

@Composable
internal actual fun rememberPlatformCameraDirections(): CameraDirections? = null
