package com.jantursky.crease.adaptive

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.window.core.layout.WindowSizeClass
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture
import com.jantursky.crease.ProvideFoldState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AdaptiveBridgeTest {
    private val window = Size(1000f, 800f)

    @Test
    fun providedBookStateWinsAndTheSizeClassFollowsTheWindow() =
        runComposeUiTest {
            var info: WindowAdaptiveInfo? = null
            var plain: WindowAdaptiveInfo? = null
            setContent {
                plain = currentWindowAdaptiveInfoV2()
                ProvideFoldState(FoldState.book(window)) { info = rememberWindowAdaptiveInfo() }
            }
            val posture = info!!.windowPosture
            assertFalse(posture.isTabletop)
            assertTrue(posture.hingeList.single().isVertical)
            assertEquals(plain!!.windowSizeClass, info!!.windowSizeClass)
        }

    @Test
    fun platformWithoutFoldsHasNoHinges() =
        runComposeUiTest {
            var info: WindowAdaptiveInfo? = null
            setContent { info = rememberWindowAdaptiveInfo() }
            assertTrue(info!!.windowPosture.hingeList.isEmpty())
            assertFalse(info!!.windowPosture.isTabletop)
        }

    @Test
    fun widthAndPostureChangeIndependently() =
        runComposeUiTest {
            var density by mutableStateOf(Density(2f))
            var fold by mutableStateOf(FoldState.None)
            var layout: FoldLayoutClass? = null
            setContent {
                CompositionLocalProvider(LocalDensity provides density) {
                    ProvideFoldState(fold) { layout = rememberFoldLayoutClass() }
                }
            }
            assertFalse(layout!!.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND))
            assertEquals(Posture.Flat, layout!!.posture)

            fold = FoldState.tabletop(window)
            waitForIdle()
            assertFalse(layout!!.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND))
            assertEquals(Posture.Tabletop, layout!!.posture)

            density = Density(0.5f)
            waitForIdle()
            assertTrue(layout!!.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))
            assertEquals(Posture.Tabletop, layout!!.posture)

            density = Density(4f)
            fold = FoldState.None
            waitForIdle()
            assertFalse(layout!!.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND))
            assertEquals(Posture.Flat, layout!!.posture)
        }
}
