package com.jantursky.crease.test

import androidx.compose.ui.geometry.Size
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture

/**
 * A window size and fold width, in pixels, that builds a [FoldState] for any posture. The values are plain
 * test fixtures, not measurements of a particular device.
 */
public class FoldPreset(
    public val windowSize: Size,
    public val foldWidth: Float,
) {
    /** The state in [posture]. [angle] applies to Book and Tabletop. */
    public fun state(
        posture: Posture,
        angle: Float = 90f,
        orientation: FoldOrientation = FoldOrientation.Vertical,
    ): FoldState = stateFor(posture, windowSize, angle, foldWidth, orientation)

    public fun closed(): FoldState = state(Posture.Closed)

    public fun flat(orientation: FoldOrientation = FoldOrientation.Vertical): FoldState =
        state(Posture.Flat, orientation = orientation)

    public fun book(angle: Float = 90f): FoldState = state(Posture.Book, angle)

    public fun tabletop(angle: Float = 90f): FoldState = state(Posture.Tabletop, angle)
}

/** Ready-made [FoldPreset]s. */
public object FoldStatePresets {
    /** A foldable whose panel is one piece across the fold: the fold takes no space. */
    public val Seamless: FoldPreset = FoldPreset(Size(2520f, 1920f), foldWidth = 0f)

    /** A foldable with a 40 px division between two screens: the fold occludes content. */
    public val Divided: FoldPreset = FoldPreset(Size(2520f, 1920f), foldWidth = 40f)
}
