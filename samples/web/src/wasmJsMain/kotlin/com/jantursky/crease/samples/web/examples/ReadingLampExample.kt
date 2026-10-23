package com.jantursky.crease.samples.web.examples

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jantursky.crease.HingeStatus
import com.jantursky.crease.avoidFold
import com.jantursky.crease.rememberHinge
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import kotlin.math.roundToInt

/** A reading lamp that dims as the hinge closes, and goes out when the device shuts. */
@Composable
fun ReadingLampExample() {
    val hinge = rememberHinge()
    val target =
        when {
            hinge == null -> 1f
            hinge.status == HingeStatus.Closed -> 0f
            else -> ((hinge.angle ?: 180f) / 180f).coerceIn(0f, 1f)
        }
    val brightness by animateFloatAsState(target)
    val glow = Color(0xFFFFC85A)
    Box(
        Modifier.fillMaxSize().background(CreaseTheme.colors.canvas),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.avoidFold(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Canvas(Modifier.size(140.dp)) {
                drawCircle(glow.copy(alpha = 0.25f * brightness), radius = size.minDimension / 2)
                drawCircle(glow.copy(alpha = 0.2f + 0.8f * brightness), radius = size.minDimension / 5)
            }
            CreaseText("${(brightness * 100).roundToInt()} %", style = CreaseTheme.type.numeric)
        }
    }
}
