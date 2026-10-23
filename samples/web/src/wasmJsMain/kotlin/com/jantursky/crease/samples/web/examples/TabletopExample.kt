package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jantursky.crease.TabletopLayout
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseSlider
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.FilledPill

/** A player: the video above the fold and the controls below it, or stacked when flat. */
@Composable
fun TabletopExample() {
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableFloatStateOf(0.3f) }
    val video = @Composable { Video(playing) }
    val controls = @Composable { Controls(playing, { playing = !playing }, position, { position = it }) }
    TabletopLayout(
        top = { Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) { video() } },
        bottom = { controls() },
        flat = {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                video()
                controls()
            }
        },
    )
}

@Composable
private fun Video(playing: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(CreaseShapes.floating)
            .background(CreaseTheme.colors.control),
        contentAlignment = Alignment.Center,
    ) {
        CreaseText(if (playing) "Playing" else "Paused", color = CreaseTheme.colors.slate)
    }
}

@Composable
private fun Controls(
    playing: Boolean,
    onToggle: () -> Unit,
    position: Float,
    onSeek: (Float) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CreaseSlider(position, onSeek, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FilledPill(if (playing) "Pause" else "Play", onToggle)
            CreaseText("Kitchen basics, part 2", style = CreaseTheme.type.bodySmall, color = CreaseTheme.colors.slate)
        }
    }
}
