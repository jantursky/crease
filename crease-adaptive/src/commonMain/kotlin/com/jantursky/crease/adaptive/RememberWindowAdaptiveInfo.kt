package com.jantursky.crease.adaptive

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.jantursky.crease.rememberFoldState

/**
 * Window adaptive info whose posture comes from Crease.
 *
 * The size class is the one `currentWindowAdaptiveInfoV2()` computes for the current window, so the breakpoints
 * match what adaptive scaffolds use. The posture is [com.jantursky.crease.rememberFoldState] converted with
 * [toWindowPosture], so it works on the iPhone Duo and follows `ProvideFoldState` in previews and tests. Inside
 * this call Crease's posture replaces the adaptive library's own.
 */
@Composable
public fun rememberWindowAdaptiveInfo(): WindowAdaptiveInfo {
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val foldState = rememberFoldState()
    return remember(sizeClass, foldState) { WindowAdaptiveInfo(sizeClass, foldState.toWindowPosture()) }
}
