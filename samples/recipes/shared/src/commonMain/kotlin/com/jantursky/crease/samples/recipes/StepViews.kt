package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseCard
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.FilledPill
import com.jantursky.crease.samples.design.OutlinedPill

internal fun CookingSession.stepKicker(): String = "Step ${stepIndex + 1} of ${recipe.steps.size}"

@Composable
fun StepControls(
    session: CookingSession,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedPill("Previous", session::previous, enabled = session.hasPrevious)
        FilledPill(
            text = if (session.isRunning) "Pause" else "Start timer",
            onClick = session::toggleTimer,
            enabled = session.hasTimer,
        )
        OutlinedPill("Next", session::next, enabled = session.hasNext)
    }
}

/** The current step as a card. Not [standalone], it only adds text and controls to a heading shown elsewhere. */
@Composable
fun StepCard(
    session: CookingSession,
    modifier: Modifier = Modifier,
    standalone: Boolean = true,
) {
    val colors = CreaseTheme.colors
    val type = CreaseTheme.type
    CreaseCard(modifier = modifier.fillMaxSize(), padding = 24.dp) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (standalone) {
                CreaseText(session.recipe.shortTitle, style = type.caption, color = colors.slate)
                CreaseText(session.stepKicker(), style = type.label, color = colors.signal)
                CreaseText(session.step.title, style = type.title)
            }
            CreaseText(session.step.text, color = colors.slate)
        }
        if (standalone) {
            TimerReadout(session, Modifier.padding(top = 16.dp), style = type.title)
        }
        StepControls(session, Modifier.padding(top = 16.dp))
    }
}

/** Tabletop's upper half: a video placeholder and the countdown, easy to read from across the counter. */
@Composable
fun StepFocus(
    session: CookingSession,
    modifier: Modifier = Modifier,
) {
    val colors = CreaseTheme.colors
    val type = CreaseTheme.type
    CreaseCard(modifier = modifier.fillMaxSize(), padding = 24.dp) {
        CreaseText(session.stepKicker(), style = type.label, color = colors.signal)
        CreaseText(session.step.title, style = type.title)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(CreaseShapes.floating)
                .background(colors.band),
            contentAlignment = Alignment.Center,
        ) {
            PlayGlyph()
            CreaseText(
                "Technique video",
                style = type.caption,
                color = colors.slate,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            )
        }
        TimerReadout(session, Modifier.widthIn(max = 640.dp), style = type.heading)
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun PlayGlyph() {
    val colors = CreaseTheme.colors
    Canvas(
        Modifier
            .padding(24.dp)
            .height(64.dp)
            .widthIn(max = 64.dp)
            .fillMaxWidth(),
    ) {
        drawCircle(colors.steel, size.minDimension / 2, style = Stroke(1.dp.toPx()))
        val triangle =
            Path().apply {
                moveTo(size.width * 0.4f, size.height * 0.3f)
                lineTo(size.width * 0.72f, size.height * 0.5f)
                lineTo(size.width * 0.4f, size.height * 0.7f)
                close()
            }
        drawPath(triangle, colors.ink)
    }
}
