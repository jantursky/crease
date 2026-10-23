package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.FilledPill

/** A floating summary of the current step. It floats over both panes, so it must not sit on the fold. */
@Composable
fun NowCookingBar(
    session: CookingSession,
    modifier: Modifier = Modifier,
) {
    val colors = CreaseTheme.colors
    Row(
        modifier =
            modifier
                .clip(CreaseShapes.floating)
                .background(colors.raised)
                .border(1.dp, colors.hairline, CreaseShapes.floating)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            CreaseText("Now cooking", style = CreaseTheme.type.caption, color = colors.slate)
            CreaseText(session.step.title, style = CreaseTheme.type.body, maxLines = 1)
        }
        CreaseText(
            if (session.hasTimer) formatClock(session.remainingSeconds) else "",
            style = CreaseTheme.type.numeric,
            color = colors.slate,
        )
        FilledPill(if (session.isRunning) "Pause" else "Start", session::toggleTimer, enabled = session.hasTimer)
    }
}
