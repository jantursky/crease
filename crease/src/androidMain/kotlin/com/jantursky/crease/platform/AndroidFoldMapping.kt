package com.jantursky.crease.platform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.jantursky.crease.Fold
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.HingeStatus
import com.jantursky.crease.androidHinge

/**
 * Builds the [FoldState] from the activity's display features, the hinge sensor and the cutouts.
 *
 * WindowManager reports features in the activity window's coordinates. A composition in another window
 * of that activity, such as a dialog or a popup, measures from its own window, so the features are moved
 * by [activityWindowOffset], where the activity window sits relative to the composition's window.
 */
internal fun androidFoldState(
    features: List<DisplayFeature>,
    angle: Float?,
    cutouts: List<Rect>,
    activityWindowOffset: Offset = Offset.Zero,
): FoldState {
    val foldingFeatures = features.filterIsInstance<FoldingFeature>()
    val status = foldingFeatures.firstOrNull()?.hingeStatus
    val folds = foldingFeatures.map { it.toFold(activityWindowOffset) }
    return FoldState(androidHinge(status, angle), folds, cutouts)
}

internal val FoldingFeature.hingeStatus: HingeStatus
    get() = if (state == FoldingFeature.State.HALF_OPENED) HingeStatus.PartiallyOpen else HingeStatus.FullyOpen

internal fun FoldingFeature.toFold(offset: Offset = Offset.Zero): Fold =
    Fold(
        bounds = bounds.toComposeRect().translate(offset),
        orientation =
            if (orientation == FoldingFeature.Orientation.VERTICAL) {
                FoldOrientation.Vertical
            } else {
                FoldOrientation.Horizontal
            },
        isSeparating = isSeparating,
        isOccluding = occlusionType == FoldingFeature.OcclusionType.FULL,
    )
