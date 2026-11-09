package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.cover.CoverDisplay
import com.jantursky.crease.cover.CoverDisplayState
import com.jantursky.crease.cover.ProvideCoverDisplayState
import com.jantursky.crease.cover.rememberCoverDisplayState
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.SegmentedControl
import com.jantursky.crease.samples.design.StatusLabel

private val States =
    listOf(
        CoverDisplayState.Unsupported,
        CoverDisplayState.Unavailable,
        CoverDisplayState.Available,
        CoverDisplayState.Active,
    )

/**
 * What an app renders for each [CoverDisplayState]. A browser has no outer display, so the state is
 * chosen here with [ProvideCoverDisplayState], and [CoverDisplay] composes nothing under it.
 */
@Composable
fun CoverDisplayExample() {
    var selected by remember { mutableStateOf<CoverDisplayState>(CoverDisplayState.Available) }
    Column(
        Modifier.fillMaxSize().avoidFold().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SegmentedControl(
            options = States,
            selected = selected,
            onSelect = { selected = it },
            modifier = Modifier.fillMaxWidth(),
            label = { it.toString() },
        )
        ProvideCoverDisplayState(selected) { CoverPanel() }
    }
}

@Composable
private fun CoverPanel() {
    val state = rememberCoverDisplayState()
    // Nothing is composed here: with a provided state there is no outer display to show it on.
    CoverDisplay { CreaseText("Shown on the outer display") }
    StatusLabel(state.toString())
    CreaseText(
        text =
            when (state) {
                CoverDisplayState.Unsupported -> "No outer display to use. Leave the option out."
                CoverDisplayState.Unavailable -> "Possible later. Say so, and keep the main screen complete."
                CoverDisplayState.Available -> "The system would show it. Offer to put the content there."
                CoverDisplayState.Active -> "The content is on the outer display. The main screen carries on."
            },
        style = CreaseTheme.type.bodySmall,
        color = CreaseTheme.colors.slate,
    )
}
