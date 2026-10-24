package com.jantursky.crease.cover

import androidx.compose.runtime.Immutable

/**
 * Whether the outer display of a foldable can show your content right now.
 *
 * The system decides, so treat the cover display as an enhancement: the main UI has to work in every state.
 */
@Immutable
public sealed interface CoverDisplayState {
    /** This device or system has no way to show app content on an outer display. Nothing ever changes it. */
    public data object Unsupported : CoverDisplayState

    /** Supported, but not possible right now, for example because the device is closed or the app is not in front. */
    public data object Unavailable : CoverDisplayState

    /** The system would present the content if [CoverDisplay] asked for it. */
    public data object Available : CoverDisplayState

    /** The content is on the outer display. */
    public data object Active : CoverDisplayState
}

/** `true` unless the state is [CoverDisplayState.Unsupported]. */
public val CoverDisplayState.isSupported: Boolean get() = this != CoverDisplayState.Unsupported
