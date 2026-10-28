package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.postures
import com.jantursky.crease.rememberFoldStateFlow
import com.jantursky.crease.rememberPostureTransition
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.StatusLabel

/** The posture before and after the latest change, and a count of changes taken from the fold state flow. */
@Composable
fun PostureChangesExample() {
    val transition = rememberPostureTransition()
    val foldState = rememberFoldStateFlow()
    var changes by remember { mutableIntStateOf(0) }
    // The first emission is the starting posture, so only later ones count as changes.
    LaunchedEffect(foldState) {
        var first = true
        foldState.postures().collect {
            if (first) first = false else changes++
        }
    }
    Column(
        Modifier.fillMaxSize().avoidFold().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StatusLabel(transition.current.name)
        CreaseText(
            text = if (transition.changed) "from ${transition.previous.name}" else "No change yet",
            style = CreaseTheme.type.bodySmall,
            color = CreaseTheme.colors.slate,
        )
        CreaseText(
            text = "$changes posture ${if (changes == 1) "change" else "changes"}",
            style = CreaseTheme.type.bodySmall,
            color = CreaseTheme.colors.slate,
        )
    }
}
