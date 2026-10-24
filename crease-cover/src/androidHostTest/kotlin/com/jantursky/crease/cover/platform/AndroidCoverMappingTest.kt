package com.jantursky.crease.cover.platform

import androidx.window.area.WindowAreaCapability.Status
import com.jantursky.crease.cover.CoverDisplayState
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidCoverMappingTest {
    @Test
    fun noRearAreaIsUnsupported() {
        assertEquals(CoverDisplayState.Unsupported, androidCoverState(null))
    }

    @Test
    fun theWindowManagerStatusesMapOneToOne() {
        assertEquals(CoverDisplayState.Unsupported, androidCoverState(Status.WINDOW_AREA_STATUS_UNSUPPORTED))
        assertEquals(CoverDisplayState.Unavailable, androidCoverState(Status.WINDOW_AREA_STATUS_UNAVAILABLE))
        assertEquals(CoverDisplayState.Available, androidCoverState(Status.WINDOW_AREA_STATUS_AVAILABLE))
        assertEquals(CoverDisplayState.Active, androidCoverState(Status.WINDOW_AREA_STATUS_ACTIVE))
    }
}
