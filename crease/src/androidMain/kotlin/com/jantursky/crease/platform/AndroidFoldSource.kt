package com.jantursky.crease.platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.window.layout.DisplayFeature
import androidx.window.layout.WindowInfoTracker
import com.jantursky.crease.FoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * The fold state of one window, shared by its readers under one lifecycle. It collects display features
 * from WindowManager, the hinge angle sensor and the display cutouts, but only while that lifecycle is
 * at least started. Everything runs on the main thread.
 */
internal class AndroidFoldSource private constructor(
    private val rootView: View,
    private val lifecycle: Lifecycle?,
) : DefaultLifecycleObserver {
    private val current = mutableStateOf(FoldState.None)
    val state: State<FoldState> get() = current

    private val activity = rootView.context.findActivity()
    private val sensorManager = rootView.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    private var users = 0
    private var scope: CoroutineScope? = null
    private var features: List<DisplayFeature> = emptyList()
    private var angle: Float? = null
    private var cutouts: List<Rect> = emptyList()

    private val angleListener =
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                angle = event.values.firstOrNull()
                report()
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int,
            ) = Unit
        }

    // Rotation and window moves lay the root view out again; both can move the cutouts and the fold.
    private val layoutListener =
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            readCutouts()
            report()
        }

    override fun onStart(owner: LifecycleOwner) = start()

    override fun onStop(owner: LifecycleOwner) = stop()

    private fun start() {
        if (scope != null) return
        val started = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        scope = started
        activity?.let { activity ->
            started.launch {
                WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity).collect {
                    features = it.displayFeatures
                    report()
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sensorManager?.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)?.let {
                sensorManager.registerListener(angleListener, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
        rootView.addOnLayoutChangeListener(layoutListener)
        readCutouts()
        report()
    }

    private fun stop() {
        val started = scope ?: return
        scope = null
        started.cancel()
        sensorManager?.unregisterListener(angleListener)
        rootView.removeOnLayoutChangeListener(layoutListener)
    }

    private fun readCutouts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        cutouts =
            rootView.rootWindowInsets
                ?.displayCutout
                ?.boundingRects
                .orEmpty()
                .map { it.toComposeRect() }
    }

    private fun report() {
        current.value = androidFoldState(features, angle, cutouts, activityWindowOffset())
    }

    /** Where the activity's window sits relative to this one; zero unless this is a dialog or popup. */
    private fun activityWindowOffset(): Offset {
        val activityRoot = activity?.window?.peekDecorView()
        if (activityRoot == null || activityRoot === rootView) return Offset.Zero
        val activityOnScreen = IntArray(2).also { activityRoot.getLocationOnScreen(it) }
        val ownOnScreen = IntArray(2).also { rootView.getLocationOnScreen(it) }
        return Offset(
            (activityOnScreen[0] - ownOnScreen[0]).toFloat(),
            (activityOnScreen[1] - ownOnScreen[1]).toFloat(),
        )
    }

    /** Gives up one reader; the last one stops the source and forgets it. */
    fun release() {
        if (--users > 0) return
        lifecycle?.removeObserver(this)
        stop()
        sources.remove(rootView to lifecycle)
    }

    companion object {
        // Keyed by window and lifecycle: fragments in one window can be started and stopped apart.
        // Entries leave with their last reader, so the map holds nothing that is no longer in use.
        private val sources = HashMap<Pair<View, Lifecycle?>, AndroidFoldSource>()

        /** The source for the window holding [view], with one more reader. Pair with [release]. */
        fun acquire(view: View): AndroidFoldSource {
            val rootView = view.rootView
            val source =
                view.findViewTreeLifecycleOwner()?.lifecycle.let { lifecycle ->
                    sources.getOrPut(rootView to lifecycle) { AndroidFoldSource(rootView, lifecycle) }
                }
            if (source.users++ == 0) {
                // Without a lifecycle there is nothing to pause on, so the source runs while it has readers.
                source.lifecycle?.addObserver(source) ?: source.start()
            }
            return source
        }
    }
}

internal tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
