package com.jantursky.crease.samples.web

import com.jantursky.crease.Posture
import com.jantursky.crease.samples.design.SimulatorState
import kotlinx.browser.window

/** Optional deep link, e.g. `?demo=photos&posture=tabletop&angle=100&overlay`. */
internal class StartParams(
    val demo: Demo,
    private val posture: Posture?,
    private val angle: Float?,
    private val overlay: Boolean,
) {
    private var applied = false

    fun applyTo(state: SimulatorState) {
        if (applied) return
        applied = true
        posture?.let { state.posture = it }
        angle?.let { state.angle = it }
        if (overlay) state.overlay = true
    }

    companion object {
        fun fromUrl(): StartParams = parse(window.location.search)

        fun parse(query: String): StartParams {
            val values =
                query.removePrefix("?").split('&').filter { it.isNotEmpty() }.associate {
                    it.substringBefore('=') to it.substringAfter('=', "")
                }
            return StartParams(
                demo = Demo.entries.firstOrNull { it.label.equals(values["demo"], ignoreCase = true) } ?: Demo.Recipes,
                posture = Posture.entries.firstOrNull { it.name.equals(values["posture"], ignoreCase = true) },
                angle = values["angle"]?.toFloatOrNull(),
                overlay = "overlay" in values,
            )
        }
    }
}
