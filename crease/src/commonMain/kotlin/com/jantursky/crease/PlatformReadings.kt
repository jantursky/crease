package com.jantursky.crease

import androidx.compose.ui.geometry.Rect
import kotlin.math.PI

// Pure mapping from raw platform readings to the public model. The platform glue only collects the
// readings; every decision about them lives here, where it runs in host tests.

/** Below this angle a hinge reads as closed. */
internal const val CLOSED_ANGLE_MAX = 10f

/** From this angle on a hinge reads as fully open. */
internal const val FULLY_OPEN_ANGLE_MIN = 170f

/** Hinge status for a bare angle in degrees, used when nothing else reports one. */
internal fun hingeStatusFromAngle(angle: Float): HingeStatus =
    when {
        angle <= CLOSED_ANGLE_MAX -> HingeStatus.Closed
        angle >= FULLY_OPEN_ANGLE_MIN -> HingeStatus.FullyOpen
        else -> HingeStatus.PartiallyOpen
    }

/**
 * The hinge on Android. [featureStatus] comes from a folding feature in the window and wins when
 * present; without one the [angle] from the hinge sensor decides. Neither means no hinge.
 */
internal fun androidHinge(
    featureStatus: HingeStatus?,
    angle: Float?,
): Hinge? =
    when {
        featureStatus != null -> Hinge(featureStatus, angle)
        angle != null -> Hinge(hingeStatusFromAngle(angle), angle)
        else -> null
    }

/** An iOS hinge angle, in radians with 0 shut and pi flat, as degrees clamped to 0..180. */
internal fun radiansToDegrees(radians: Double): Float = (radians * 180.0 / PI).coerceIn(0.0, 180.0).toFloat()

/** Maps a raw `UIHingeStatus`. Unknown (0) and anything unexpected map to `null`. */
internal fun iosHingeStatus(raw: Long): HingeStatus? =
    when (raw) {
        1L -> HingeStatus.Closed
        2L -> HingeStatus.PartiallyOpen
        3L -> HingeStatus.FullyOpen
        else -> null
    }

/** A `UIViewReservedRegion` as read through the runtime, in points. */
internal class RawRegion(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val isActive: Boolean,
) {
    fun toPixels(scale: Float): Rect =
        Rect(
            left = (x * scale).toFloat(),
            top = (y * scale).toFloat(),
            right = ((x + width) * scale).toFloat(),
            bottom = ((y + height) * scale).toFloat(),
        )
}

/** One reading of the iOS observer: the hinge, if one has reported, and the reserved regions. */
internal class IosReading(
    val hasHinge: Boolean,
    val rawStatus: Long,
    val angleRadians: Double,
    val divisions: List<RawRegion>,
    val occlusions: List<RawRegion>,
)

/**
 * Builds the [FoldState] for an iOS reading, with points converted to pixels by [scale].
 *
 * The fold division lags the hinge by up to a second and keeps its frame while inactive, so it is
 * published only while the hinge reads partially open. It occludes once it is active and has an area.
 */
internal fun iosFoldState(
    reading: IosReading,
    scale: Float,
): FoldState {
    val hinge =
        if (reading.hasHinge) {
            val angle = radiansToDegrees(reading.angleRadians)
            Hinge(iosHingeStatus(reading.rawStatus) ?: hingeStatusFromAngle(angle), angle)
        } else {
            null
        }
    val folds =
        if (hinge?.status == HingeStatus.PartiallyOpen) {
            reading.divisions.mapNotNull { it.toFold(scale) }
        } else {
            emptyList()
        }
    return FoldState(hinge, folds, reading.occlusions.map { it.toPixels(scale) })
}

private fun RawRegion.toFold(scale: Float): Fold? {
    if (width <= 0.0 && height <= 0.0) return null
    return Fold(
        bounds = toPixels(scale),
        orientation = if (height >= width) FoldOrientation.Vertical else FoldOrientation.Horizontal,
        isSeparating = true,
        isOccluding = isActive && width > 0.0 && height > 0.0,
    )
}
