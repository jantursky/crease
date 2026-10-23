package com.jantursky.crease.samples.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.Posture
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SimulatorControls(
    state: SimulatorState,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Labelled("Posture") {
            SegmentedControl(
                options = Posture.entries,
                selected = state.posture,
                onSelect = { state.posture = it },
                modifier = Modifier.width(320.dp),
            )
        }
        Labelled("Hinge angle") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CreaseSlider(
                    value = state.angle,
                    onValueChange = { state.angle = it },
                    valueRange = SimulatorState.MIN_ANGLE..SimulatorState.MAX_ANGLE,
                    enabled = state.isHalfOpen,
                    modifier = Modifier.width(200.dp),
                )
                CreaseText(
                    text = "${state.displayAngle().roundToInt()}°",
                    style = CreaseTheme.type.numeric,
                    color = CreaseTheme.colors.slate,
                    modifier = Modifier.width(56.dp),
                )
            }
        }
        Labelled("Fold width") {
            SegmentedControl(
                options = listOf(0, 40),
                selected = state.foldWidthDp,
                onSelect = { state.foldWidthDp = it },
                label = { if (it == 0) "Seamless" else "$it dp" },
                modifier = Modifier.width(180.dp),
            )
        }
        Labelled("Show") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TogglePill("Camera cutout", state.cutout, { state.cutout = !state.cutout })
                TogglePill("Fold overlay", state.overlay, { state.overlay = !state.overlay })
            }
        }
    }
}

private fun SimulatorState.displayAngle(): Float =
    when (posture) {
        Posture.Closed -> 0f
        Posture.Flat -> 180f
        else -> angle
    }

@Composable
private fun Labelled(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CreaseText(label, style = CreaseTheme.type.caption, color = CreaseTheme.colors.slate)
        content()
    }
}
