package com.jantursky.crease.cover.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.uikit.LocalUIViewController
import com.jantursky.crease.cover.CameraDirections
import com.jantursky.crease.cover.CoverDisplayState

@Composable
internal actual fun rememberPlatformCoverDisplayState(): CoverDisplayState {
    val host = LocalUIViewController.current
    var source by remember(host) { mutableStateOf<IosCoverSource?>(null) }
    DisposableEffect(host) {
        val acquired = IosCoverSource.acquire(host)
        source = acquired
        onDispose { acquired.release() }
    }
    return source?.state?.value ?: CoverDisplayState.Unsupported
}

@Composable
internal actual fun PlatformCoverDisplay(
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    val host = LocalUIViewController.current
    val latest by rememberUpdatedState(content)
    DisposableEffect(host, enabled) {
        val acquired = IosCoverSource.acquire(host)
        if (enabled) acquired.present { latest() }
        onDispose {
            acquired.present(null)
            acquired.release()
        }
    }
}

@Composable
internal actual fun rememberPlatformCameraDirections(): CameraDirections? {
    val host = LocalUIViewController.current.view
    var source by remember(host) { mutableStateOf<IosCameraDirectionSource?>(null) }
    DisposableEffect(host) {
        val acquired = IosCameraDirectionSource.acquire(host)
        source = acquired
        onDispose { acquired.release() }
    }
    return source?.state?.value
}
