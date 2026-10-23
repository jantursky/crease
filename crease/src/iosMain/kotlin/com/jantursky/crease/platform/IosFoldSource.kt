package com.jantursky.crease.platform

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.jantursky.crease.FoldState
import com.jantursky.crease.IosReading
import com.jantursky.crease.RawRegion
import com.jantursky.crease.internal.cinterop.CRSHingeObserverView
import com.jantursky.crease.internal.cinterop.CRSRegion
import com.jantursky.crease.iosFoldState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIScreen
import platform.UIKit.UIView

/**
 * The fold state of one host view, shared by every reader in it. It puts a [CRSHingeObserverView]
 * behind the host's content and maps each hinge update and region change. Without the iOS 27.1 API
 * the view stays inert and the state stays [FoldState.None]. Everything runs on the main thread.
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosFoldSource private constructor(
    private val host: UIView,
) {
    private val current = mutableStateOf(FoldState.None)
    val state: State<FoldState> get() = current

    private var users = 0
    val view: CRSHingeObserverView = CRSHingeObserverView(changeHandler = { report() })

    init {
        view.setFrame(host.bounds)
        // At the back, so it never sits over the content.
        host.insertSubview(view, atIndex = 0)
    }

    /** Compose measures in pixels at the screen scale. */
    private fun report() {
        current.value = iosFoldState(view.reading(), UIScreen.mainScreen.scale.toFloat())
    }

    /** Gives up one reader; the last one removes the observer view. */
    fun release() {
        if (--users > 0) return
        view.detach()
        sources.remove(host)
    }

    companion object {
        private val sources = mutableMapOf<UIView, IosFoldSource>()

        /** The source for [host], with one more reader. Pair with [release]. */
        fun acquire(host: UIView): IosFoldSource = sources.getOrPut(host) { IosFoldSource(host) }.also { it.users++ }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun CRSHingeObserverView.reading(): IosReading =
    IosReading(
        hasHinge = hasHinge,
        rawStatus = hingeStatus,
        angleRadians = hingeAngle,
        divisions = divisions.toRawRegions(),
        occlusions = occlusions.toRawRegions(),
    )

@OptIn(ExperimentalForeignApi::class)
private fun List<*>.toRawRegions(): List<RawRegion> =
    filterIsInstance<CRSRegion>().map { RawRegion(it.x, it.y, it.width, it.height, it.active) }
