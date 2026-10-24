package com.jantursky.crease.cover.platform

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.jantursky.crease.cover.CameraDescriptor
import com.jantursky.crease.cover.CameraDirections
import com.jantursky.crease.cover.cameraPosition
import com.jantursky.crease.cover.internal.cinterop.CRSCameraDescriptor
import com.jantursky.crease.cover.internal.cinterop.CRSCameraDirectionObserver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

/**
 * The camera directions relative to one host view, shared by every reader in it. [state] is `null` where
 * the iOS 27.1 coordinator is missing. Everything runs on the main thread.
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosCameraDirectionSource private constructor(
    private val host: UIView,
) {
    val state: State<CameraDirections?>
        field = mutableStateOf<CameraDirections?>(null)

    private var users = 0
    val observer: CRSCameraDirectionObserver = CRSCameraDirectionObserver(host, changeHandler = { report() })

    init {
        report()
    }

    private fun report() {
        state.value = if (observer.supported) observer.directions() else null
    }

    /** Gives up one reader; the last one stops the observer. */
    fun release() {
        if (--users > 0) return
        observer.detach()
        sources.remove(host)
    }

    companion object {
        private val sources = mutableMapOf<UIView, IosCameraDirectionSource>()

        /** The source for [host], with one more reader. Pair with [release]. */
        fun acquire(host: UIView): IosCameraDirectionSource =
            sources.getOrPut(host) { IosCameraDirectionSource(host) }.also { it.users++ }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun CRSCameraDirectionObserver.directions(): CameraDirections =
    CameraDirections(forwardFacing.toDescriptors(), backwardFacing.toDescriptors())

@OptIn(ExperimentalForeignApi::class)
private fun List<*>.toDescriptors(): List<CameraDescriptor> =
    filterIsInstance<CRSCameraDescriptor>().map {
        CameraDescriptor(it.uniqueID, it.localizedName, it.deviceType, cameraPosition(it.position))
    }
