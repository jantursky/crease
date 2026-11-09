package com.jantursky.crease.adaptive

import androidx.compose.material3.adaptive.HingeInfo
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.HingeStatus
import androidx.compose.material3.adaptive.Posture as WindowPosture
import com.jantursky.crease.Posture as CreasePosture

/**
 * Converts this fold state to the Material3 adaptive library's posture.
 *
 * The result is tabletop when [FoldState.posture] is [CreasePosture.Tabletop]. Every fold becomes one hinge
 * with the same bounds, orientation, separating and occluding flags. A hinge counts as flat when the device
 * reports it fully open.
 */
public fun FoldState.toWindowPosture(): WindowPosture {
    val flat = hinge?.status == HingeStatus.FullyOpen
    return WindowPosture(
        isTabletop = posture == CreasePosture.Tabletop,
        hingeList =
            folds.map { fold ->
                HingeInfo(
                    bounds = fold.bounds,
                    isFlat = flat,
                    isVertical = fold.orientation == FoldOrientation.Vertical,
                    isSeparating = fold.isSeparating,
                    isOccluding = fold.isOccluding,
                )
            },
    )
}
