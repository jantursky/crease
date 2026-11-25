package com.jantursky.crease

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class FoldAvoidingPopupProviderTest {
    /** Places the popup at the anchor's top-left corner, like a plain menu anchored to its button. */
    private val atAnchor =
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset = anchorBounds.topLeft
        }

    /** Clamps its requested x to the window, like a provider that keeps popups on screen. */
    private val clampingToWindow =
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                val x = anchorBounds.left.coerceIn(0, windowSize.width - popupContentSize.width)
                return IntOffset(x, anchorBounds.top)
            }
        }

    @Composable
    private fun MenuNextToFold(provider: PopupPositionProvider) {
        // The anchor sits at x 440..480, so a 200 px popup from it would cross the fold at 500.
        Placed(x = 440, y = 300, width = 40, height = 40) {
            Popup(popupPositionProvider = provider) {
                Box(Modifier.testTag("menu").size(200.dp, 100.dp))
            }
        }
    }

    @Composable
    private fun FoldAwareMenu(delegate: PopupPositionProvider) {
        MenuNextToFold(rememberFoldAvoidingPopupPositionProvider(delegate))
    }

    @Test
    fun popupIsPlacedBesideTheFoldInABookPosture() =
        runWindowTest {
            setWindowContent { ProvideFoldState(FoldState.book(window)) { FoldAwareMenu(atAnchor) } }
            waitForIdle()
            assertEquals(Rect(300f, 300f, 500f, 400f), boundsOf("menu"))
        }

    @Test
    fun popupWithoutAFoldIsNotMoved() =
        runWindowTest {
            setWindowContent { ProvideFoldState(FoldState.None) { FoldAwareMenu(atAnchor) } }
            waitForIdle()
            assertEquals(Rect(440f, 300f, 640f, 400f), boundsOf("menu"))
        }

    @Test
    fun popupMovesWhenThePostureChangesWhileOpen() =
        runWindowTest {
            var state by mutableStateOf(FoldState.flat(window))
            setWindowContent { ProvideFoldState(state) { FoldAwareMenu(atAnchor) } }
            waitForIdle()
            assertEquals(Rect(440f, 300f, 640f, 400f), boundsOf("menu"))

            state = FoldState.book(window)
            waitForIdle()
            assertEquals(Rect(300f, 300f, 500f, 400f), boundsOf("menu"))
        }

    @Test
    fun clampingDelegateDoesNotPushThePopupBackAcrossTheFold() =
        runWindowTest {
            setWindowContent { ProvideFoldState(FoldState.book(window)) { FoldAwareMenu(clampingToWindow) } }
            waitForIdle()
            assertEquals(Rect(300f, 300f, 500f, 400f), boundsOf("menu"))
        }
}
