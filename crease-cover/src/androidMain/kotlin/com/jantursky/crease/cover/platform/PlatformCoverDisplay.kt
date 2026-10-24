package com.jantursky.crease.cover.platform

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.window.area.WindowAreaCapability.Status
import com.jantursky.crease.cover.CoverDisplayState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal actual fun rememberPlatformCoverDisplayState(): CoverDisplayState {
    val activity = LocalContext.current.findActivity()
    var state by remember(activity) { mutableStateOf<CoverDisplayState>(CoverDisplayState.Unsupported) }
    if (activity != null) {
        LaunchedEffect(activity) {
            AndroidCoverSource.rearArea().collect { state = androidCoverState(AndroidCoverSource.status(it)) }
        }
    }
    return state
}

@Composable
internal actual fun PlatformCoverDisplay(
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current.findActivity() ?: return
    val parent = rememberCompositionContext()
    val latest by rememberUpdatedState(content)
    var presented by remember(activity) { mutableStateOf<PresentedContent?>(null) }
    DisposableEffect(activity) { onDispose { presented?.close() } }
    LaunchedEffect(activity, enabled) {
        if (!enabled) {
            presented?.close()
            presented = null
            return@LaunchedEffect
        }
        // The area comes and goes with the device: closing the device ends the session, opening it
        // makes the area available again, and the session is started once more.
        AndroidCoverSource.rearArea().collectLatest { area ->
            if (area == null || presented != null ||
                AndroidCoverSource.status(area) != Status.WINDOW_AREA_STATUS_AVAILABLE
            ) {
                return@collectLatest
            }
            AndroidCoverSource.present(
                activity = activity as Activity,
                token = area.token,
                onStarted = { session -> presented = PresentedContent(session, parent) { latest() } },
                onEnded = {
                    presented?.close()
                    presented = null
                },
            )
        }
    }
}
