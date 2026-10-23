package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.TwoPaneFallback
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme

private class Note(
    val title: String,
    val body: String,
)

private val notes =
    listOf(
        Note("Groceries", "Tomatoes, basil, a good olive oil and two lemons."),
        Note("Trip", "Train at 8:40. Seats 41 and 42, carriage 6."),
        Note("Ideas", "A reading lamp that dims as the hinge closes."),
    )

/** A list and its detail: one pane on a flat phone, both panes either side of the fold. */
@Composable
fun ListDetailExample() {
    var selected by remember { mutableStateOf(notes.first()) }
    FoldAwareTwoPane(
        first = { NoteList(selected, onSelect = { selected = it }) },
        second = { NoteDetail(selected) },
        fallback = TwoPaneFallback.FirstOnly,
    )
}

@Composable
private fun NoteList(
    selected: Note,
    onSelect: (Note) -> Unit,
) {
    val colors = CreaseTheme.colors
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        notes.forEach { note ->
            CreaseText(
                text = note.title,
                color = if (note == selected) colors.ink else colors.slate,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(CreaseShapes.floating)
                        .background(if (note == selected) colors.band else colors.canvas)
                        .clickable { onSelect(note) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun NoteDetail(note: Note) {
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CreaseText(note.title, style = CreaseTheme.type.title)
        CreaseText(note.body, color = CreaseTheme.colors.slate)
    }
}
