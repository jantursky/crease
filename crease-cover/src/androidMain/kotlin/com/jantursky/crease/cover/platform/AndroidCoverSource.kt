package com.jantursky.crease.cover.platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.window.area.WindowAreaCapability
import androidx.window.area.WindowAreaCapability.Status
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaInfo
import androidx.window.area.WindowAreaPresentationSessionCallback
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionPresenter
import androidx.window.core.ExperimentalWindowApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executor

/**
 * The rear display of a foldable through Jetpack WindowManager's window areas. It uses the presentation
 * operation (dual-screen mode): content on the rear display while the activity stays on the inner one.
 * Unlike rear display mode, which moves the whole activity and asks the user in a system dialog, nothing
 * here needs the user's consent. The API is experimental, so every use of it stays in this file.
 */
@OptIn(ExperimentalWindowApi::class)
internal object AndroidCoverSource {
    private val mainExecutor = Executor { Handler(Looper.getMainLooper()).post(it) }

    /** The rear display area, or `null` while the device reports none. Empty where the API is missing. */
    fun rearArea(): Flow<WindowAreaInfo?> =
        controller()
            ?.windowAreaInfos
            ?.map { infos -> infos.firstOrNull { it.type == WindowAreaInfo.Type.TYPE_REAR_FACING } }
            ?: emptyFlow()

    fun status(info: WindowAreaInfo?): Status? =
        info?.getCapability(WindowAreaCapability.Operation.OPERATION_PRESENT_ON_AREA)?.status

    /** Starts presenting on the area. [onStarted] gets the presenter and [onEnded] runs when the session ends. */
    fun present(
        activity: Activity,
        token: Binder,
        onStarted: (WindowAreaSessionPresenter) -> Unit,
        onEnded: () -> Unit,
    ) {
        controller()?.presentContentOnWindowArea(
            token,
            activity,
            mainExecutor,
            object : WindowAreaPresentationSessionCallback {
                override fun onSessionStarted(session: WindowAreaSessionPresenter) = onStarted(session)

                override fun onSessionEnded(t: Throwable?) = onEnded()

                override fun onContainerVisibilityChanged(isVisible: Boolean) = Unit
            },
        )
    }

    // A device without the extension answers with an empty controller; a broken one must not crash the app.
    private fun controller(): WindowAreaController? = runCatching { WindowAreaController.getOrCreate() }.getOrNull()
}

/** Hosts [content] in the window of a presentation session, with its own lifecycle and saved state. */
internal class PresentedContent(
    private val session: WindowAreaSessionPresenter,
    parent: CompositionContext,
    content: @androidx.compose.runtime.Composable () -> Unit,
) {
    private val owner = PresenterOwner()
    private val view =
        ComposeView(session.context).apply {
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setParentCompositionContext(parent)
            setContent(content)
        }

    init {
        session.setContentView(view)
        owner.resume()
    }

    fun close() {
        owner.destroy()
        view.disposeComposition()
        runCatching { (session as WindowAreaSession).close() }
    }
}

private class PresenterOwner :
    LifecycleOwner,
    SavedStateRegistryOwner {
    private val registry = LifecycleRegistry(this)
    private val controller = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = registry
    override val savedStateRegistry: SavedStateRegistry get() = controller.savedStateRegistry

    init {
        controller.performRestore(null)
    }

    fun resume() {
        registry.currentState = Lifecycle.State.RESUMED
    }

    fun destroy() {
        registry.currentState = Lifecycle.State.DESTROYED
    }
}

internal tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
