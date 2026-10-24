package com.jantursky.crease.cover.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.jantursky.crease.cover.CoverDisplayState
import com.jantursky.crease.cover.coverDisplayState
import com.jantursky.crease.cover.internal.cinterop.CRSCoverObserverView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

/**
 * The cover display of one host view controller, shared by every reader in it. It registers a camera
 * capture scene accessory through a [CRSCoverObserverView] and, once the system presents the accessory
 * scene, hosts [content] there in a Compose view controller of its own. Without the iOS 27.1 API the view
 * stays inert and the state stays [CoverDisplayState.Unsupported]. Everything runs on the main thread.
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosCoverSource private constructor(
    private val host: UIViewController,
) {
    val state: State<CoverDisplayState>
        field = mutableStateOf<CoverDisplayState>(CoverDisplayState.Unsupported)

    // Snapshot state, so the accessory scene's own composition follows it.
    private var content by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var users = 0
    val view: CRSCoverObserverView =
        CRSCoverObserverView(
            host = host,
            contentFactory = { ComposeUIViewController { content?.invoke() } },
            changeHandler = { report() },
        )

    init {
        view.setFrame(host.view.bounds)
        // At the back, so it never sits over the content.
        host.view.insertSubview(view, atIndex = 0)
        report()
    }

    private fun report() {
        state.value = coverDisplayState(view.supported, view.available, view.presenting)
    }

    /** Shows [content] once the system allows it, or takes it away again with `null`. */
    fun present(content: (@Composable () -> Unit)?) {
        this.content = content
        view.enabled = content != null
    }

    /** Gives up one reader; the last one unregisters the accessory. */
    fun release() {
        if (--users > 0) return
        view.detach()
        sources.remove(host)
    }

    companion object {
        private val sources = mutableMapOf<UIViewController, IosCoverSource>()

        /** The source for [host], with one more reader. Pair with [release]. */
        fun acquire(host: UIViewController): IosCoverSource =
            sources.getOrPut(host) { IosCoverSource(host) }.also { it.users++ }
    }
}
