package com.jantursky.crease.samples.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 4 dp `control` track, `ink` active part, 20 dp `card` thumb with a hairline ring. The active part
 * runs from [origin], which is the start of the range unless the slider is centered on a value.
 */
@Composable
fun CreaseSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    origin: Float = valueRange.start,
) {
    val colors = CreaseTheme.colors
    val currentOnChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableIntStateOf(0) }
    val span = valueRange.endInclusive - valueRange.start
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    val originFraction = ((origin - valueRange.start) / span).coerceIn(0f, 1f)

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(28.dp)
                .onSizeChanged { widthPx = it.width }
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange)
                    setProgress { target ->
                        currentOnChange(target.coerceIn(valueRange.start, valueRange.endInclusive))
                        true
                    }
                }.pointerInput(enabled, valueRange, widthPx) {
                    if (!enabled) return@pointerInput
                    val inset = 10.dp.toPx()
                    val usable = (widthPx - 2 * inset).coerceAtLeast(1f)

                    fun valueAt(x: Float) = valueRange.start + ((x - inset) / usable).coerceIn(0f, 1f) * span
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        currentOnChange(valueAt(down.position.x))
                        drag(down.id) {
                            currentOnChange(valueAt(it.position.x))
                            it.consume()
                        }
                    }
                },
    ) {
        val inset = 10.dp.toPx()
        val usable = size.width - 2 * inset
        val trackHeight = 4.dp.toPx()
        val centerY = size.height / 2
        val thumbX = inset + usable * fraction
        val corner = CornerRadius(trackHeight / 2)
        drawRoundRect(
            colors.control,
            Offset(inset, centerY - trackHeight / 2),
            Size(usable, trackHeight),
            corner,
        )
        drawRoundRect(
            if (enabled) colors.ink else colors.steel,
            Offset(inset + usable * minOf(originFraction, fraction), centerY - trackHeight / 2),
            Size(usable * abs(fraction - originFraction), trackHeight),
            corner,
        )
        drawCircle(colors.card, 10.dp.toPx(), Offset(thumbX, centerY))
        drawCircle(colors.hairline, 10.dp.toPx(), Offset(thumbX, centerY), style = Stroke(1.dp.toPx()))
    }
}
