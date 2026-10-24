package com.jantursky.crease.cover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.jantursky.crease.cover.platform.rememberPlatformCameraDirections

/** Where a camera sits on the device. */
public enum class CameraPosition { Unspecified, Back, Front }

/** One camera, as much as is needed to find it again in the platform's camera API. */
@Immutable
public class CameraDescriptor(
    /** The platform's unique id of the device. On iOS it matches `AVCaptureDevice.uniqueID`. */
    public val id: String,
    public val name: String,
    /** The platform's device type, on iOS the `AVCaptureDeviceType` string. */
    public val type: String,
    public val position: CameraPosition,
) {
    override fun equals(other: Any?): Boolean =
        other is CameraDescriptor && id == other.id && name == other.name && type == other.type &&
            position == other.position

    override fun hashCode(): Int = listOf(id, name, type, position).hashCode()

    override fun toString(): String = "CameraDescriptor(id=$id, name=$name, type=$type, position=$position)"
}

/**
 * Which cameras face the viewer and which face away, from the point of view of the screen the UI is on.
 * On a foldable the answer changes with the display: the camera that faces away from the inner display
 * can face the viewer on the outer one.
 */
@Immutable
public class CameraDirections(
    /** Cameras that capture the scene in front of the display. */
    public val forwardFacing: List<CameraDescriptor>,
    /** Cameras that capture the scene behind the display. */
    public val backwardFacing: List<CameraDescriptor>,
) {
    override fun equals(other: Any?): Boolean =
        other is CameraDirections && forwardFacing == other.forwardFacing && backwardFacing == other.backwardFacing

    override fun hashCode(): Int = 31 * forwardFacing.hashCode() + backwardFacing.hashCode()

    override fun toString(): String = "CameraDirections(forwardFacing=$forwardFacing, backwardFacing=$backwardFacing)"
}

/** The directions set by [ProvideCameraDirections], or `null` to use the platform's. */
internal val LocalCameraDirectionsOverride = compositionLocalOf<CameraDirections?> { null }

/**
 * The directions of the cameras relative to the display this composable is on, or `null` where the
 * platform cannot tell. Only iOS 27.1 can: other platforms and older systems return `null`. On iOS the list
 * is empty until the system has reported once.
 */
@Composable
public fun rememberCameraDirections(): CameraDirections? =
    LocalCameraDirectionsOverride.current ?: rememberPlatformCameraDirections()

/** Makes [rememberCameraDirections] return [directions] inside [content]. Use it for previews and tests. */
@Composable
public fun ProvideCameraDirections(
    directions: CameraDirections?,
    content: @Composable () -> Unit,
): Unit = CompositionLocalProvider(LocalCameraDirectionsOverride provides directions, content = content)

/** The platform's raw camera position (iOS `AVCaptureDevicePosition`): back 1, front 2, anything else unspecified. */
internal fun cameraPosition(raw: Long): CameraPosition =
    when (raw) {
        1L -> CameraPosition.Back
        2L -> CameraPosition.Front
        else -> CameraPosition.Unspecified
    }
