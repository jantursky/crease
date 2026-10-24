package com.jantursky.crease.cover.platform

import androidx.window.area.WindowAreaCapability.Status
import com.jantursky.crease.cover.CoverDisplayState
import com.jantursky.crease.cover.coverDisplayState

/**
 * Maps the status of the "present content on this window area" capability of the rear display to a
 * [CoverDisplayState]. `null` means the device reports no rear display area at all.
 *
 * A status the library does not know (a newer androidx.window may add one) counts as unsupported.
 */
internal fun androidCoverState(status: Status?): CoverDisplayState =
    coverDisplayState(
        supported =
            status == Status.WINDOW_AREA_STATUS_UNAVAILABLE || status == Status.WINDOW_AREA_STATUS_AVAILABLE ||
                status == Status.WINDOW_AREA_STATUS_ACTIVE,
        available = status == Status.WINDOW_AREA_STATUS_AVAILABLE,
        presenting = status == Status.WINDOW_AREA_STATUS_ACTIVE,
    )
