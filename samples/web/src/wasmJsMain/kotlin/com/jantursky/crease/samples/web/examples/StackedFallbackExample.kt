package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.TwoPaneFallback
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme

private val week = listOf("Mon" to 0.4f, "Tue" to 0.7f, "Wed" to 0.55f, "Thu" to 0.9f, "Fri" to 0.65f)

/** A chart and its numbers: side by side across a fold, stacked when there is none. */
@Composable
fun StackedFallbackExample() {
    FoldAwareTwoPane(
        first = { Chart() },
        second = { Numbers() },
        fallback = TwoPaneFallback.Stacked,
    )
}

@Composable
private fun Chart() {
    Row(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        week.forEach { (_, value) ->
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(value)
                    .clip(CreaseShapes.link)
                    .background(CreaseTheme.colors.link),
            )
        }
    }
}

@Composable
private fun Numbers() {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CreaseText("Steps this week", style = CreaseTheme.type.title)
        week.forEach { (day, value) ->
            Row(Modifier.fillMaxWidth()) {
                CreaseText(day, color = CreaseTheme.colors.slate, modifier = Modifier.weight(1f))
                CreaseText("${(value * 12_000).toInt()}", style = CreaseTheme.type.numeric)
            }
        }
    }
}
