package com.jantursky.crease.samples.photos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseSlider
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.OutlinedPill
import kotlin.math.roundToInt

@Composable
fun AdjustmentPanel(
    session: EditorSession,
    modifier: Modifier = Modifier,
) {
    val adjustments = session.adjustments
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CreaseText("Adjust", style = CreaseTheme.type.kicker, modifier = Modifier.weight(1f))
            OutlinedPill("Reset", session::reset, enabled = !adjustments.isDefault)
        }
        AdjustmentRow("Exposure", adjustments.exposure) { v -> session.update { it.copy(exposure = v) } }
        AdjustmentRow("Contrast", adjustments.contrast) { v -> session.update { it.copy(contrast = v) } }
        AdjustmentRow("Saturation", adjustments.saturation) { v -> session.update { it.copy(saturation = v) } }
        AdjustmentRow("Warmth", adjustments.warmth) { v -> session.update { it.copy(warmth = v) } }
    }
}

@Composable
private fun AdjustmentRow(
    label: String,
    value: Float,
    onChange: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CreaseText(label, style = CreaseTheme.type.bodySmall, modifier = Modifier.width(80.dp))
        CreaseSlider(value, onChange, -1f..1f, Modifier.weight(1f), origin = 0f)
        CreaseText(
            text = (value * 100).roundToInt().toString(),
            style = CreaseTheme.type.numeric.copy(fontSize = CreaseTheme.type.bodySmall.fontSize),
            color = CreaseTheme.colors.slate,
            textAlign = TextAlign.End,
            modifier = Modifier.width(40.dp),
        )
    }
}
