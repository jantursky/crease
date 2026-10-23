package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.FilledPill
import com.jantursky.crease.samples.design.OutlinedPill

/** A centred dialog that moves off the fold, so no button sits in the crease. */
@Composable
fun AvoidFoldExample() {
    Box(Modifier.fillMaxSize().background(CreaseTheme.colors.band)) {
        Box(Modifier.fillMaxSize().avoidFold(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .padding(16.dp)
                    .widthIn(max = 280.dp)
                    .clip(CreaseShapes.floating)
                    .background(CreaseTheme.colors.card)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CreaseText("Delete this photo?", style = CreaseTheme.type.title)
                CreaseText(
                    "It stays in Recently deleted for 30 days.",
                    style = CreaseTheme.type.bodySmall,
                    color = CreaseTheme.colors.slate,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedPill("Cancel", onClick = {})
                    FilledPill("Delete", onClick = {})
                }
            }
        }
    }
}
