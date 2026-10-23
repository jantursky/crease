package com.jantursky.crease

import androidx.compose.runtime.Immutable

/** How far the hinge is open, as the platform reports it. */
public enum class HingeStatus { Closed, PartiallyOpen, FullyOpen }

/**
 * The physical hinge of a foldable device.
 *
 * [angle] is in degrees: 0 when the device is shut, 180 when it lies flat. It is `null` when the
 * device reports a status but has no angle sensor.
 */
@Immutable
public class Hinge(
    public val status: HingeStatus,
    public val angle: Float?,
) {
    override fun equals(other: Any?): Boolean = other is Hinge && status == other.status && angle == other.angle

    override fun hashCode(): Int = 31 * status.hashCode() + angle.hashCode()

    override fun toString(): String = "Hinge(status=$status, angle=$angle)"
}
