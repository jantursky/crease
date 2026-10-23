package com.jantursky.crease

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect

/** Direction of the fold line. A vertical fold splits the window into a left and a right half. */
public enum class FoldOrientation { Vertical, Horizontal }

/**
 * A fold across the window.
 *
 * @property bounds the fold in window coordinates, in pixels. A seamless panel reports zero width.
 * @property isSeparating whether content should stay on one side of the fold.
 * @property isOccluding whether the fold hides what is drawn beneath it.
 */
@Immutable
public class Fold(
    public val bounds: Rect,
    public val orientation: FoldOrientation,
    public val isSeparating: Boolean,
    public val isOccluding: Boolean,
) {
    override fun equals(other: Any?): Boolean =
        other is Fold &&
            bounds == other.bounds &&
            orientation == other.orientation &&
            isSeparating == other.isSeparating &&
            isOccluding == other.isOccluding

    override fun hashCode(): Int {
        var result = bounds.hashCode()
        result = 31 * result + orientation.hashCode()
        result = 31 * result + isSeparating.hashCode()
        result = 31 * result + isOccluding.hashCode()
        return result
    }

    override fun toString(): String =
        "Fold(bounds=$bounds, orientation=$orientation, isSeparating=$isSeparating, isOccluding=$isOccluding)"
}
