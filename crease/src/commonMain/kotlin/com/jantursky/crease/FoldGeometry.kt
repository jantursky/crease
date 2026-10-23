package com.jantursky.crease

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Constraints

/** The two parts of a layout on either side of a fold, in the layout's local coordinates. */
internal class FoldSplit(
    val first: Rect,
    val second: Rect,
) {
    /** The larger part; the first one when both are the same size. */
    val larger: Rect get() = if (second.area > first.area) second else first

    private val Rect.area: Float get() = width * height
}

/**
 * Splits [container] at [fold], both in the same coordinates. An occluding fold is left out of both
 * parts; any other fold splits at its center line. Returns `null` when the fold does not cross the
 * container, or leaves nothing on one side of it.
 */
internal fun splitAtFold(
    container: Rect,
    fold: Fold,
): FoldSplit? {
    val bounds = fold.bounds
    return when (fold.orientation) {
        FoldOrientation.Vertical -> {
            if (bounds.top >= container.bottom || bounds.bottom <= container.top) return null
            val start = if (fold.isOccluding) bounds.left else bounds.center.x
            val end = if (fold.isOccluding) bounds.right else bounds.center.x
            if (start <= container.left || end >= container.right) return null
            FoldSplit(
                first = Rect(container.left, container.top, start, container.bottom),
                second = Rect(end, container.top, container.right, container.bottom),
            )
        }

        FoldOrientation.Horizontal -> {
            if (bounds.left >= container.right || bounds.right <= container.left) return null
            val start = if (fold.isOccluding) bounds.top else bounds.center.y
            val end = if (fold.isOccluding) bounds.bottom else bounds.center.y
            if (start <= container.top || end >= container.bottom) return null
            FoldSplit(
                first = Rect(container.left, container.top, container.right, start),
                second = Rect(container.left, end, container.right, container.bottom),
            )
        }
    }
}

/** The fold that layouts react to: the first one, if it separates content. */
internal val FoldState.separatingFold: Fold? get() = fold?.takeIf { it.isSeparating }

/** [fold] moved from window coordinates into those of a layout whose top-left sits at [origin]. */
internal fun Fold.toLocal(origin: Offset): Fold =
    Fold(bounds.translate(-origin), orientation, isSeparating, isOccluding)

/**
 * Splits [container], a layout's own area, at [fold], given in window coordinates. [origin] is where the
 * layout sits in the window; until it is known there is nothing to split.
 */
internal fun splitAtWindowFold(
    container: Rect,
    fold: Fold?,
    origin: Offset?,
): FoldSplit? = if (fold == null || origin == null) null else splitAtFold(container, fold.toLocal(origin))

/**
 * The area a layout fills under [constraints]: up to the maximum where it is bounded, and open-ended
 * (infinite) where it is not, for the content to decide.
 */
internal fun Constraints.fillArea(): Rect =
    Rect(
        left = 0f,
        top = 0f,
        right = if (hasBoundedWidth) maxWidth.toFloat() else Float.POSITIVE_INFINITY,
        bottom = if (hasBoundedHeight) maxHeight.toFloat() else Float.POSITIVE_INFINITY,
    )
