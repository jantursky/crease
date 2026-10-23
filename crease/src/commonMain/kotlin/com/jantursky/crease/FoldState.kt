package com.jantursky.crease

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

/** How the device is being held, derived from the hinge and the fold. */
public enum class Posture { Closed, Flat, Book, Tabletop }

/**
 * Everything the platform reports about the hinge and the display.
 *
 * @property hinge the hinge, or `null` when the device has none or has not reported it yet.
 * @property folds folds across the window, usually zero or one.
 * @property cutouts camera and sensor housings, in window coordinates and pixels.
 */
@Immutable
public class FoldState(
    public val hinge: Hinge?,
    public val folds: List<Fold>,
    public val cutouts: List<Rect>,
) {
    /** The first fold, or `null` when there is none. */
    public val fold: Fold? get() = folds.firstOrNull()

    /** The current posture. */
    public val posture: Posture get() = postureOf(hinge, fold)

    override fun equals(other: Any?): Boolean =
        other is FoldState && hinge == other.hinge && folds == other.folds && cutouts == other.cutouts

    override fun hashCode(): Int {
        var result = hinge.hashCode()
        result = 31 * result + folds.hashCode()
        result = 31 * result + cutouts.hashCode()
        return result
    }

    override fun toString(): String = "FoldState(hinge=$hinge, folds=$folds, cutouts=$cutouts)"

    public companion object {
        /** No hinge, no folds, no cutouts: a phone, a desktop window or a browser. */
        public val None: FoldState = FoldState(hinge = null, folds = emptyList(), cutouts = emptyList())

        /** A foldable that is shut, running on its outer display. */
        public fun closed(): FoldState =
            FoldState(hinge = Hinge(HingeStatus.Closed, 0f), folds = emptyList(), cutouts = emptyList())

        /** A foldable lying flat with a fold across a window of [windowSize]. */
        public fun flat(
            windowSize: Size,
            orientation: FoldOrientation = FoldOrientation.Vertical,
            foldWidth: Float = 0f,
        ): FoldState =
            FoldState(
                hinge = Hinge(HingeStatus.FullyOpen, 180f),
                folds = listOf(centeredFold(windowSize, orientation, foldWidth, isSeparating = false)),
                cutouts = emptyList(),
            )

        /** A foldable held like a book: half open, vertical fold in the middle of the window. */
        public fun book(
            windowSize: Size,
            angle: Float = 90f,
            foldWidth: Float = 0f,
        ): FoldState = halfOpen(windowSize, FoldOrientation.Vertical, angle, foldWidth)

        /** A foldable standing like a laptop: half open, horizontal fold in the middle of the window. */
        public fun tabletop(
            windowSize: Size,
            angle: Float = 90f,
            foldWidth: Float = 0f,
        ): FoldState = halfOpen(windowSize, FoldOrientation.Horizontal, angle, foldWidth)

        private fun halfOpen(
            windowSize: Size,
            orientation: FoldOrientation,
            angle: Float,
            foldWidth: Float,
        ) = FoldState(
            hinge = Hinge(HingeStatus.PartiallyOpen, angle),
            folds = listOf(centeredFold(windowSize, orientation, foldWidth, isSeparating = true)),
            cutouts = emptyList(),
        )

        private fun centeredFold(
            windowSize: Size,
            orientation: FoldOrientation,
            foldWidth: Float,
            isSeparating: Boolean,
        ): Fold {
            val half = foldWidth / 2f
            val bounds =
                when (orientation) {
                    FoldOrientation.Vertical -> {
                        val center = windowSize.width / 2f
                        Rect(center - half, 0f, center + half, windowSize.height)
                    }

                    FoldOrientation.Horizontal -> {
                        val center = windowSize.height / 2f
                        Rect(0f, center - half, windowSize.width, center + half)
                    }
                }
            return Fold(bounds, orientation, isSeparating = isSeparating, isOccluding = foldWidth > 0f)
        }
    }
}

/** Posture as a pure function of the hinge and the first fold. */
internal fun postureOf(
    hinge: Hinge?,
    fold: Fold?,
): Posture {
    if (hinge?.status == HingeStatus.Closed) return Posture.Closed
    val halfOpen = hinge == null || hinge.status == HingeStatus.PartiallyOpen
    if (fold == null || !fold.isSeparating || !halfOpen) return Posture.Flat
    return when (fold.orientation) {
        FoldOrientation.Vertical -> Posture.Book
        FoldOrientation.Horizontal -> Posture.Tabletop
    }
}
