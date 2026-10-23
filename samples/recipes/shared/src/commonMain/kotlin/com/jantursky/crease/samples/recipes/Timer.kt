package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme

@Composable
fun TimerReadout(
    session: CookingSession,
    modifier: Modifier = Modifier,
    style: TextStyle = CreaseTheme.type.heading,
) {
    val colors = CreaseTheme.colors
    Column(modifier) {
        CreaseText(
            text = if (session.hasTimer) formatClock(session.remainingSeconds) else "No timer",
            style = style.copy(fontFeatureSettings = "tnum"),
            color = if (session.hasTimer) colors.ink else colors.slate,
        )
        Canvas(Modifier.fillMaxWidth().height(4.dp)) {
            val corner = CornerRadius(size.height / 2)
            drawRoundRect(colors.control, Offset.Zero, size, corner)
            drawRoundRect(colors.ink, Offset.Zero, Size(size.width * session.progress, size.height), corner)
        }
    }
}
