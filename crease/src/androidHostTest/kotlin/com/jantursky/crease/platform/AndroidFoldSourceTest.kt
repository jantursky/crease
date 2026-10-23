package com.jantursky.crease.platform

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Looper
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.jantursky.crease.Hinge
import com.jantursky.crease.HingeStatus
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.SensorEventBuilder
import org.robolectric.shadows.ShadowSensor
import kotlin.test.assertEquals
import kotlin.test.assertSame

private class TestLifecycleOwner : LifecycleOwner {
    val registry: LifecycleRegistry = LifecycleRegistry.createUnsafe(this)
    override val lifecycle: Lifecycle get() = registry
}

@RunWith(RobolectricTestRunner::class)
class AndroidFoldSourceTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
    private val sensors = shadowOf(activity.getSystemService(SensorManager::class.java))
    private val hingeSensor = ShadowSensor.newInstance(Sensor.TYPE_HINGE_ANGLE)
    private val owner = TestLifecycleOwner()
    private val first = FrameLayout(activity)
    private val second = FrameLayout(activity)

    init {
        sensors.addSensor(hingeSensor)
        activity.setContentView(FrameLayout(activity).apply { addView(first) })
        (first.parent as FrameLayout).addView(second)
        first.rootView.setViewTreeLifecycleOwner(owner)
        owner.registry.currentState = Lifecycle.State.CREATED
    }

    private fun angle(degrees: Float) = SensorEventBuilder.newBuilder(hingeSensor, floatArrayOf(degrees)).build()

    private fun idle() = shadowOf(Looper.getMainLooper()).idle()

    @Test
    fun listensToTheHingeOnlyWhileStarted() {
        val source = AndroidFoldSource.acquire(first)
        assertEquals(0, sensors.listeners.size)
        owner.registry.currentState = Lifecycle.State.RESUMED
        assertEquals(1, sensors.listeners.size)
        owner.registry.currentState = Lifecycle.State.CREATED
        assertEquals(0, sensors.listeners.size)
        owner.registry.currentState = Lifecycle.State.STARTED
        assertEquals(1, sensors.listeners.size)
        source.release()
        assertEquals(0, sensors.listeners.size)
    }

    @Test
    fun oneSourcePerWindowWhateverTheNumberOfReaders() {
        owner.registry.currentState = Lifecycle.State.STARTED
        val a = AndroidFoldSource.acquire(first)
        val b = AndroidFoldSource.acquire(second)
        val c = AndroidFoldSource.acquire(first)
        assertSame(a, b)
        assertSame(a, c)
        assertEquals(1, sensors.listeners.size)
        a.release()
        b.release()
        assertEquals(1, sensors.listeners.size)
        c.release()
        assertEquals(0, sensors.listeners.size)
        // Released for good: the next reader gets a fresh source.
        val fresh = AndroidFoldSource.acquire(first)
        assertEquals(1, sensors.listeners.size)
        fresh.release()
    }

    @Test
    fun hingeAngleUpdatesTheState() {
        owner.registry.currentState = Lifecycle.State.STARTED
        val source = AndroidFoldSource.acquire(first)
        idle()
        sensors.sendSensorEventToListeners(angle(95f))
        assertEquals(Hinge(HingeStatus.PartiallyOpen, 95f), source.state.value.hinge)
        sensors.sendSensorEventToListeners(angle(2f))
        assertEquals(Hinge(HingeStatus.Closed, 2f), source.state.value.hinge)
        source.release()
    }

    @Test
    fun withoutALifecycleOwnerItRunsWhileItHasReaders() {
        val other = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = FrameLayout(other)
        other.setContentView(view)
        val source = AndroidFoldSource.acquire(view)
        assertEquals(1, sensors.listeners.size)
        source.release()
        assertEquals(0, sensors.listeners.size)
    }
}
