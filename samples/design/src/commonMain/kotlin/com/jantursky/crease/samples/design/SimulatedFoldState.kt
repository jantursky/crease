package com.jantursky.crease.samples.design

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.jantursky.crease.Fold
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture

/** Logical screen size of the simulated device in each posture. */
internal fun screenSize(posture: Posture): DpSize =
    when (posture) {
        Posture.Closed -> DpSize(340.dp, 640.dp)
        Posture.Flat, Posture.Book -> DpSize(840.dp, 620.dp)
        Posture.Tabletop -> DpSize(620.dp, 840.dp)
    }

/** The camera housing: a [diameter] square, near the top edge of the half it sits in. */
internal fun cutoutRect(
    posture: Posture,
    windowSize: Size,
    diameter: Float,
): Rect {
    val centerX = if (posture == Posture.Book) windowSize.width / 4 else windowSize.width / 2
    return Rect(centerX - diameter / 2, diameter, centerX + diameter / 2, diameter + diameter)
}

/** The state a real device would report, in the coordinates of the simulated screen. */
internal fun simulatedFoldState(
    posture: Posture,
    angle: Float,
    windowSize: Size,
    foldWidth: Float,
    cutout: Rect?,
): FoldState {
    val base =
        when (posture) {
            Posture.Closed -> FoldState.closed()
            Posture.Flat -> FoldState.flat(windowSize, FoldOrientation.Vertical, foldWidth)
            Posture.Book -> FoldState.book(windowSize, angle, foldWidth)
            Posture.Tabletop -> FoldState.tabletop(windowSize, angle, foldWidth)
        }
    return FoldState(base.hinge, base.folds, listOfNotNull(cutout))
}

/** Moves every fold and cutout, to turn screen coordinates into window coordinates. */
internal fun FoldState.translatedBy(offset: Offset): FoldState =
    FoldState(
        hinge = hinge,
        folds = folds.map { Fold(it.bounds.translate(offset), it.orientation, it.isSeparating, it.isOccluding) },
        cutouts = cutouts.map { it.translate(offset) },
    )
