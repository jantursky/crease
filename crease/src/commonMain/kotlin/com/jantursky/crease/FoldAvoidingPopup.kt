package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.math.roundToInt

/**
 * Moves a popup at [position], in window coordinates, off a separating [fold] when it would cross it.
 *
 * The popup goes to the side of the fold that [anchor] is on when it fits there. Otherwise it goes to
 * the other side. When it fits on neither, it starts at the beginning of the larger side and the window
 * clips the rest, so it never sits across the fold.
 */
internal fun foldAvoidingPosition(
    position: IntOffset,
    anchor: IntRect,
    popupSize: IntSize,
    windowSize: IntSize,
    fold: Fold?,
): IntOffset {
    if (fold == null || !fold.isSeparating) return position
    val window = Rect(0f, 0f, windowSize.width.toFloat(), windowSize.height.toFloat())
    val split = splitAtFold(window, fold) ?: return position
    val vertical = fold.orientation == FoldOrientation.Vertical
    val first = split.first
    val second = split.second

    val start = (if (vertical) position.x else position.y).toFloat()
    val length = (if (vertical) popupSize.width else popupSize.height).toFloat()
    if (start + length <= first.hi(vertical) || start >= second.lo(vertical)) return position

    val anchorCenter = (if (vertical) anchor.center.x else anchor.center.y).toFloat()
    val foldCenter = (first.hi(vertical) + second.lo(vertical)) / 2f
    val sides = if (anchorCenter < foldCenter) listOf(first, second) else listOf(second, first)
    val fitting = sides.firstOrNull { it.size(vertical) >= length }
    val newStart =
        if (fitting != null) {
            start.coerceIn(fitting.lo(vertical), fitting.hi(vertical) - length)
        } else {
            (if (first.size(vertical) >= second.size(vertical)) first else second).lo(vertical)
        }
    val rounded = newStart.roundToInt()
    return if (vertical) IntOffset(rounded, position.y) else IntOffset(position.x, rounded)
}

private fun Rect.lo(vertical: Boolean): Float = if (vertical) left else top

private fun Rect.hi(vertical: Boolean): Float = if (vertical) right else bottom

private fun Rect.size(vertical: Boolean): Float = if (vertical) width else height

/**
 * A [PopupPositionProvider] that takes the position from [delegate] and moves it off a separating fold
 * when the popup would cross it. The rules of [delegate], such as alignment and offsets, still apply.
 *
 * [fold] is read every time a position is needed, in window coordinates of the main window, so the popup
 * follows the fold as it changes. Use [rememberFoldAvoidingPopupPositionProvider] in composables.
 */
public class FoldAvoidingPopupPositionProvider(
    private val delegate: PopupPositionProvider,
    private val fold: () -> Fold?,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val position =
            delegate.calculatePosition(anchorBounds, windowSize, layoutDirection, popupContentSize)
        return foldAvoidingPosition(position, anchorBounds, popupContentSize, windowSize, fold())
    }
}

/**
 * Remembers a [FoldAvoidingPopupPositionProvider] around [delegate] that follows the current fold state,
 * including one given by [ProvideFoldState]. The popup is repositioned when the posture changes, without
 * being recreated.
 */
@Composable
public fun rememberFoldAvoidingPopupPositionProvider(delegate: PopupPositionProvider): PopupPositionProvider {
    val separatingFold = rememberFoldState().separatingFold
    val latestFold by rememberUpdatedState(separatingFold)
    return remember(delegate) { FoldAvoidingPopupPositionProvider(delegate) { latestFold } }
}
